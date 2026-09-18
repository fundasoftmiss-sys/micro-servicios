# Tienda Microservicios — el resultado de migrar `tienda-monolito`

**Curso:** Microservicios Essentials (MICR_ESS) · Módulo 2 · Práctica 2

Este no es un proyecto nuevo. Es **el mismo `tienda-monolito`** después de recorrer las seis fases
del patrón *Strangler Fig*: se le fueron extrayendo módulos hasta que lo que quedó era, exactamente,
un microservicio.

```
tienda-monolito 1.0.0                    tienda-microservicios
┌──────────────────────────┐             ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Productos                │  ─────────► │ ms-productos │ │ ms-clientes  │ │  ms-pedidos  │
│ Clientes                 │             │    :8081     │ │    :8082     │ │    :8080     │
│ Pedidos                  │             │ productosdb  │ │  clientesdb  │ │  pedidosdb   │
│ 1 proceso · 1 BD · 1 JAR │             └──────────────┘ └──────────────┘ └──────────────┘
└──────────────────────────┘                                                 ↑ era el monolito
```

Ademas, `ms-ventas` (`:8083`) genera una venta y solicita a `ms-pedidos` que
registre el pedido asociado. `ms-pedidos` conserva `ventaId` y `fechaHoraVenta`
para relacionar ambos registros.

## Requisitos

- JDK 17 o superior
- Maven 3.8+
- Cuatro terminales libres (o usar el script de arranque)

## Arrancar

```bat
arrancar-todo.bat        :: Windows
./arrancar-todo.sh       # Linux / macOS / Git Bash
```

O a mano, **en este orden** (ms-pedidos necesita a los otros dos para crear sus pedidos de ejemplo):

```bash
cd ms-productos && mvn spring-boot:run     # 8081
cd ms-clientes  && mvn spring-boot:run     # 8082
cd ms-pedidos   && mvn spring-boot:run     # 8080
cd ms-ventas    && mvn spring-boot:run     # 8083
```

Abrir **http://localhost:8080**

| URL | Servicio | Qué es |
|---|---|---|
| `http://localhost:8080` | ms-pedidos | Inicio, con los contadores de los tres servicios |
| `http://localhost:8080/pedidos` | ms-pedidos | Crear y listar pedidos (la vista del monolito) |
| `http://localhost:8080/api/pedidos` | ms-pedidos | JSON de pedidos |
| `http://localhost:8081/productos` | ms-productos | JSON del catálogo |
| `http://localhost:8081/productos/reservas` | ms-productos | **El rastro de la saga** |
| `http://localhost:8082/clientes` | ms-clientes | JSON de clientes |
| `http://localhost:8083/api/ventas` | ms-ventas | Crear y listar ventas |
| `.../h2-console` en 8080, 8081, 8082 y 8083 | los cuatro | **Cuatro bases de datos distintas** |

---

### Crear una venta

`ms-ventas` genera el ID y la fecha/hora, guarda la venta y llama a
`ms-pedidos` para ejecutar la saga de reserva y registrar el pedido:

```bash
curl -X POST http://localhost:8083/api/ventas \
   -H "Content-Type: application/json" \
   -d '{"clienteId":1,"productoId":1,"cantidad":1}'
```

La respuesta incluye `id`, `fechaHora` y `pedidoId`. El pedido correspondiente
se puede consultar en `http://localhost:8080/api/pedidos` y contiene los mismos
`ventaId` y `fechaHoraVenta`.

## Qué mirar en el código

### 1. `ms-pedidos` ES el antiguo monolito

Compara `PedidoController` y la vista `pedidos.html` con los del monolito: son los mismos.
El `pom.xml` lo dice explícitamente: mismo `groupId`, artefacto renombrado, versión 2.0.0.
**Nunca se borró nada: se adelgazó.**

### 2. Las llaves foráneas desaparecieron

| `tienda-monolito` | `ms-pedidos` |
|---|---|
| `@ManyToOne private Cliente cliente;` | `private Long clienteId;` + `private String clienteNombre;` |
| `@ManyToOne private Producto producto;` | `private Long productoId;` + `productoNombre` + `precioUnitario` |

Las copias son **deliberadas**. Un pedido es un documento de lo que ocurrió, no una vista de los
datos de hoy: si mañana sube el precio del monitor, ese pedido debe seguir mostrando lo que se cobró.

### 3. El puerto sobrevivió al cambio de implementación

`CatalogoPort` (en `port/`) es el contrato de negocio: solo `buscar`, `reservar`, `confirmar` y
`liberar`. No tiene `listar()` ni `guardar()` — crear productos es asunto del catálogo.
`CatalogoHttp` lo implementa por red, y **`PedidoService` no sabe que existe HTTP**.

> `ConsultaCatalogoPort` está aparte a propósito: el desplegable del formulario es una lectura de
> interfaz de usuario, no una dependencia de negocio. En un sistema mayor viviría en un BFF o en el
> gateway, no dentro de `ms-pedidos`.

### 4. La transacción se convirtió en saga

`PedidoService.crear()` — compara con el del monolito:

```java
String opId = UUID.randomUUID().toString();   // clave de idempotencia
catalogo.reservarStock(productoId, cantidad, opId);   // 1. reservar
try {
    Pedido pedido = registro.guardar(cliente, producto, cantidad);   // 2. tx LOCAL
    catalogo.confirmarReserva(opId);                                 // 3. confirmar
    return pedido;
} catch (RuntimeException e) {
    catalogo.liberarReserva(opId);            // COMPENSAR
    throw e;
}
```

Tres detalles que en clase merecen una pausa:

- **`opId` se genera antes de la primera escritura.** La red entrega el mismo mensaje dos veces con
  toda naturalidad. `ProductoService.reservar()` comprueba si ese `opId` ya existe y no descuenta
  dos veces.
- **Reservar va antes de guardar.** Al revés, podrías crear un pedido que nadie puede servir.
- **`@Transactional` no está sobre `crear()`**, sino en `RegistroPedidos`, una clase aparte.
  Mantener abierta una transacción de base de datos mientras esperas a otro servicio es de las
  peores cosas que se pueden hacer en un sistema distribuido — y si la anotación estuviera en un
  método privado del mismo bean, Spring ni siquiera la aplicaría.

### 5. Apareció código que en el monolito era impensable

- `RestClientConfig` — **timeouts**. Sin ellos, un `ms-productos` lento agota el pool de hilos de
  `ms-pedidos` y tumba al servicio que sí funcionaba. Es el fallo número uno al extraer el primero.
- `ManejadorDeErrores` — traducir excepciones a códigos HTTP, para que el otro lado distinga
  "no existe" (404) de "no hay stock" (409) de "estoy caído".
- `ServicioNoDisponibleException` — un fallo que una llamada de método no puede producir.
- `Reserva` — una entidad entera que solo existe porque la transacción ya no alcanza.

---

## Guion de demo sugerido (≈40 min)

| Min | Qué hacer | Qué señalar |
|---|---|---|
| 0–5 | Abrir las tres consolas H2 (8080, 8081, 8082) | Tres bases de datos. `PEDIDO` no tiene FK a nada |
| 5–12 | Crear un pedido desde `/pedidos` | Una acción del usuario = 4 llamadas de red |
| 12–18 | Abrir `http://localhost:8081/productos/reservas` | El rastro de la saga: `RESERVADA` → `CONFIRMADA` |
| 18–24 | **Pedir 5 monitores** (solo hay 3) | El error nace en 8081, viaja como HTTP 409, lo pinta 8080. Y en `/reservas` **no** queda basura: no llegó a reservar |
| 24–32 | **Cerrar la ventana de `ms-productos`** y recargar `/pedidos` | El histórico se sigue viendo (copias locales), el formulario se deshabilita. Aislamiento de fallos |
| 32–38 | Volver a levantar `ms-productos` y recargar | Se recupera solo. El stock volvió a 4 productos: **su BD se recreó**, la de pedidos no |
| 38–40 | Pregunta al grupo | ¿Mereció la pena? ¿Qué se ganó y qué se perdió? |

### La demo que mejor funciona

Cerrar `ms-productos` y recargar `http://localhost:8080/pedidos`. Pasan dos cosas a la vez:

1. La tabla de pedidos **sigue funcionando** — porque en la fase 2 decidimos copiar el nombre y el
   precio en lugar de consultarlos.
2. El formulario **se deshabilita** con un aviso — porque para crear un pedido sí hacen falta los
   otros servicios.

Esa es, en una sola pantalla, la diferencia entre acoplamiento de datos y acoplamiento de proceso.

---

## Ejercicios para los estudiantes

1. **Rompe la compensación.** Comenta la línea `catalogo.liberarReserva(opId)` en `PedidoService`.
   Provoca un fallo después de la reserva (por ejemplo, pon `productoNombre` a `null` en
   `RegistroPedidos` con la columna marcada `nullable = false`). Comprueba en `/productos/reservas`
   que queda una reserva huérfana y que el stock nunca vuelve. **Eso es lo que el monolito hacía
   gratis.**

2. **Comprueba la idempotencia.** Lanza dos veces la misma reserva con el mismo `opId`:
   ```bash
   curl -X POST http://localhost:8081/productos/reservas \
        -H "Content-Type: application/json" \
        -d '{"opId":"prueba-1","productoId":1,"cantidad":2}'
   ```
   El stock solo baja una vez. Cambia el `opId` y baja otra vez.

3. **Provoca un timeout.** Baja `servicios.timeout.lectura-ms` a `1` en
   `ms-pedidos/application.properties` y crea un pedido. Observa que el error es distinto al de
   "servicio caído", y piensa qué debería hacer el sistema en ese caso: ¿reintentar? ¿y si la
   reserva sí llegó a hacerse?

4. **Añade `categoria` a `Producto`.** Compara con el ejercicio del monolito: aquí solo se recompila
   y redespliega `ms-productos`; los otros dos servicios ni se enteran. Ése es el beneficio que se
   compró — y el ejercicio 1 fue el precio que se pagó.

---

## Lo que este ejemplo NO tiene (y un sistema real sí)

Se han dejado fuera a propósito, para que el código siga siendo legible en clase:

- **API Gateway** — hoy el navegador habla directamente con los tres puertos.
- **Service discovery** — las URLs están fijas en `application.properties`.
- **Circuit breaker y reintentos** — hay timeouts, pero no Resilience4j.
- **Trazas distribuidas** — sin `traceId`, seguir un pedido entre tres logs es a mano.
- **Mensajería asíncrona** — la saga es síncrona (coreografía por HTTP). Con Kafka o RabbitMQ sería
  más robusta, y bastante menos evidente de leer.
- **Contenedores** — sin Docker ni Compose; se arranca con Maven.

Cada una de esas ausencias es un tema del temario. **Y que la lista sea tan larga es, en sí misma,
el argumento más honesto a favor de empezar por un monolito.**

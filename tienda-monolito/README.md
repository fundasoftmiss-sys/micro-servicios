# Tienda Monolito — Ejemplo de Arquitectura Monolítica
**Curso:** Microservicios Essentials (MICR_ESS) · Módulo 1 · Instructor: Roberto Cerquera

Aplicación Spring Boot 3 mínima que muestra un **monolito clásico en capas**: tres módulos de negocio
(Productos, Clientes, Pedidos) dentro de **una sola aplicación**, un solo proceso, un solo puerto y una sola base de datos.

## Requisitos
- JDK 17 o superior
- Maven 3.8+ (o usar el IDE: IntelliJ / Eclipse / VS Code con extensiones de Java)

## Ejecutar
```bash
mvn spring-boot:run
```
o construir el JAR único (el "artefacto monolítico"):
```bash
mvn clean package
java -jar target/tienda-monolito-1.0.0.jar
```
Abrir: **http://localhost:8080**

| URL | Qué es |
|---|---|
| `/` | Inicio: contadores y explicación de las capas |
| `/productos` | Módulo de catálogo (listar y crear) |
| `/clientes` | Módulo de clientes (listar y crear) |
| `/pedidos` | Módulo de ventas (crear pedido descuenta stock) |
| `/api/productos`, `/api/clientes`, `/api/pedidos` | Los mismos datos como JSON |
| `/h2-console` | Consola de la BD. JDBC URL: `jdbc:h2:mem:tiendadb`, usuario `sa`, sin contraseña |

## Estructura
```
src/main/java/com/netec/tienda
├── TiendaMonolitoApplication.java   ← punto de entrada ÚNICO
├── model/        Producto, Cliente, Pedido        (entidades JPA)
├── repository/   *Repository                      (acceso a datos)
├── service/      *Service                         (reglas de negocio)
├── controller/   *Controller + ApiController      (web MVC + REST)
└── config/       DatosIniciales                   (datos de ejemplo)
src/main/resources
├── application.properties            ← UNA base de datos para todo
└── templates/*.html                  ← vistas Thymeleaf
```

## Guion sugerido de la demo (≈45 min)

| Min | Qué hacer | Qué señalar |
|---|---|---|
| 0–5 | Mostrar la estructura de carpetas en el IDE | Un solo proyecto, un solo `pom.xml`, capas MVC que ya conocen |
| 5–10 | Abrir `TiendaMonolitoApplication` y `application.properties` | Un `main`, un puerto, una BD |
| 10–20 | Ejecutar y navegar `/`, `/productos`, `/clientes`, `/pedidos` | Todo responde desde el mismo puerto 8080 |
| 20–28 | Crear un pedido válido y luego pedir 5 monitores (hay 3) | El error viene de `ProductoService` pero lo muestra `PedidoController`: acoplamiento en memoria |
| 28–35 | Abrir `PedidoService.crear()` | Llama a `ClienteService` y `ProductoService` directamente; `@Transactional` sobre una sola BD. Ventaja: simple y consistente. Desventaja: pedidos no vive sin los otros |
| 35–40 | Abrir `/api/productos` y `/h2-console` (tabla PEDIDO con FKs) | La API y las tablas están unidas; separar Pedidos implicaría romper esas llaves foráneas |
| 40–45 | Pregunta al grupo: "¿cómo partirían esto?" | Anticipa el Módulo 2 (conversión a microservicios) |

## Ejercicio rápido para los estudiantes (opcional)
1. Agregar un campo `categoria` a `Producto` y mostrarlo en la tabla.
   → Observar que tocar Productos obliga a **recompilar y redesplegar toda la aplicación**.
2. Detener la app: se caen Productos, Clientes y Pedidos a la vez.

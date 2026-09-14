@echo off
REM Levanta los tres servicios, cada uno en su propia ventana.
REM El orden importa: ms-pedidos necesita a los otros dos para crear
REM sus pedidos de ejemplo al arrancar.

echo Arrancando ms-productos (8081)...
start "ms-productos :8081" cmd /k "cd /d %~dp0ms-productos && mvn spring-boot:run"

echo Arrancando ms-clientes (8082)...
start "ms-clientes :8082" cmd /k "cd /d %~dp0ms-clientes && mvn spring-boot:run"

echo Esperando 25 segundos a que arranquen...
timeout /t 25 /nobreak >nul

echo Arrancando ms-pedidos (8080)...
start "ms-pedidos :8080" cmd /k "cd /d %~dp0ms-pedidos && mvn spring-boot:run"

echo.
echo   ms-productos  http://localhost:8081/productos
echo   ms-clientes   http://localhost:8082/clientes
echo   ms-pedidos    http://localhost:8080
echo.
echo Cierra cada ventana para detener su servicio.
echo Cerrar solo la de ms-productos es la mejor demo de la clase.
pause

#!/usr/bin/env bash
# Levanta los cuatro servicios en segundo plano.
set -e
cd "$(dirname "$0")"
mkdir -p logs

echo "Arrancando ms-productos (8081)..."
(cd ms-productos && mvn -q spring-boot:run > ../logs/ms-productos.log 2>&1 &)

echo "Arrancando ms-clientes (8082)..."
(cd ms-clientes && mvn -q spring-boot:run > ../logs/ms-clientes.log 2>&1 &)

echo "Esperando 25 segundos..."
sleep 25

echo "Arrancando ms-pedidos (8080)..."
(cd ms-pedidos && mvn -q spring-boot:run > ../logs/ms-pedidos.log 2>&1 &)

echo "Arrancando ms-ventas (8083)..."
(cd ms-ventas && mvn -q spring-boot:run > ../logs/ms-ventas.log 2>&1 &)

cat <<'FIN'

  ms-productos  http://localhost:8081/productos
  ms-clientes   http://localhost:8082/clientes
  ms-pedidos    http://localhost:8080
  ms-ventas     http://localhost:8083/api/ventas

  Logs en ./logs/
  Para parar:  pkill -f spring-boot:run
FIN

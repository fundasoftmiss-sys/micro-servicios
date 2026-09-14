package com.netec.pedidos.port;

import java.util.List;

/** Igual que ConsultaCatalogoPort, pero para el desplegable de clientes. */
public interface ConsultaClientesPort {
    List<ClienteInfo> listarClientes();
}

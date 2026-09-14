package com.netec.clientes.service;

import com.netec.clientes.model.Cliente;
import com.netec.clientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clientes;

    public ClienteService(ClienteRepository clientes) {
        this.clientes = clientes;
    }

    public List<Cliente> listar() {
        return clientes.findAll();
    }

    public Cliente buscar(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Cliente no encontrado: " + id));
    }

    public Cliente guardar(Cliente cliente) {
        return clientes.save(cliente);
    }
}

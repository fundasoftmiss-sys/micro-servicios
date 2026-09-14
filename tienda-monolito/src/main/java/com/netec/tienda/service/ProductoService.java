package com.netec.tienda.service;

import com.netec.tienda.model.Producto;
import com.netec.tienda.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    /** Descuenta stock. Lo llama el modulo de pedidos: acoplamiento directo en memoria. */
    public void descontarStock(Long productoId, int cantidad) {
        Producto p = buscar(productoId);
        if (p.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para " + p.getNombre()
                    + " (disponible: " + p.getStock() + ", pedido: " + cantidad + ")");
        }
        p.setStock(p.getStock() - cantidad);
        productoRepository.save(p);
    }
}

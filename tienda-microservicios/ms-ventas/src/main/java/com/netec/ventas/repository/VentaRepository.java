package com.netec.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netec.ventas.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}

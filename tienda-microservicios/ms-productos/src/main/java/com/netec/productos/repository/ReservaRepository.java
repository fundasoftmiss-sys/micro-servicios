package com.netec.productos.repository;

import com.netec.productos.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, String> {
}

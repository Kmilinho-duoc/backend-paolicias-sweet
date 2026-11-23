package com.paolicias.reservas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paolicias.reservas.entities.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByClienteEmail(String clienteEmail);

    List<Reserva> findByEstado(String estado);

    List<Reserva> findByClienteEmailAndEstado(String clienteEmail, String estado);
}

package com.paolicias.reservas.services;

import java.util.List;
import java.util.Optional;

import com.paolicias.reservas.entities.Reserva;

public interface ReservaService {

    // email y estado son filtros opcionales (si van nulos, devuelve todas)
    List<Reserva> listar(String email, String estado);

    Optional<Reserva> obtenerPorId(Long id);

    Reserva crear(Reserva reserva);

    Optional<Reserva> cambiarEstado(Long id, String estado);

    // guarda la imagen, el método de pago y deja la reserva en "Enviado"
    Optional<Reserva> guardarComprobante(Long id, String metodoPago, byte[] imagen);

    Optional<byte[]> obtenerComprobante(Long id);

    boolean eliminar(Long id);
}

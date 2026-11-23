package com.paolicias.reservas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.reservas.entities.Reserva;
import com.paolicias.reservas.repository.ReservaRepository;

@Service
public class ReservaServiceImpl implements ReservaService {

    static final List<String> ESTADOS = List.of("Pendiente", "Enviado", "Pagado 50%");
    static final List<String> METODOS_PAGO = List.of("Transferencia", "Depósito");

    private final ReservaRepository reservaRepo;

    public ReservaServiceImpl(ReservaRepository reservaRepository) {
        this.reservaRepo = reservaRepository;
    }

    @Override
    public List<Reserva> listar(String email, String estado) {
        boolean hayEmail = email != null && !email.isBlank();
        boolean hayEstado = estado != null && !estado.isBlank();

        if (hayEmail && hayEstado) {
            return reservaRepo.findByClienteEmailAndEstado(email, estado);
        }
        if (hayEmail) {
            return reservaRepo.findByClienteEmail(email);
        }
        if (hayEstado) {
            return reservaRepo.findByEstado(estado);
        }
        return reservaRepo.findAll();
    }

    @Override
    public Optional<Reserva> obtenerPorId(Long id) {
        return reservaRepo.findById(id);
    }

    // el servidor calcula el total (suma de las órdenes) y el abono del 50 %
    @Override
    public Reserva crear(Reserva reserva) {
        if (esVacio(reserva.getClienteEmail()) || esVacio(reserva.getFecha()) || esVacio(reserva.getHora())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El correo del cliente, la fecha y la hora son obligatorios.");
        }
        if (reserva.getItems() == null || reserva.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reserva debe tener al menos una orden.");
        }

        int total = reserva.getItems().stream()
                .mapToInt(item -> item.getSubtotal() == null ? 0 : item.getSubtotal())
                .sum();

        reserva.setId(null);
        reserva.getItems().forEach(item -> item.setId(null));
        reserva.setTotal(total);
        reserva.setAbono(total / 2);
        reserva.setEstado("Pendiente");
        reserva.setMetodoPago("");
        reserva.setComprobante(null);
        return reservaRepo.save(reserva);
    }

    @Override
    public Optional<Reserva> cambiarEstado(Long id, String estado) {
        if (estado == null || !ESTADOS.contains(estado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado no válido. Usa: Pendiente, Enviado o Pagado 50%.");
        }
        return reservaRepo.findById(id).map(reserva -> {
            reserva.setEstado(estado);
            return reservaRepo.save(reserva);
        });
    }

    @Override
    public Optional<Reserva> guardarComprobante(Long id, String metodoPago, byte[] imagen) {
        if (metodoPago == null || !METODOS_PAGO.contains(metodoPago)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Método de pago no válido. Usa: Transferencia o Depósito.");
        }
        if (imagen == null || imagen.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comprobante está vacío.");
        }
        return reservaRepo.findById(id).map(reserva -> {
            reserva.setComprobante(imagen);
            reserva.setMetodoPago(metodoPago);
            reserva.setEstado("Enviado");
            return reservaRepo.save(reserva);
        });
    }

    @Override
    public Optional<byte[]> obtenerComprobante(Long id) {
        return reservaRepo.findById(id).map(Reserva::getComprobante);
    }

    @Override
    public boolean eliminar(Long id) {
        if (!reservaRepo.existsById(id)) {
            return false;
        }
        reservaRepo.deleteById(id);
        return true;
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}

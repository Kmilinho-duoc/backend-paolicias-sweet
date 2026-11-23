package com.paolicias.reservas.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.paolicias.reservas.entities.Reserva;
import com.paolicias.reservas.services.ReservaService;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // GET /api/reservas?email=...&estado=...  (los dos filtros son opcionales)
    @GetMapping
    public List<Reserva> listarReservas(@RequestParam(required = false) String email,
            @RequestParam(required = false) String estado) {
        return reservaService.listar(email, estado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerPorId(@PathVariable Long id) {
        return reservaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva guardarReserva(@RequestBody Reserva reserva) {
        return reservaService.crear(reserva);
    }

    // el cuerpo trae {"estado": "Pagado 50%"}
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Reserva> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> cuerpo) {
        return reservaService.cambiarEstado(id, cuerpo.get("estado"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // sube la imagen del comprobante (campo "archivo") y el método de pago
    @PostMapping(value = "/{id}/comprobante", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Reserva> subirComprobante(@PathVariable Long id,
            @RequestParam String metodoPago,
            @RequestParam("archivo") MultipartFile archivo) throws IOException {
        return reservaService.guardarComprobante(id, metodoPago, archivo.getBytes())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/comprobante")
    public ResponseEntity<byte[]> verComprobante(@PathVariable Long id) {
        return reservaService.obtenerComprobante(id)
                .map(imagen -> ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imagen))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long id) {
        return reservaService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

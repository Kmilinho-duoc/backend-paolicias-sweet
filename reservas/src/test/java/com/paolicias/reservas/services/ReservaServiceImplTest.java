package com.paolicias.reservas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.reservas.entities.ItemReserva;
import com.paolicias.reservas.entities.Reserva;
import com.paolicias.reservas.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepo;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private ItemReserva item(int subtotal) {
        ItemReserva item = new ItemReserva();
        item.setProducto("Producto");
        item.setCantidad(1);
        item.setSubtotal(subtotal);
        return item;
    }

    private Reserva reservaValida() {
        Reserva reserva = new Reserva();
        reserva.setClienteEmail("ana@correo.cl");
        reserva.setFecha("28/11/2026");
        reserva.setHora("18:30");
        reserva.setItems(new ArrayList<>(List.of(item(28000), item(70000))));
        return reserva;
    }

    // ---- crear ----

    @Test
    void crearCalculaElTotalYElAbonoDelCincuentaPorCiento() {
        when(reservaRepo.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva creada = reservaService.crear(reservaValida());

        assertEquals(98000, creada.getTotal());
        assertEquals(49000, creada.getAbono());
        assertEquals("Pendiente", creada.getEstado());
        assertNull(creada.getId());
    }

    @Test
    void crearIgnoraElTotalYElEstadoQueVienenDelCliente() {
        when(reservaRepo.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));
        Reserva reserva = reservaValida();
        reserva.setTotal(1);
        reserva.setEstado("Pagado 50%");

        Reserva creada = reservaService.crear(reserva);

        assertEquals(98000, creada.getTotal());
        assertEquals("Pendiente", creada.getEstado());
    }

    @Test
    void crearSinOrdenesDevuelveError() {
        Reserva reserva = reservaValida();
        reserva.setItems(new ArrayList<>());

        assertThrows(ResponseStatusException.class, () -> reservaService.crear(reserva));
        verify(reservaRepo, never()).save(any(Reserva.class));
    }

    @Test
    void crearSinFechaDevuelveError() {
        Reserva reserva = reservaValida();
        reserva.setFecha(" ");

        assertThrows(ResponseStatusException.class, () -> reservaService.crear(reserva));
        verify(reservaRepo, never()).save(any(Reserva.class));
    }

    // ---- cambiar estado ----

    @Test
    void cambiarEstadoActualizaLaReserva() {
        Reserva reserva = reservaValida();
        when(reservaRepo.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepo.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Reserva> resultado = reservaService.cambiarEstado(1L, "Pagado 50%");

        assertTrue(resultado.isPresent());
        assertEquals("Pagado 50%", resultado.get().getEstado());
    }

    @Test
    void cambiarEstadoConUnValorInvalidoDevuelveError() {
        assertThrows(ResponseStatusException.class, () -> reservaService.cambiarEstado(1L, "Cualquiera"));
        assertThrows(ResponseStatusException.class, () -> reservaService.cambiarEstado(1L, null));
    }

    @Test
    void cambiarEstadoDeUnaReservaInexistenteDevuelveVacio() {
        when(reservaRepo.findById(9L)).thenReturn(Optional.empty());

        assertTrue(reservaService.cambiarEstado(9L, "Enviado").isEmpty());
    }

    // ---- comprobante ----

    @Test
    void guardarComprobanteDejaLaReservaEnEnviadoConSuMetodoDePago() {
        Reserva reserva = reservaValida();
        when(reservaRepo.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepo.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Reserva> resultado = reservaService.guardarComprobante(1L, "Depósito", new byte[] { 1, 2, 3 });

        assertTrue(resultado.isPresent());
        assertEquals("Enviado", resultado.get().getEstado());
        assertEquals("Depósito", resultado.get().getMetodoPago());
        assertEquals(3, resultado.get().getComprobante().length);
    }

    @Test
    void guardarComprobanteConMetodoInvalidoDevuelveError() {
        assertThrows(ResponseStatusException.class,
                () -> reservaService.guardarComprobante(1L, "Efectivo", new byte[] { 1 }));
    }

    @Test
    void guardarComprobanteVacioDevuelveError() {
        assertThrows(ResponseStatusException.class,
                () -> reservaService.guardarComprobante(1L, "Transferencia", new byte[0]));
    }

    @Test
    void obtenerComprobanteSinImagenDevuelveVacio() {
        when(reservaRepo.findById(1L)).thenReturn(Optional.of(reservaValida()));

        assertTrue(reservaService.obtenerComprobante(1L).isEmpty());
    }

    // ---- listar y eliminar ----

    @Test
    void listarUsaElFiltroQueCorresponde() {
        reservaService.listar("ana@correo.cl", "Pendiente");
        verify(reservaRepo).findByClienteEmailAndEstado("ana@correo.cl", "Pendiente");

        reservaService.listar("ana@correo.cl", null);
        verify(reservaRepo).findByClienteEmail("ana@correo.cl");

        reservaService.listar(null, "Enviado");
        verify(reservaRepo).findByEstado("Enviado");

        reservaService.listar(null, null);
        verify(reservaRepo).findAll();
    }

    @Test
    void eliminarUnaReservaInexistenteDevuelveFalso() {
        when(reservaRepo.existsById(5L)).thenReturn(false);

        assertFalse(reservaService.eliminar(5L));
        verify(reservaRepo, never()).deleteById(5L);
    }

    @Test
    void eliminarUnaReservaExistenteLaBorra() {
        when(reservaRepo.existsById(1L)).thenReturn(true);

        assertTrue(reservaService.eliminar(1L));
        verify(reservaRepo).deleteById(1L);
    }
}

package com.paolicias.productos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.productos.entities.Producto;
import com.paolicias.productos.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepo;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto(String nombre, int precio) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        return producto;
    }

    @Test
    void crearGuardaElProductoSinId() {
        when(productoRepo.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        Producto nuevo = producto("Brownie", 1500);
        nuevo.setId(99L);

        Producto creado = productoService.crear(nuevo);

        assertEquals("Brownie", creado.getNombre());
        assertNull(creado.getId());
    }

    @Test
    void crearSinNombreDevuelveError() {
        assertThrows(ResponseStatusException.class, () -> productoService.crear(producto(" ", 1500)));
        verify(productoRepo, never()).save(any(Producto.class));
    }

    @Test
    void crearConPrecioCeroDevuelveError() {
        assertThrows(ResponseStatusException.class, () -> productoService.crear(producto("Brownie", 0)));
    }

    @Test
    void actualizarConservaElIdDelProductoExistente() {
        when(productoRepo.findById(5L)).thenReturn(Optional.of(producto("Viejo", 1000)));
        when(productoRepo.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Producto> resultado = productoService.actualizar(5L, producto("Nuevo", 2000));

        assertTrue(resultado.isPresent());
        assertEquals(5L, resultado.get().getId());
        assertEquals("Nuevo", resultado.get().getNombre());
    }

    @Test
    void actualizarUnProductoInexistenteDevuelveVacio() {
        when(productoRepo.findById(9L)).thenReturn(Optional.empty());

        assertTrue(productoService.actualizar(9L, producto("Nuevo", 2000)).isEmpty());
    }

    @Test
    void eliminarSoloBorraLosProductosQueExisten() {
        when(productoRepo.existsById(1L)).thenReturn(true);
        when(productoRepo.existsById(2L)).thenReturn(false);

        assertTrue(productoService.eliminar(1L));
        assertFalse(productoService.eliminar(2L));
        verify(productoRepo).deleteById(1L);
        verify(productoRepo, never()).deleteById(2L);
    }

        @Test
    void guardarFotoVaciaDevuelveError() {
        assertThrows(ResponseStatusException.class, () -> productoService.guardarFoto(1L, new byte[0]));
        verify(productoRepo, never()).save(any(Producto.class));
    }

    @Test
    void actualizarConservaLaFotoDelProductoExistente() {
        Producto existente = producto("Viejo", 1000);
        existente.setFoto(new byte[] {1, 2, 3});
        when(productoRepo.findById(5L)).thenReturn(Optional.of(existente));
        when(productoRepo.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Producto> resultado = productoService.actualizar(5L, producto("Nuevo", 2000));

        assertArrayEquals(new byte[] {1, 2, 3}, resultado.get().getFoto());
    }
}

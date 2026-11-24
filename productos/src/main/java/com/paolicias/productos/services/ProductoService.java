package com.paolicias.productos.services;

import java.util.List;
import java.util.Optional;

import com.paolicias.productos.entities.Producto;

public interface ProductoService {

    List<Producto> obtenerTodos();

    Optional<Producto> obtenerPorId(Long id);

    Producto crear(Producto producto);

    Optional<Producto> actualizar(Long id, Producto producto);

    boolean eliminar(Long id);

    Optional<Producto> guardarFoto(Long id, byte[] foto);
    
    Optional<byte[]> obtenerFoto(Long id);
}

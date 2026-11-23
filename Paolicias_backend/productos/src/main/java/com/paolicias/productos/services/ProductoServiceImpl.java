package com.paolicias.productos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.productos.entities.Producto;
import com.paolicias.productos.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepo;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepo = productoRepository;
    }

    @Override
    public List<Producto> obtenerTodos() {
        return productoRepo.findAll();
    }

    @Override
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepo.findById(id);
    }

    @Override
    public Producto crear(Producto producto) {
        validar(producto);
        producto.setId(null);
        return productoRepo.save(producto);
    }

    @Override
    public Optional<Producto> actualizar(Long id, Producto producto) {
        validar(producto);
        return productoRepo.findById(id).map(existente -> {
            producto.setId(id);
            producto.setFoto(existente.getFoto());
            return productoRepo.save(producto);
        });
    }

    @Override
    public boolean eliminar(Long id) {
        if (!productoRepo.existsById(id)) {
            return false;
        }
        productoRepo.deleteById(id);
        return true;
    }

    // el nombre es obligatorio y el precio debe ser mayor a 0
    private void validar(Producto producto) {
        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del producto es obligatorio.");
        }
        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio debe ser mayor a 0.");
        }
    }

    @Override
    public Optional<Producto> guardarFoto(Long id, byte[] foto) {
        if (foto == null || foto.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen está vacía.");
        }
        return productoRepo.findById(id).map(p -> {
            p.setFoto(foto);
            return productoRepo.save(p);
        });
    }

    @Override
    public Optional<byte[]> obtenerFoto(Long id) {
        return productoRepo.findById(id).map(Producto::getFoto);
    }
}

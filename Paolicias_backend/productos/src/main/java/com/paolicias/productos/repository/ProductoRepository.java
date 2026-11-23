package com.paolicias.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paolicias.productos.entities.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

}

package com.paolicias.productos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre = "";

    @Column(length = 500)
    private String descripcion = "";
    private String categoria = "";
    private Integer precio = 0;
    private boolean porPersona;
    private String emoji = "";
    private Long colorFondo = 0L;

    // cantidad de personas -> precio por persona (ej: "10": 6500)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producto_tarifario", joinColumns = @JoinColumn(name = "producto_id"))
    private Map<String, Integer> tarifario = new HashMap<>();

    private String imagen; // nombre del archivo en res/drawable de la app (opcional)

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producto_ingredientes", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "ingrediente")
    private List<String> ingredientes = new ArrayList<>();

    private String rinde = "";

    @Column(length = 500)
    private String alergenos = "";
    private String anticipacion = "";

    @JsonIgnore
    private byte[] foto;

    public boolean isTieneFoto() { return foto != null && foto.length > 0; }

    public Producto() {

    }
}

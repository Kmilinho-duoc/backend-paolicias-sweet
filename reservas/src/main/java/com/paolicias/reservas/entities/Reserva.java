package com.paolicias.reservas.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clienteEmail = "";
    private String fecha = "";  // dd/mm/aaaa
    private String hora = "";   // hh:mm

    @Column(length = 1000)
    private String observaciones = "";
    private Integer total = 0;
    private Integer abono = 0;  // 50 % del total
    private String estado = "Pendiente"; // Pendiente -> Enviado -> Pagado 50%
    private String metodoPago = "";      // Transferencia o Depósito

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "reserva_id")
    private List<ItemReserva> items = new ArrayList<>();

    // imagen del comprobante: se guarda en la base, pero no viaja en el JSON de la reserva
    @JsonIgnore
    private byte[] comprobante;

    public Reserva() {

    }
}

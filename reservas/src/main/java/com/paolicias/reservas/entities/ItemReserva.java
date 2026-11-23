package com.paolicias.reservas.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

// Una orden dentro de la reserva (producto + cantidad)
@Data
@Entity
@Table(name = "items_reserva")
public class ItemReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String producto = "";
    private Integer cantidad = 0;
    private String detalle = ""; // ej: "50 personas"
    private Integer subtotal = 0;

    public ItemReserva() {

    }
}

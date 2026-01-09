package com.cibertec.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transacciones")
public class Transaccion {
	
	//BANER MURGA & MARYTERE BENAVIDES
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal monto;
    private String descripcion;
    private LocalDate fecha;
    private String tipo; // Ingreso o Gasto
    private boolean activo; // Para el borrado lógico

    @ManyToOne
    @JoinColumn(name = "cuenta_id")
    @JsonBackReference(value = "cuenta") // Lado referenciado, se ignora al serializar
    private Cuenta cuenta;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonBackReference(value = "categoria") // Lado referenciado, se ignora al serializar
    private Categoria categoria;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference(value = "usuario")
    private Usuario usuario;
}
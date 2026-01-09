package com.cibertec.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cuentas")
public class Cuenta {
	
	//BANER MURGA & MARYTERE BENAVIDES
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private BigDecimal saldo;
    private String tipoCuenta; // Ahorro, Corriente, etc.
    private boolean activo; // Para el borrado lógico

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonBackReference(value = "usuario") // Lado referenciado, se ignora al serializar
    private Usuario usuario;

    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "cuenta")
    private List<Transaccion> transacciones;
}
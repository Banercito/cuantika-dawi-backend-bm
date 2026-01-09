package com.cibertec.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
	
	//BANER MURGA & MARYTERE BENAVIDES
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String username;
    private String contraseña;
    private String telefono;
    private String rol;    // ADMINISTRADOR, USUARIO
    private boolean activo; // Para el borrado lógico

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "usuario") // Lado gestionado, se serializa
    private List<Cuenta> cuentas;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "usuario-categorias")
    private List<Categoria> categorias;

}
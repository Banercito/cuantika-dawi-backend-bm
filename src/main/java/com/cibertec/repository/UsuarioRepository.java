package com.cibertec.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	//BANER MURGA & MARYTERE BENAVIDES
	
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByUsernameAndActivoTrue(String username);

    
    // Modificar todos los métodos para considerar solo activos
    java.util.List<Usuario> findByActivoTrue();
    Optional<Usuario> findByIdAndActivoTrue(Long id);
	boolean existsByUsername(String username);
	boolean existsByTelefono(String telefono);
}
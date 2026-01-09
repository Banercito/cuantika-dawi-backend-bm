package com.cibertec.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
	
	//BANER MURGA & MARYTERE BENAVIDES
	
	List<Categoria> findByUsuarioIdAndActivoTrue(Long usuarioId);

	List<Categoria> findByTipoAndUsuarioIdAndActivoTrue(String tipo, Long usuarioId);

    List<Categoria> findByTipoAndActivoTrue(String tipo);
    List<Categoria> findByActivoTrue();
    java.util.Optional<Categoria> findByIdAndActivoTrue(Long id);
}
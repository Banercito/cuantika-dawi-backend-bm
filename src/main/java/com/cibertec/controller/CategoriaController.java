package com.cibertec.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cibertec.model.Categoria;
import com.cibertec.service.CategoriaService;

@RestController

@RequestMapping("/api/categorias")
public class CategoriaController {
	
	//BANER MURGA & MARYTERE BENAVIDES
	
    
    @Autowired
    private CategoriaService categoriaService;
    
    //Listar categorías
    
    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategoriasSegunRol());
    }
    
    //Listar categorías por tipo
    
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<List<Categoria>> listarCategoriasPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(categoriaService.listarCategoriasPorTipo(tipo));
    }
    
    //Listar categoría por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<Categoria> obtenerCategoria(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerCategoriaPorId(id));
    }
    
    //Insertar categoría
    /**
     * Endpoint para crear una nueva categoría para el usuario autenticado.
     * Solo el usuario que está autenticado puede crear categorías para sí mismo.
     */
    @PostMapping("/insertar")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMINISTRADOR')")
    public ResponseEntity<Categoria> crearCategoria(@RequestBody Categoria categoria) {
        Categoria categoriaCreada = categoriaService.crearCategoria(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaCreada);
    }
    
    //Actualizar categoría
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','USUARIO')")
    public ResponseEntity<Categoria> actualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, categoria));
    }
    
    //Eliminar categoría
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','USUARIO')")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.ok().build();
    }
}
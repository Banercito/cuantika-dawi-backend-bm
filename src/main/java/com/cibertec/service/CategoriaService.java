package com.cibertec.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cibertec.model.Categoria;
import com.cibertec.model.Usuario;
import com.cibertec.repository.CategoriaRepository;
import com.cibertec.repository.UsuarioRepository;

@Service
public class CategoriaService {

	//BANER MURGA & MARYTERE BENAVIDES
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    
    
    //Obtener usuario

    private Usuario obtenerUsuarioAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsernameAndActivoTrue(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        
    }
    
    // Método para determinar si el usuario actual es administrador
    private boolean esUsuarioAdministrador() {
        Usuario usuario = obtenerUsuarioAutenticado();
        return usuario.getRol() != null && usuario.getRol().equals("ADMINISTRADOR");
    }

    // Método que lista todas las categorías o solo las del usuario según su rol
    public List<Categoria> listarCategoriasSegunRol() {
        if (esUsuarioAdministrador()) {
            // Si es administrador, devuelve todas las categorías activas
            return categoriaRepository.findByActivoTrue();
        } else {
            // Si es usuario regular, solo devuelve las categorías del usuario autenticado
            Usuario usuario = obtenerUsuarioAutenticado();
            return categoriaRepository.findByUsuarioIdAndActivoTrue(usuario.getId());
        }
    }



    // Método para listar categorías por tipo (solo para el usuario actual o todas si es admin)
    public List<Categoria> listarCategoriasPorTipo(String tipo) {
        if (esUsuarioAdministrador()) {
            // Si es administrador, devuelve todas las categorías del tipo especificado
            return categoriaRepository.findByTipoAndActivoTrue(tipo);
        } else {
            // Si es usuario regular, solo devuelve las categorías del tipo y del usuario autenticado
            Usuario usuario = obtenerUsuarioAutenticado();
            return categoriaRepository.findByTipoAndUsuarioIdAndActivoTrue(tipo, usuario.getId());
        }
    }
    
//Listar por ID
    public Categoria obtenerCategoriaPorId(Long id) {
        Categoria categoria = categoriaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Usuario usuario = obtenerUsuarioAutenticado();
        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para acceder a esta categoría.");
        }

        return categoria;
    }

    public Categoria crearCategoria(Categoria categoria) {
        // Validar campos obligatorios
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoría es obligatorio");
        }

        if (categoria.getTipo() == null || categoria.getTipo().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de la categoría es obligatorio");
        }

        String tipo = categoria.getTipo().trim();
        if (!tipo.equalsIgnoreCase("Ingreso") && !tipo.equalsIgnoreCase("Gasto")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo debe ser 'Ingreso' o 'Gasto'");
        }

        // Obtener usuario autenticado
        Usuario usuario = obtenerUsuarioAutenticado();

        // Verificar si ya existe una categoría con el mismo nombre para ese usuario
        List<Categoria> existentes = categoriaRepository.findByUsuarioIdAndActivoTrue(usuario.getId());
        boolean existeNombreRepetido = existentes.stream()
            .anyMatch(c -> c.getNombre().equalsIgnoreCase(categoria.getNombre().trim()));

        if (existeNombreRepetido) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una categoría con ese nombre.");
        }

        categoria.setUsuario(usuario);
        categoria.setActivo(true);
        return categoriaRepository.save(categoria);
    }



    
    //Actualizar categoría
    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        Categoria categoria = obtenerCategoriaPorId(id);
        Usuario usuario = obtenerUsuarioAutenticado();

        // Verificar si el nuevo nombre ya lo tiene otra categoría activa del mismo usuario (excluyendo la actual)
        List<Categoria> existentes = categoriaRepository.findByUsuarioIdAndActivoTrue(usuario.getId());
        boolean nombreDuplicado = existentes.stream()
            .anyMatch(c -> !c.getId().equals(id) && c.getNombre().equalsIgnoreCase(categoriaActualizada.getNombre().trim()));

        if (nombreDuplicado) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe otra categoría con ese nombre.");
        }

        categoria.setNombre(categoriaActualizada.getNombre());
        categoria.setDescripcion(categoriaActualizada.getDescripcion());
        categoria.setTipo(categoriaActualizada.getTipo());

        return categoriaRepository.save(categoria);
    }

    //Eliminar categoría
    public void eliminarCategoria(Long id) {
        Categoria categoria = obtenerCategoriaPorId(id);
        categoria.setActivo(false);
        categoriaRepository.save(categoria);
    }
}

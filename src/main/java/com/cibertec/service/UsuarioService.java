package com.cibertec.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.cibertec.model.Usuario;
import com.cibertec.repository.UsuarioRepository;

@Service
public class UsuarioService {
	//BANER MURGA & MARYTERE BENAVIDES

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    //Validar usuario
    
    public void validarPropietarioOCancelar(Long usuarioId) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado();

        if (!usuarioAutenticado.getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para acceder a este usuario.");
        }
    }
//Obtener usuario

    public Usuario obtenerUsuarioAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsernameAndActivoTrue(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
//Listar usuarios
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findByActivoTrue();
    }
    
    //Listar por ID

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    //Crear usuario

    public Usuario crearUsuario(Usuario usuario) {
        // Verifica si hay alguien autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Si ya hay un usuario autenticado (y no es anónimo), no puede crear un nuevo usuario
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("No puede crear un nuevo usuario estando autenticado.");
        }

        // Validación: evitar duplicados por username
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("Ya existe un usuario con ese nombre de usuario.");
        }
        
        // Validación: evitar duplicados por teléfono
        if (usuario.getTelefono() != null && usuarioRepository.existsByTelefono(usuario.getTelefono())) {
            throw new RuntimeException("Ya existe un usuario con ese número de teléfono.");
        }

        // Asignar rol de usuario por defecto y evitar rol de admin
        usuario.setRol("USUARIO");
        
        // Si todo está bien, se registra el nuevo usuario
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

       //Actualizar usuario
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario actual = obtenerUsuarioPorId(id);
        Usuario logueado = obtenerUsuarioAutenticado();

        // Solo puede modificarse a sí mismo o ser admin
        if (!logueado.getId().equals(actual.getId()) && !logueado.getRol().equals("ADMINISTRADOR")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para modificar este usuario.");
        }

        actual.setNombre(usuarioActualizado.getNombre());
        actual.setApellido(usuarioActualizado.getApellido());
        actual.setUsername(usuarioActualizado.getUsername());
        if (usuarioActualizado.getContraseña() != null && !usuarioActualizado.getContraseña().isEmpty()) {
            actual.setContraseña(passwordEncoder.encode(usuarioActualizado.getContraseña()));
        }
        actual.setTelefono(usuarioActualizado.getTelefono());
        actual.setRol(usuarioActualizado.getRol());

        return usuarioRepository.save(actual);
    }
//Eliminar usuario
    public void eliminarUsuario(Long id) {
        Usuario actual = obtenerUsuarioPorId(id);
        Usuario logueado = obtenerUsuarioAutenticado();

        if (!logueado.getId().equals(actual.getId()) && !logueado.getRol().equals("ADMINISTRADOR")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para eliminar este usuario.");
        }

        actual.setActivo(false);
        usuarioRepository.save(actual);
    }
}

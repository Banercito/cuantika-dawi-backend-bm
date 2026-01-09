package com.cibertec.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cibertec.model.Usuario;
import com.cibertec.service.UsuarioService;

import jakarta.annotation.security.PermitAll;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/usuarios")
public class UsuarioController {
	
	//BANER MURGA & MARYTERE BENAVIDES

    @Autowired
    private UsuarioService usuarioService;

    
    //Listar todos los usuarios
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }
    
    //Listar usuario autenticado

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<Usuario> obtenerUsuario() {
        Usuario usuarioAutenticado = usuarioService.obtenerUsuarioAutenticado();
        return ResponseEntity.ok(usuarioAutenticado);
    }

    //Insertar usuario

    @PostMapping("/registro")
    @PermitAll
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.crearUsuario(usuario));
    }

    //Actualizar usuario
    @PutMapping("/put")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','USUARIO')")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuarioActualizado) {
        Usuario usuarioAutenticado = usuarioService.obtenerUsuarioAutenticado();
        Usuario actualizado = usuarioService.actualizarUsuario(usuarioAutenticado.getId(), usuarioActualizado);
        return ResponseEntity.ok(actualizado);
    }

//Eliminar usuario
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok().build();
    }
}

package com.cibertec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cibertec.dto.LoginRequest;
import com.cibertec.model.Usuario;
import com.cibertec.service.UsuarioService;

@RestController

@RequestMapping("/api/auth")
public class AuthController {

    // BANER MURGA & MARYTERE BENAVIDES
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager; // Necesitas configurar esto en tu SecurityConfig

    //Registrar usuario
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        usuario.setRol("USUARIO"); // Por defecto, todos los usuarios nuevos son de tipo USUARIO
        Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente: " + nuevoUsuario.getUsername());
    }
 
    //Logueo de usuario
    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Autenticación exitosa
            // Aquí deberías generar y devolver un token JWT o configurar una sesión HTTP
            // Para este ejemplo básico, simplemente devolvemos un mensaje de éxito
            return ResponseEntity.ok("Logueo exitoso para: " + loginRequest.getUsernameOrEmail());

        } catch (Exception e) {
            // La autenticación falló
            return ResponseEntity.badRequest().body("Credenciales inválidas.");
        }
    }
}
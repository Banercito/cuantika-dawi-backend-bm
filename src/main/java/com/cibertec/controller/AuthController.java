package com.cibertec.controller;

import com.cibertec.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {
        "https://cuantika-frontend.onrender.com",
        "http://localhost:4200"
    }
)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsernameOrEmail(), // 🔑 frontend compatible
                    request.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok(
                new AuthResponse("Login exitoso", authentication.getName())
            );

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Usuario o contraseña incorrectos"));
        }
    }

    // 🔹 RESPUESTAS JSON
    static class AuthResponse {
        private String message;
        private String username;

        public AuthResponse(String message, String username) {
            this.message = message;
            this.username = username;
        }

        public String getMessage() { return message; }
        public String getUsername() { return username; }
    }

    static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }
}

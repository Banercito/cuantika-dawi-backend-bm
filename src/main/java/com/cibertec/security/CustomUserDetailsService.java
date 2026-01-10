package com.cibertec.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.cibertec.model.Usuario;
import com.cibertec.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("Usuario no encontrado")
            );

        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("Usuario desactivado");
        }

        return new User(
            usuario.getUsername(),
            usuario.getContraseña(), // BCrypt
            Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol())
            )
        );
    }
}

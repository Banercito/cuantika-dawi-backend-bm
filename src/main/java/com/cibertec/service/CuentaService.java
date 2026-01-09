package com.cibertec.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cibertec.model.Cuenta;
import com.cibertec.model.Usuario;
import com.cibertec.repository.CuentaRepository;
import com.cibertec.repository.UsuarioRepository;

@Service
public class CuentaService {
	
	//BANER MURGA & MARYTERE BENAVIDES

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    
  //Listar cuentas

    public List<Cuenta> listarTodasLasCuentasActivas() {
        return cuentaRepository.findByActivoTrue();
    }


    
    //Listar cuentas de usuario
    public List<Cuenta> listarCuentasDelUsuarioAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        return cuentaRepository.findByUsuarioIdAndActivoTrue(usuario.getId());
    }
    
    

  //Obtener usuario
      private Usuario obtenerUsuarioAutenticado() {
          String username = SecurityContextHolder.getContext().getAuthentication().getName();
          return usuarioRepository.findByUsernameAndActivoTrue(username)
              .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
      }
      
    //Listar cuenta por ID
    public Cuenta obtenerCuentaPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findByIdAndActivoTrue(id)
            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (!cuenta.getUsuario().getId().equals(obtenerUsuarioAutenticado().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para acceder a esta cuenta.");
        }

        return cuenta;
    }
    

    //Validar propietario de cuenta
    
    public void validarPropietarioCuentaOCancelar(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findByIdAndActivoTrue(cuentaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        if (!cuenta.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para modificar esta cuenta.");
        }
    }
//Crear cuenta 
    
    public Cuenta crearCuentaParaUsuarioAutenticado(Cuenta cuenta) {
        if (cuenta.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se debe especificar un ID al crear una nueva cuenta.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        cuenta.setUsuario(usuario);
        cuenta.setSaldo(BigDecimal.ZERO);
        cuenta.setActivo(true);
        return cuentaRepository.save(cuenta);
    }


    
  
    //Crear cuenta

    @Transactional
    public Cuenta crearCuenta(Cuenta cuenta) {
        Usuario usuario = obtenerUsuarioAutenticado();
        cuenta.setUsuario(usuario);
        cuenta.setSaldo(BigDecimal.ZERO); // <-- Forzar saldo inicial en 0.00
        cuenta.setActivo(true);
        return cuentaRepository.save(cuenta);
    }

//Actualizar cuenta
    @Transactional
    public Cuenta actualizarCuenta(Long id, Cuenta cuentaActualizada) {
        Cuenta cuenta = obtenerCuentaPorId(id);

        if (!cuenta.getUsuario().getId().equals(obtenerUsuarioAutenticado().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para modificar esta cuenta.");
        }

        cuenta.setNombre(cuentaActualizada.getNombre());
        cuenta.setSaldo(cuentaActualizada.getSaldo());
        cuenta.setTipoCuenta(cuentaActualizada.getTipoCuenta());
        cuenta.setActivo(cuentaActualizada.isActivo());
        return cuentaRepository.save(cuenta);
    }
    
    
    //Eliminar cuenta

    @Transactional
    public void eliminarCuenta(Long id) {
        Cuenta cuenta = obtenerCuentaPorId(id);

        if (!cuenta.getUsuario().getId().equals(obtenerUsuarioAutenticado().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para eliminar esta cuenta.");
        }

        cuenta.setActivo(false);
        cuentaRepository.save(cuenta);
    }
}

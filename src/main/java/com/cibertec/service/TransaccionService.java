package com.cibertec.service;


import java.util.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cibertec.model.Categoria;
import com.cibertec.model.Cuenta;
import com.cibertec.model.Transaccion;
import com.cibertec.model.Usuario;
import com.cibertec.repository.CategoriaRepository;
import com.cibertec.repository.CuentaRepository;
import com.cibertec.repository.TransaccionRepository;
import com.cibertec.repository.UsuarioRepository;

@Service
public class TransaccionService {
	
	//BANER MURGA & MARYTERE BENAVIDES

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private CuentaRepository cuentaRepository;

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
    
    //Listar trasacción

    public List<Transaccion> listarTodasLasTransacciones() {
        // Verificar si el usuario autenticado tiene el rol de ADMINISTRADOR
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

     // Verificar si el usuario tiene el rol de ADMINISTRADOR
        boolean esAdministrador = usuario.getRol() != null && usuario.getRol().equals("ADMINISTRADOR");

        if (!esAdministrador) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para modificar este usuario.");
        }

        // Si es administrador, devolver todas las transacciones activas
        return transaccionRepository.findByActivoTrue();
    }
//Listar transacciones de usuario 


    public List<Transaccion> listarTransaccionesDelUsuarioAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        // Suponiendo que quieras todas las transacciones de todas sus cuentas activas
        List<Cuenta> cuentas = cuentaRepository.findByUsuarioIdAndActivoTrue(usuario.getId());

        List<Transaccion> todasTransacciones = new ArrayList<>();
        for (Cuenta cuenta : cuentas) {
            List<Transaccion> transacciones = transaccionRepository.findByCuentaIdAndActivoTrue(cuenta.getId());
            todasTransacciones.addAll(transacciones);
        }

        return todasTransacciones;
    }
    
    //Listar transacciones por cuenta

    public List<Transaccion> listarTransaccionesPorCuenta(Long cuentaId) {
        Usuario usuario = obtenerUsuarioAutenticado();
        Cuenta cuenta = cuentaRepository.findByIdAndActivoTrue(cuentaId)
            .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (!cuenta.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para acceder a esta cuenta.");
        }

        return transaccionRepository.findByCuentaAndActivoTrue(cuenta);
    }
    
    //Obtener transacción por IF

    public Transaccion obtenerTransaccionPorId(Long id) {
        Transaccion transaccion = transaccionRepository.findByIdAndActivoTrue(id)
            .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));

        Usuario usuario = obtenerUsuarioAutenticado();
        if (!transaccion.getCuenta().getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para acceder a esta transacción.");
        }

        return transaccion;
    }
//Validad propietario de la cuenta

public void validarPropietarioCuentaOCancelar(Long cuentaId) {
    Usuario usuario = obtenerUsuarioAutenticado();
    Cuenta cuenta = cuentaRepository.findByIdAndActivoTrue(cuentaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));

    if (!cuenta.getUsuario().getId().equals(usuario.getId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene acceso a esta cuenta.");
    }
}

//Validar propietario de la transacción
public void validarPropietarioTransaccionOCancelar(Long transaccionId) {
    Usuario usuario = obtenerUsuarioAutenticado();
    Transaccion transaccion = transaccionRepository.findByIdAndActivoTrue(transaccionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transacción no encontrada"));

    if (!transaccion.getCuenta().getUsuario().getId().equals(usuario.getId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene acceso a esta transacción.");
    }
}
    
    

//Actualizar transacción

@Transactional
public Transaccion actualizarTransaccion(Long id, Transaccion transaccionActualizada) {
    Transaccion transaccion = obtenerTransaccionPorId(id);
    Cuenta cuenta = transaccion.getCuenta();

    if (!cuenta.getUsuario().getId().equals(obtenerUsuarioAutenticado().getId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para modificar esta transacción.");
    }

    

    
 // Validación de fecha (no se permite fecha futura)
    if (transaccionActualizada.getFecha().isAfter(LocalDate.now())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de la transaccion no puede ser posterior a hoy");
    }

    // Revertir la anterior
    if ("Ingreso".equals(transaccion.getTipo())) {
        cuenta.setSaldo(cuenta.getSaldo().subtract(transaccion.getMonto()));
    } else {
        cuenta.setSaldo(cuenta.getSaldo().add(transaccion.getMonto()));
    }

    // Aplicar nueva
    if ("Ingreso".equals(transaccionActualizada.getTipo())) {
        cuenta.setSaldo(cuenta.getSaldo().add(transaccionActualizada.getMonto()));
    } else {
        cuenta.setSaldo(cuenta.getSaldo().subtract(transaccionActualizada.getMonto()));
    }

    cuentaRepository.save(cuenta);

    transaccion.setMonto(transaccionActualizada.getMonto());
    transaccion.setDescripcion(transaccionActualizada.getDescripcion());
    transaccion.setFecha(transaccionActualizada.getFecha());
    transaccion.setTipo(transaccionActualizada.getTipo());

    return transaccionRepository.save(transaccion);
}

    
    //Crear transacción para usuario autenticado
@Transactional
public Transaccion crearTransaccionParaUsuarioAutenticado(Long cuentaId, Long categoriaId, Transaccion transaccion) {
    // Obtener el usuario autenticado
    Usuario usuario = obtenerUsuarioAutenticado();

    // Buscar la cuenta específica por ID en lugar de usar la primera cuenta
    Cuenta cuenta = cuentaRepository.findByIdAndActivoTrue(cuentaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));

    // Verificar que la cuenta pertenece al usuario autenticado
    if (!cuenta.getUsuario().getId().equals(usuario.getId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene acceso a esta cuenta.");
    }

    // Validar que la categoría existe
    Categoria categoria = categoriaRepository.findByIdAndActivoTrue(categoriaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

    // Validar que la fecha no sea futura
    if (transaccion.getFecha() != null && transaccion.getFecha().isAfter(LocalDate.now())) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de la transacción no puede ser posterior a hoy.");
    }

    // Verificar el tipo de transacción (Ingreso o Gasto) y modificar el saldo de la cuenta
    if ("Ingreso".equalsIgnoreCase(transaccion.getTipo())) {
        cuenta.setSaldo(cuenta.getSaldo().add(transaccion.getMonto()));
    } else if ("Gasto".equalsIgnoreCase(transaccion.getTipo())) {
        if (cuenta.getSaldo().compareTo(transaccion.getMonto()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente.");
        }
        cuenta.setSaldo(cuenta.getSaldo().subtract(transaccion.getMonto()));
    } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo no válido: 'Ingreso' o 'Gasto'.");
    }

    // Guardar la cuenta actualizada
    cuentaRepository.save(cuenta);

    // Asociar la transacción con la cuenta, categoría y usuario
    transaccion.setCuenta(cuenta);
    transaccion.setCategoria(categoria);
    transaccion.setUsuario(usuario);
    transaccion.setActivo(true);

    // Guardar la transacción
    return transaccionRepository.save(transaccion);
}

    //Eliminar transacción

    @Transactional
    public void eliminarTransaccion(Long id) {
        Transaccion transaccion = obtenerTransaccionPorId(id);
        Cuenta cuenta = transaccion.getCuenta();

        if (!cuenta.getUsuario().getId().equals(obtenerUsuarioAutenticado().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permiso para eliminar esta transacción.");
        }

        transaccion.setActivo(false);

        if ("Ingreso".equals(transaccion.getTipo())) {
            cuenta.setSaldo(cuenta.getSaldo().subtract(transaccion.getMonto()));
        } else {
            cuenta.setSaldo(cuenta.getSaldo().add(transaccion.getMonto()));
        }

        cuentaRepository.save(cuenta);
        transaccionRepository.save(transaccion);
    }
}

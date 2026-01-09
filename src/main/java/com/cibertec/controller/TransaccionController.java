package com.cibertec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import com.cibertec.model.Transaccion;
import com.cibertec.service.TransaccionService;

@RestController

@RequestMapping("/api/transacciones")
public class TransaccionController {
	
	
	//BANER MURGA & MARYTERE BENAVIDES

    @Autowired
    private TransaccionService transaccionService;

    //Listar todas las transacciones
    
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Transaccion>> listarTodasLasTransacciones() {
        return ResponseEntity.ok(transaccionService.listarTodasLasTransacciones());
    }

    
    //Listar transacciones del usuario

        @GetMapping("/listar")
        @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
        public ResponseEntity<List<Transaccion>> listarTransaccionesDelUsuarioAutenticado() {
            return ResponseEntity.ok(transaccionService.listarTransaccionesDelUsuarioAutenticado());
        }
    
//Listar transacciones por cuenta
    @GetMapping("/cuenta/{cuentaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<List<Transaccion>> listarTransaccionesPorCuenta(@PathVariable Long cuentaId) {
        transaccionService.validarPropietarioCuentaOCancelar(cuentaId);
        return ResponseEntity.ok(transaccionService.listarTransaccionesPorCuenta(cuentaId));
    }

    //Listar transaccciones por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<Transaccion> obtenerTransaccion(@PathVariable Long id) {
        transaccionService.validarPropietarioTransaccionOCancelar(id);
        return ResponseEntity.ok(transaccionService.obtenerTransaccionPorId(id));
    }
    
    //Crear transacción
    @PostMapping("/cuenta/{cuentaId}/categoria/{categoriaId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<Transaccion> crearTransaccion(
        @PathVariable Long cuentaId,
        @PathVariable Long categoriaId,
        @RequestBody Transaccion transaccion) {

        // Llamar al servicio para crear la transacción para el usuario autenticado
        Transaccion transaccionCreada = transaccionService.crearTransaccionParaUsuarioAutenticado(cuentaId, categoriaId, transaccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaccionCreada);
    }

//Actualizar transacción
    @PutMapping("/put")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<Transaccion> actualizarTransaccion(@RequestBody Transaccion transaccion) {
        if (transaccion.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere el ID de la transacción para actualizar.");
        }

        return ResponseEntity.ok(transaccionService.actualizarTransaccion(transaccion.getId(), transaccion));
    }

    //Eliminar transacción

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','USUARIO')")
    public ResponseEntity<Void> eliminarTransaccion(@PathVariable Long id) {
        transaccionService.eliminarTransaccion(id);
        return ResponseEntity.ok().build();
    }
}

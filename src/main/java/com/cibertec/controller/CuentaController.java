package com.cibertec.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.cibertec.model.Cuenta;
import com.cibertec.service.CuentaService;
import com.cibertec.service.UsuarioService;

@RestController

@RequestMapping("/api/cuentas")
public class CuentaController {
	
	//BANER MURGA & MARYTERE BENAVIDES

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private UsuarioService usuarioService;

    //Listar todas las cuentas
   
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<Cuenta>> listarTodasLasCuentas() {
        return ResponseEntity.ok(cuentaService.listarTodasLasCuentasActivas());
    }

//Listar cuentas de un usuario
    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<List<Cuenta>> listarCuentasPorUsuarioAutenticado() {
        return ResponseEntity.ok(cuentaService.listarCuentasDelUsuarioAutenticado());
    }

    //Listar cuenta por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<Cuenta> obtenerCuenta(@PathVariable Long id) {
        cuentaService.validarPropietarioCuentaOCancelar(id);
        return ResponseEntity.ok(cuentaService.obtenerCuentaPorId(id));
    }
    
//Insertar cuenta
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<Cuenta> crearCuenta(@RequestBody Cuenta cuenta) {
        return ResponseEntity.ok(cuentaService.crearCuentaParaUsuarioAutenticado(cuenta));
    }
    
    //Actualizar cuenta

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<Cuenta> actualizarCuenta(@PathVariable Long id, @RequestBody Cuenta cuenta) {
        cuentaService.validarPropietarioCuentaOCancelar(id);
        return ResponseEntity.ok(cuentaService.actualizarCuenta(id, cuenta));
    }
    //Eliminar cuenta

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'USUARIO')")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        cuentaService.validarPropietarioCuentaOCancelar(id);
        cuentaService.eliminarCuenta(id);
        return ResponseEntity.ok().build();
    }
}

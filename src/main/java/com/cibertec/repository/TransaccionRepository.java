package com.cibertec.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cibertec.model.Transaccion;
import com.cibertec.model.Cuenta;
import com.cibertec.model.Categoria;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    // Método corregido para buscar una transacción por su id y usuario (vía cuenta)
    Optional<Transaccion> findByIdAndCuentaUsuarioIdAndActivo(Long id, Long usuarioId, boolean activo);

    // Método para encontrar transacciones activas por el usuario de la cuenta
    List<Transaccion> findByCuentaUsuarioIdAndActivoTrue(Long usuarioId);

    // Método para encontrar transacciones activas por el id de la cuenta
    List<Transaccion> findByCuentaIdAndActivoTrue(Long cuentaId);

    // Método para encontrar transacciones activas por la cuenta misma
    List<Transaccion> findByCuentaAndActivoTrue(Cuenta cuenta);

    // Método para encontrar transacciones activas por la categoría
    List<Transaccion> findByCategoriaAndActivoTrue(Categoria categoria);

    // Método para encontrar todas las transacciones activas
    List<Transaccion> findByActivoTrue();

    // Método para encontrar una transacción activa por su id
    Optional<Transaccion> findByIdAndActivoTrue(Long id);
}
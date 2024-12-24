package dev.jnorza.ejercicio_kafka.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.jnorza.ejercicio_kafka.entities.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado,Long> {
    
    @Query(" SELECT e FROM Empleado e WHERE e.correo LIKE :email ")
    public Optional<Empleado> existeCorreo(@Param("email") String email);
    
    @Query(" SELECT e FROM Empleado e WHERE e.departamento LIKE :departamento ")
    public Page<Empleado> findByDepartamento(@Param("departamento") String departamento, Pageable pageable);

}

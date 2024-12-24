package dev.jnorza.ejercicio_kafka.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
@Table(name = "Empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String nombre;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fechaIngreso;

    private BigDecimal salario;

    @Transient
    private String salarioMX;

    private String correo;

    private String departamento;
    
}

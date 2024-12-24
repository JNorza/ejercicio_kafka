package dev.jnorza.ejercicio_kafka.controllers;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.jnorza.ejercicio_kafka.Utileria.Archivos;
import dev.jnorza.ejercicio_kafka.entities.Empleado;
import dev.jnorza.ejercicio_kafka.repositories.EmpleadoRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("empleados")
public class EmpleadoController {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;        
    
    @Autowired
    EmpleadoRepository empleadoRepository;

    @Autowired
    Archivos archivo;

    @PostMapping
    public ResponseEntity<Empleado> crear(
        @RequestBody Empleado empleado
    ){


        if(this.empleadoRepository.existeCorreo(empleado.getCorreo()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        Empleado empleadoSaved = null;

        try {
            empleadoSaved = this.empleadoRepository.save(empleado);    
            kafkaTemplate.send("empleados-events","Empleado Creado: "+empleado.getNombre());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }

        if(empleadoSaved != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(empleadoSaved);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);


    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> obtener(@PathVariable Long id) {

        if(id <= 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<Empleado> empleado = this.empleadoRepository.findById(id);

        if(empleado.isPresent()){
            NumberFormat fmoneda = NumberFormat.getCurrencyInstance(new Locale("es","MX"));

            empleado.get().setSalarioMX(fmoneda.format(empleado.get().getSalario()));

            return ResponseEntity.status(HttpStatus.OK).body(empleado.get());

        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @GetMapping()
    public ResponseEntity<Page<Empleado>> obtener(
        @RequestParam(required = false) String departamento, 
        Pageable page
        ) {

        Page<Empleado> empleados = null;
        
        if(departamento != null && !departamento.isEmpty()){
            empleados = this.empleadoRepository.findByDepartamento(departamento, page);
        }else{
            empleados = this.empleadoRepository.findAll(page);
        }

        
        return ResponseEntity.status(HttpStatus.OK).body(empleados);       

        
    }
    

    @PutMapping
    public ResponseEntity<Empleado> editar(
        @RequestBody Empleado empleado
    ){


        //Verificando usuario
        Optional<Empleado> empleadoAux = this.empleadoRepository.findById(empleado.getId());
        if(!empleadoAux.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }        

        //Verificando correo
        if(!empleado.getCorreo().equalsIgnoreCase(empleadoAux.get().getCorreo()) &&  this.empleadoRepository.existeCorreo(empleado.getCorreo()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        Empleado empleadoSaved = null;

        try {
            this.archivo.generarPDFEmpleado(empleadoAux.get());
            empleadoSaved = this.empleadoRepository.saveAndFlush(empleado);            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }

        if(empleadoSaved != null){
            return ResponseEntity.ok().body(empleadoSaved);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);


    }


    
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(
        @PathVariable Long id
    ) {

        try {
            Optional<Empleado> empleadoEliminar = this.empleadoRepository.findById(id);
            if(empleadoEliminar.isPresent()){
                this.archivo.registrarElminacion(empleadoEliminar.get());
                this.empleadoRepository.deleteById(id);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(false);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(false);
        }


        return ResponseEntity.ok().body(Boolean.TRUE);
    }



    

    
}

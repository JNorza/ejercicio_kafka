package dev.jnorza.ejercicio_kafka.Utileria;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import dev.jnorza.ejercicio_kafka.entities.Empleado;

@Component
public class Archivos {
    
    public void generarPDFEmpleado(Empleado empleado) throws IOException{

        
        NumberFormat fmoneda = NumberFormat.getCurrencyInstance(new Locale("es","MX"));

        empleado.setSalarioMX(fmoneda.format(empleado.getSalario()));

        String pdf = "c:/reportes/empleado_"+empleado.getId()+".pdf";

        // Verificar si la carpeta existe, si no, crearla        
        File f = new File(pdf);
        
        File dir = new File(f.getParent());
        if (!dir.exists()) {
            dir.mkdir();
        }
        
        PdfWriter writer = new PdfWriter(pdf);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document d = new Document(pdfDoc);

        // Agregar información al PDF (empleado)
        d.add(new Paragraph("Empleado Detalles (Antes de la Actualización):"));
        d.add(new Paragraph("--------------------------------------------------"));
        d.add(new Paragraph("ID: " + empleado.getId()));
        d.add(new Paragraph("Nombre: " + empleado.getNombre()));
        d.add(new Paragraph("Fecha de Ingreso: " + empleado.getFechaIngreso()));
        d.add(new Paragraph("Salario: " + empleado.getSalarioMX()));
        d.add(new Paragraph("Correo: " + empleado.getCorreo()));
        d.add(new Paragraph("Departamento: " + empleado.getDepartamento()));

        d.close();

    }

    public void registrarElminacion(Empleado empleado) throws IOException{
        
        String log = "c:/logs/empleados.txt";
        // Verificar si la carpeta existe, si no, crearla
            File f = new File(log);
            File dir = new File(f.getParent());
            if (!dir.exists()) {
                dir.mkdir();
            }
            
            DateTimeFormatter ffecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            String contenido = new String();
            contenido += "\n-------------------------------------------------- \n";
            contenido += "[" + ffecha.format(LocalDateTime.now()) + "] \n";
            contenido += "Empleado Detalles (Antes de la Actualización): \n";
            contenido += "ID: " + empleado.getId()+"\n";
            contenido += "Nombre: " + empleado.getNombre()+"\n";
            contenido += "Fecha de Ingreso: " + empleado.getFechaIngreso()+"\n";
            contenido += "Salario: " + empleado.getSalarioMX()+"\n";
            contenido += "Correo: " + empleado.getCorreo()+"\n";
            contenido += "Departamento: " + empleado.getDepartamento()+"\n";

            if (!f.exists()) {
                f.createNewFile();
            }
            
            FileWriter fw = new FileWriter(f.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contenido);
            bw.close();
        
    }


}

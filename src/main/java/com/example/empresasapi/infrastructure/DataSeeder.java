package com.example.empresasapi.infrastructure;

import com.example.empresasapi.domain.entities.Compania;
import com.example.empresasapi.domain.entities.Empleado;
import com.example.empresasapi.domain.interfaces.IUnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private IUnitOfWork unitOfWork;

    @Override
    @Transactional
    public void run(String... args) {
        if (unitOfWork.getCompanias().findAll().isEmpty()) {
            logger.info("Insertando datos iniciales...");

            Compania c1 = new Compania();
            c1.setNombre("Tech Solutions S.A.S");
            c1.setDireccion("Calle 45 # 10-20, Bogotá");
            c1.setTelefono("3001234567");
            c1.setFechaCreacion(LocalDate.now());
            unitOfWork.getCompanias().save(c1);

            Compania c2 = new Compania();
            c2.setNombre("Innovatech Ltda");
            c2.setDireccion("Carrera 15 # 80-45, Medellín");
            c2.setTelefono("3107654321");
            c2.setFechaCreacion(LocalDate.now());
            unitOfWork.getCompanias().save(c2);

            Compania c3 = new Compania();
            c3.setNombre("Digital Corp S.A");
            c3.setDireccion("Avenida 68 # 22-10, Cali");
            c3.setTelefono("3209876543");
            c3.setFechaCreacion(LocalDate.now());
            unitOfWork.getCompanias().save(c3);

            crearEmpleado("Ana", "Gómez", "ana.gomez@tech.com", "Desarrolladora", 3500000.0, c1);
            crearEmpleado("Carlos", "Rojas", "carlos.rojas@tech.com", "Tester", 2800000.0, c1);
            crearEmpleado("Laura", "Martínez", "laura.martinez@tech.com", "Diseñadora", 3000000.0, c1);
            crearEmpleado("Pedro", "López", "pedro.lopez@tech.com", "DevOps", 4000000.0, c1);

            crearEmpleado("María", "Torres", "maria.torres@innova.com", "Analista", 3200000.0, c2);
            crearEmpleado("Jorge", "Díaz", "jorge.diaz@innova.com", "Scrum Master", 4500000.0, c2);
            crearEmpleado("Sofía", "Ramírez", "sofia.ramirez@innova.com", "Backend Dev", 3800000.0, c2);

            crearEmpleado("Andrés", "Castro", "andres.castro@digital.com", "Frontend Dev", 3300000.0, c3);
            crearEmpleado("Valentina", "Herrera", "valentina.herrera@digital.com", "QA Engineer", 2900000.0, c3);
            crearEmpleado("Felipe", "Mora", "felipe.mora@digital.com", "Arquitecto", 5000000.0, c3);

            unitOfWork.save();
            logger.info("Datos iniciales insertados correctamente.");
        } else {
            logger.info("La base de datos ya tiene datos, se omite el seed.");
        }
    }

    private void crearEmpleado(String nombre, String apellido, String correo,
                               String cargo, Double salario, Compania compania) {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setApellido(apellido);
        e.setCorreo(correo);
        e.setCargo(cargo);
        e.setSalario(salario);
        e.setCompania(compania);
        unitOfWork.getEmpleados().save(e);
    }
}
package com.example.empresasapi.application.services;

import com.example.empresasapi.application.dtos.CompaniaDTO;
import com.example.empresasapi.application.dtos.CompaniaConEmpleadosDTO;
import com.example.empresasapi.domain.entities.Compania;
import com.example.empresasapi.domain.entities.Empleado;
import com.example.empresasapi.domain.interfaces.IUnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CompaniaService {

    private static final Logger logger = LoggerFactory.getLogger(CompaniaService.class);

    @Autowired
    private IUnitOfWork unitOfWork;

    public List<Compania> getAll() {
        logger.info("Consultando todas las compañías");
        return unitOfWork.getCompanias().findAll();
    }

    public Optional<Compania> getById(Long id) {
        logger.info("Consultando compañía con id: {}", id);
        return unitOfWork.getCompanias().findById(id);
    }

    @Transactional
    public Compania create(CompaniaDTO dto) {
        logger.info("Creando compañía: {}", dto.getNombre());
        Compania compania = new Compania();
        compania.setNombre(dto.getNombre());
        compania.setDireccion(dto.getDireccion());
        compania.setTelefono(dto.getTelefono());
        compania.setFechaCreacion(LocalDate.now());
        Compania saved = unitOfWork.getCompanias().save(compania);
        unitOfWork.save();
        logger.info("Compañía creada correctamente con id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Optional<Compania> update(Long id, CompaniaDTO dto) {
        logger.info("Actualizando compañía con id: {}", id);
        return unitOfWork.getCompanias().findById(id).map(compania -> {
            compania.setNombre(dto.getNombre());
            compania.setDireccion(dto.getDireccion());
            compania.setTelefono(dto.getTelefono());
            Compania updated = unitOfWork.getCompanias().save(compania);
            unitOfWork.save();
            return updated;
        });
    }

    @Transactional
    public boolean delete(Long id) {
        logger.info("Eliminando compañía con id: {}", id);
        return unitOfWork.getCompanias().findById(id).map(compania -> {
            unitOfWork.getCompanias().delete(compania);
            unitOfWork.save();
            return true;
        }).orElse(false);
    }

    @Transactional
    public Compania createConEmpleados(CompaniaConEmpleadosDTO dto) {
        logger.info("Iniciando transacción: crear compañía con empleados");
        Compania compania = new Compania();
        compania.setNombre(dto.getNombre());
        compania.setDireccion(dto.getDireccion());
        compania.setTelefono(dto.getTelefono());
        compania.setFechaCreacion(LocalDate.now());
        unitOfWork.getCompanias().save(compania);

        dto.getEmpleados().forEach(empDto -> {
            Empleado empleado = new Empleado();
            empleado.setNombre(empDto.getNombre());
            empleado.setApellido(empDto.getApellido());
            empleado.setCorreo(empDto.getCorreo());
            empleado.setCargo(empDto.getCargo());
            empleado.setSalario(empDto.getSalario());
            empleado.setCompania(compania);
            unitOfWork.getEmpleados().save(empleado);
        });

        unitOfWork.save();
        logger.info("Transacción completada: compañía y empleados guardados");
        return compania;
    }
}

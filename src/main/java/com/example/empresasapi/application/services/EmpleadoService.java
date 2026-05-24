package com.example.empresasapi.application.services;

import com.example.empresasapi.application.dtos.EmpleadoDTO;
import com.example.empresasapi.domain.entities.Empleado;
import com.example.empresasapi.domain.interfaces.IUnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);

    @Autowired
    private IUnitOfWork unitOfWork;

    public List<Empleado> getAll() {
        logger.info("Consultando todos los empleados");
        return unitOfWork.getEmpleados().findAll();
    }

    public Optional<Empleado> getById(Long id) {
        logger.info("Consultando empleado con id: {}", id);
        return unitOfWork.getEmpleados().findById(id);
    }

    public List<Empleado> getByCompaniaId(Long companiaId) {
        logger.info("Consultando empleados de compañía id: {}", companiaId);
        return unitOfWork.getEmpleados().findByCompaniaId(companiaId);
    }

    @Transactional
    public Empleado create(EmpleadoDTO dto) {
        logger.info("Creando empleado: {} {}", dto.getNombre(), dto.getApellido());
        return unitOfWork.getCompanias().findById(dto.getCompaniaId()).map(compania -> {
            Empleado empleado = new Empleado();
            empleado.setNombre(dto.getNombre());
            empleado.setApellido(dto.getApellido());
            empleado.setCorreo(dto.getCorreo());
            empleado.setCargo(dto.getCargo());
            empleado.setSalario(dto.getSalario());
            empleado.setCompania(compania);
            Empleado saved = unitOfWork.getEmpleados().save(empleado);
            unitOfWork.save();
            logger.info("Empleado creado con id: {}", saved.getId());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Compañía no encontrada con id: " + dto.getCompaniaId()));
    }

    @Transactional
    public Optional<Empleado> update(Long id, EmpleadoDTO dto) {
        logger.info("Actualizando empleado con id: {}", id);
        return unitOfWork.getEmpleados().findById(id).map(empleado -> {
            empleado.setNombre(dto.getNombre());
            empleado.setApellido(dto.getApellido());
            empleado.setCorreo(dto.getCorreo());
            empleado.setCargo(dto.getCargo());
            empleado.setSalario(dto.getSalario());
            Empleado updated = unitOfWork.getEmpleados().save(empleado);
            unitOfWork.save();
            return updated;
        });
    }

    @Transactional
    public boolean delete(Long id) {
        logger.info("Eliminando empleado con id: {}", id);
        return unitOfWork.getEmpleados().findById(id).map(empleado -> {
            unitOfWork.getEmpleados().delete(empleado);
            unitOfWork.save();
            return true;
        }).orElse(false);
    }
}



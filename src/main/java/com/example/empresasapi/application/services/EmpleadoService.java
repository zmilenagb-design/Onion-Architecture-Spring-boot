package com.example.empresasapi.application.services;

import com.example.empresasapi.application.dtos.EmpleadoDTO;
import com.example.empresasapi.application.dtos.EmpleadoPatchDTO;
import com.example.empresasapi.application.dtos.PaginadoDTO;
import com.example.empresasapi.application.exceptions.BusinessException;
import com.example.empresasapi.domain.entities.Empleado;
import com.example.empresasapi.domain.interfaces.IUnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);

    @Autowired
    private IUnitOfWork unitOfWork;

    @Autowired
    private NotificacionService notificacionService;

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

        // Validación 1: la compañía debe existir
        if (!unitOfWork.getCompanias().existsById(dto.getCompaniaId())) {
            throw new BusinessException("companiaId",
                    "No existe una compañía con el id: " + dto.getCompaniaId());
        }

        // Validación 2: el correo no puede estar duplicado
        if (unitOfWork.getEmpleados().existsByCorreo(dto.getCorreo())) {
            throw new BusinessException("correo",
                    "El correo " + dto.getCorreo() + " ya está registrado");
        }

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
            notificacionService.enviarBienvenida(saved.getCorreo(), saved.getNombre());
            return saved;
        }).orElseThrow(() -> new RuntimeException(
                "Compañía no encontrada con id: " + dto.getCompaniaId()));
    }

    @Transactional
    public Optional<Empleado> update(Long id, EmpleadoDTO dto) {
        logger.info("Actualizando empleado con id: {}", id);

        // Validación: si cambia el correo, verificar que no esté en uso por otro empleado
        unitOfWork.getEmpleados().findById(id).ifPresent(existente -> {
            if (!existente.getCorreo().equalsIgnoreCase(dto.getCorreo())
                    && unitOfWork.getEmpleados().existsByCorreo(dto.getCorreo())) {
                throw new BusinessException("correo",
                        "El correo " + dto.getCorreo() + " ya está registrado");
            }
        });

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

    @Transactional
    public List<Empleado> createLote(List<EmpleadoDTO> dtos) {
        logger.info("Creación masiva de {} empleados", dtos.size());
        List<Empleado> lista = new ArrayList<>();
        for (EmpleadoDTO dto : dtos) {
            Empleado empleado = new Empleado();
            empleado.setNombre(dto.getNombre());
            empleado.setApellido(dto.getApellido());
            empleado.setCorreo(dto.getCorreo());
            empleado.setCargo(dto.getCargo());
            empleado.setSalario(dto.getSalario());
            unitOfWork.getCompanias().findById(dto.getCompaniaId())
                    .ifPresent(empleado::setCompania);
            lista.add(empleado);
        }
        unitOfWork.getEmpleados().saveAll(lista);
        unitOfWork.save();
        return lista;
    }

    @Transactional
    public Optional<Empleado> patch(Long id, EmpleadoPatchDTO dto) {
        logger.info("PATCH empleado id: {}", id);
        return unitOfWork.getEmpleados().findById(id).map(emp -> {
            if (dto.getNombre() != null)   emp.setNombre(dto.getNombre());
            if (dto.getApellido() != null) emp.setApellido(dto.getApellido());
            if (dto.getCorreo() != null)   emp.setCorreo(dto.getCorreo());
            if (dto.getCargo() != null)    emp.setCargo(dto.getCargo());
            if (dto.getSalario() != null)  emp.setSalario(dto.getSalario());
            Empleado updated = unitOfWork.getEmpleados().save(emp);
            unitOfWork.save();
            return updated;
        });
    }

    @Transactional
    public void deleteLote(List<Long> ids) {
        logger.info("Eliminación masiva de ids: {}", ids);
        unitOfWork.getEmpleados().deleteAllByIds(ids);
        unitOfWork.save();
    }

    public PaginadoDTO<Empleado> getPaged(int pagina, int tamano, String orden, String dir, String buscar) {
        logger.info("Listado paginado: pagina={} tamano={} orden={} dir={} buscar={}", pagina, tamano, orden, dir, buscar);
        List<Empleado> datos = unitOfWork.getEmpleados().findPaged(pagina, tamano, orden, dir, buscar);
        long total = unitOfWork.getEmpleados().countFiltered(buscar);
        return new PaginadoDTO<>(datos, pagina, tamano, total);
    }

    public PaginadoDTO<Empleado> getByCompaniaPaged(Long companiaId, int pagina, int tamano) {
        logger.info("Empleados paginados de compañía id: {}", companiaId);
        List<Empleado> datos = unitOfWork.getEmpleados().findByCompaniaPaged(companiaId, pagina, tamano);
        long total = unitOfWork.getEmpleados().countByCompaniaId(companiaId);
        return new PaginadoDTO<>(datos, pagina, tamano, total);
    }
}
package com.example.empresasapi.domain.interfaces;

import com.example.empresasapi.domain.entities.Empleado;
import java.util.List;
import java.util.Optional;

public interface IEmpleadoRepository {
    List<Empleado> findAll();
    Optional<Empleado> findById(Long id);
    List<Empleado> findByCompaniaId(Long companiaId);
    Empleado save(Empleado empleado);
    void delete(Empleado empleado);
}
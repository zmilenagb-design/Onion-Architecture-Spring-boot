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

    // Nuevos para Módulo 1
    void saveAll(List<Empleado> empleados);
    void deleteAllByIds(List<Long> ids);
    List<Empleado> findPaged(int pagina, int tamano, String orden, String dir, String buscar);
    long countFiltered(String buscar);
    List<Empleado> findByCompaniaPaged(Long companiaId, int pagina, int tamano);
    long countByCompaniaId(Long companiaId);
    boolean existsByCorreo(String correo);
}
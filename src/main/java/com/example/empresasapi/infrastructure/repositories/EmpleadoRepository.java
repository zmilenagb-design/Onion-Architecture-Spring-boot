package com.example.empresasapi.infrastructure.repositories;

import com.example.empresasapi.domain.entities.Empleado;
import com.example.empresasapi.domain.interfaces.IEmpleadoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoRepository implements IEmpleadoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Empleado> findAll() {
        return entityManager
                .createQuery("SELECT e FROM Empleado e", Empleado.class)
                .getResultList();
    }

    @Override
    public Optional<Empleado> findById(Long id) {
        Empleado empleado = entityManager.find(Empleado.class, id);
        return Optional.ofNullable(empleado);
    }

    @Override
    public List<Empleado> findByCompaniaId(Long companiaId) {
        return entityManager
                .createQuery("SELECT e FROM Empleado e WHERE e.compania.id = :id", Empleado.class)
                .setParameter("id", companiaId)
                .getResultList();
    }

    @Override
    public Empleado save(Empleado empleado) {
        if (empleado.getId() == null) {
            entityManager.persist(empleado);
            return empleado;
        } else {
            return entityManager.merge(empleado);
        }
    }

    @Override
    public void delete(Empleado empleado) {
        entityManager.remove(
                entityManager.contains(empleado)
                        ? empleado
                        : entityManager.merge(empleado)
        );
    }
}

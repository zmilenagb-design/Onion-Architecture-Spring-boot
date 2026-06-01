package com.example.empresasapi.infrastructure.repositories;

import com.example.empresasapi.domain.entities.Empleado;
import com.example.empresasapi.domain.interfaces.IEmpleadoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
        return Optional.ofNullable(entityManager.find(Empleado.class, id));
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
                entityManager.contains(empleado) ? empleado : entityManager.merge(empleado)
        );
    }

    @Override
    public void saveAll(List<Empleado> empleados) {
        for (Empleado e : empleados) {
            if (e.getId() == null) {
                entityManager.persist(e);
            } else {
                entityManager.merge(e);
            }
        }
    }

    @Override
    public void deleteAllByIds(List<Long> ids) {
        entityManager.createQuery("DELETE FROM Empleado e WHERE e.id IN :ids")
                .setParameter("ids", ids)
                .executeUpdate();
    }

    @Override
    public List<Empleado> findPaged(int pagina, int tamano, String orden, String dir, String buscar) {
        String campoOrden = List.of("nombre", "apellido", "correo", "salario", "cargo")
                .contains(orden) ? orden : "apellido";
        String direccion = "desc".equalsIgnoreCase(dir) ? "DESC" : "ASC";

        String jpql = "SELECT e FROM Empleado e WHERE "
                + "(:buscar IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%',:buscar,'%')) "
                + "OR LOWER(e.apellido) LIKE LOWER(CONCAT('%',:buscar,'%')) "
                + "OR LOWER(e.correo) LIKE LOWER(CONCAT('%',:buscar,'%'))) "
                + "ORDER BY e." + campoOrden + " " + direccion;

        TypedQuery<Empleado> query = entityManager.createQuery(jpql, Empleado.class);
        query.setParameter("buscar", buscar == null || buscar.isBlank() ? null : buscar);
        query.setFirstResult((pagina - 1) * tamano);
        query.setMaxResults(tamano);
        return query.getResultList();
    }

    @Override
    public long countFiltered(String buscar) {
        String jpql = "SELECT COUNT(e) FROM Empleado e WHERE "
                + "(:buscar IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%',:buscar,'%')) "
                + "OR LOWER(e.apellido) LIKE LOWER(CONCAT('%',:buscar,'%')) "
                + "OR LOWER(e.correo) LIKE LOWER(CONCAT('%',:buscar,'%')))";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("buscar", buscar == null || buscar.isBlank() ? null : buscar)
                .getSingleResult();
    }

    @Override
    public List<Empleado> findByCompaniaPaged(Long companiaId, int pagina, int tamano) {
        return entityManager
                .createQuery("SELECT e FROM Empleado e WHERE e.compania.id = :id", Empleado.class)
                .setParameter("id", companiaId)
                .setFirstResult((pagina - 1) * tamano)
                .setMaxResults(tamano)
                .getResultList();
    }

    @Override
    public long countByCompaniaId(Long companiaId) {
        return entityManager
                .createQuery("SELECT COUNT(e) FROM Empleado e WHERE e.compania.id = :id", Long.class)
                .setParameter("id", companiaId)
                .getSingleResult();
    }

    @Override
    public boolean existsByCorreo(String correo) {
        Long count = entityManager
                .createQuery("SELECT COUNT(e) FROM Empleado e WHERE LOWER(e.correo) = LOWER(:correo)", Long.class)
                .setParameter("correo", correo)
                .getSingleResult();
        return count > 0;
    }
}
package com.example.empresasapi.infrastructure.repositories;

import com.example.empresasapi.domain.entities.Compania;
import com.example.empresasapi.domain.interfaces.ICompaniaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class CompaniaRepository implements ICompaniaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Compania> findAll() {
        return entityManager
                .createQuery("SELECT c FROM Compania c", Compania.class)
                .getResultList();
    }

    @Override
    public Optional<Compania> findById(Long id) {
        Compania compania = entityManager.find(Compania.class, id);
        return Optional.ofNullable(compania);
    }

    @Override
    public Compania save(Compania compania) {
        if (compania.getId() == null) {
            entityManager.persist(compania);
            return compania;
        } else {
            return entityManager.merge(compania);
        }
    }

    @Override
    public void delete(Compania compania) {
        entityManager.remove(
                entityManager.contains(compania)
                        ? compania
                        : entityManager.merge(compania)
        );
    }
}
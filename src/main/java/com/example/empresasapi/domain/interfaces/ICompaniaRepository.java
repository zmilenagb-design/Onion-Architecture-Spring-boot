package com.example.empresasapi.domain.interfaces;

import com.example.empresasapi.domain.entities.Compania;
import java.util.List;
import java.util.Optional;

public interface ICompaniaRepository {
    List<Compania> findAll();
    Optional<Compania> findById(Long id);
    Compania save(Compania compania);
    void delete(Compania compania);
    boolean existsById(Long id);
}
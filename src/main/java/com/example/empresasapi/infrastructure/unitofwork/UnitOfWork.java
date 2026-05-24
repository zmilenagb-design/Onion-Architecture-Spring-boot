package com.example.empresasapi.infrastructure.unitofwork;

import com.example.empresasapi.domain.interfaces.ICompaniaRepository;
import com.example.empresasapi.domain.interfaces.IEmpleadoRepository;
import com.example.empresasapi.domain.interfaces.IUnitOfWork;
import com.example.empresasapi.infrastructure.repositories.CompaniaRepository;
import com.example.empresasapi.infrastructure.repositories.EmpleadoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UnitOfWork implements IUnitOfWork {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CompaniaRepository companiaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Override
    public ICompaniaRepository getCompanias() {
        return companiaRepository;
    }

    @Override
    public IEmpleadoRepository getEmpleados() {
        return empleadoRepository;
    }

    @Override
    @Transactional
    public void save() {
        entityManager.flush();
    }
}
package com.example.empresasapi.domain.interfaces;

public interface IUnitOfWork {
    ICompaniaRepository getCompanias();
    IEmpleadoRepository getEmpleados();
    void save();
}
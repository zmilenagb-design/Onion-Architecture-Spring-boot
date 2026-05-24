package com.example.empresasapi.application.dtos;

import java.util.List;

public class CompaniaConEmpleadosDTO {
    private String nombre;
    private String direccion;
    private String telefono;
    private List<EmpleadoDTO> empleados;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public List<EmpleadoDTO> getEmpleados() { return empleados; }
    public void setEmpleados(List<EmpleadoDTO> empleados) { this.empleados = empleados; }
}
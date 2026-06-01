package com.example.empresasapi.application.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;

public class EmpleadoPatchDTO {
    private String nombre;
    private String apellido;

    @Email(message = "Formato de correo inválido")
    private String correo;

    private String cargo;

    @Min(value = 0, message = "El salario no puede ser negativo")
    private Double salario;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public Double getSalario() { return salario; }
    public void setSalario(Double salario) { this.salario = salario; }
}

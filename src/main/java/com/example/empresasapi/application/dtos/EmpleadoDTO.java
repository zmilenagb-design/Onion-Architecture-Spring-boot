package com.example.empresasapi.application.dtos;

import jakarta.validation.constraints.*;

public class EmpleadoDTO {

    private Long id;

    @NotBlank(message = "El nombre del empleado es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido del empleado es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correo;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 100, message = "El cargo no puede superar los 100 caracteres")
    private String cargo;

    @NotNull(message = "El salario es obligatorio")
    @Min(value = 0, message = "El salario no puede ser negativo")
    private Double salario;

    @NotNull(message = "El ID de la compañía es obligatorio")
    private Long companiaId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Long getCompaniaId() { return companiaId; }
    public void setCompaniaId(Long companiaId) { this.companiaId = companiaId; }
}
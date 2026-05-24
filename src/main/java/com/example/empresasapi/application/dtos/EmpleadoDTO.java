package com.example.empresasapi.application.dtos;

public class EmpleadoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String cargo;
    private Double salario;
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

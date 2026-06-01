package com.example.empresasapi.application.dtos;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class EliminarLoteDTO {

    @NotEmpty(message = "Debe enviar al menos un ID")
    private List<Long> ids;

    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }
}
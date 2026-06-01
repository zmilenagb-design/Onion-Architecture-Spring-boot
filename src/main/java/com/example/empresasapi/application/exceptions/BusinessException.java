package com.example.empresasapi.application.exceptions;

public class BusinessException extends RuntimeException {

    private final String campo;

    public BusinessException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}

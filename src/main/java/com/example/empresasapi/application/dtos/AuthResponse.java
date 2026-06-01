package com.example.empresasapi.application.dtos;

import com.example.empresasapi.domain.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    // Token JWT generado — el cliente lo guarda y lo envía en cada request
    private String token;

    // Información del usuario autenticado
    private Long id;
    private String username;
    private String email;
    private Rol rol;
}
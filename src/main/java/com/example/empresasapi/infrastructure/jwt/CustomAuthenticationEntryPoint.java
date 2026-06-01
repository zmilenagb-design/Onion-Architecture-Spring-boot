package com.example.empresasapi.infrastructure.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        // Configurar la respuesta como JSON con status 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Construir el cuerpo del mensaje de error
        Map<String, Object> body = new HashMap<>();
        body.put("status", 401);
        body.put("error", "No autorizado");
        body.put("message", "Acceso denegado: token ausente o inválido");
        body.put("path", request.getServletPath());

        // Escribir el JSON en la respuesta
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}
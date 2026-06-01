package com.example.empresasapi.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class NotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);

    @Async("taskExecutor")
    public CompletableFuture<String> enviarBienvenida(String correo, String nombre) {
        logger.info("[{}] Iniciando envío de bienvenida a: {}",
                Thread.currentThread().getName(), correo);
        try {
            Thread.sleep(2000); // simula tiempo real de envío de correo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String mensaje = "Bienvenido/a " + nombre
                + ", tu cuenta fue registrada con: " + correo;
        logger.info("[{}] Bienvenida enviada correctamente a: {}",
                Thread.currentThread().getName(), correo);
        return CompletableFuture.completedFuture(mensaje);
    }

    @Async("taskExecutor")
    public CompletableFuture<String> generarReporteEmpleados(Long companiaId) {
        logger.info("[{}] Generando reporte para compañía id: {}",
                Thread.currentThread().getName(), companiaId);
        try {
            Thread.sleep(3000); // simula procesamiento pesado
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String reporte = "Reporte generado para compañía id: " + companiaId
                + " | Timestamp: " + System.currentTimeMillis();
        logger.info("[{}] Reporte listo para compañía id: {}",
                Thread.currentThread().getName(), companiaId);
        return CompletableFuture.completedFuture(reporte);
    }
}
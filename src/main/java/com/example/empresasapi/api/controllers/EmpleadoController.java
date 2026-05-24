package com.example.empresasapi.api.controllers;

import com.example.empresasapi.application.dtos.EmpleadoDTO;
import com.example.empresasapi.application.services.EmpleadoService;
import com.example.empresasapi.domain.entities.Empleado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<Empleado>> getAll() {
        logger.info("GET /api/empleados");
        return ResponseEntity.ok(empleadoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getById(@PathVariable Long id) {
        logger.info("GET /api/empleados/{}", id);
        return empleadoService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Empleado> create(@RequestBody EmpleadoDTO dto) {
        logger.info("POST /api/empleados");
        Empleado created = empleadoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleado> update(@PathVariable Long id, @RequestBody EmpleadoDTO dto) {
        logger.info("PUT /api/empleados/{}", id);
        return empleadoService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /api/empleados/{}", id);
        boolean deleted = empleadoService.delete(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}


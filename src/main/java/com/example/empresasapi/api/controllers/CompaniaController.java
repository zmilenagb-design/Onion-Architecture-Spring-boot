package com.example.empresasapi.api.controllers;

import com.example.empresasapi.application.dtos.CompaniaConEmpleadosDTO;
import com.example.empresasapi.application.dtos.CompaniaDTO;
import com.example.empresasapi.application.services.CompaniaService;
import com.example.empresasapi.application.services.EmpleadoService;
import com.example.empresasapi.domain.entities.Compania;
import com.example.empresasapi.domain.entities.Empleado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companias")
public class CompaniaController {

    private static final Logger logger = LoggerFactory.getLogger(CompaniaController.class);

    @Autowired
    private CompaniaService companiaService;

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<Compania>> getAll() {
        logger.info("GET /api/companias");
        return ResponseEntity.ok(companiaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compania> getById(@PathVariable Long id) {
        logger.info("GET /api/companias/{}", id);
        return companiaService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Compania> create(@RequestBody CompaniaDTO dto) {
        logger.info("POST /api/companias");
        Compania created = companiaService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compania> update(@PathVariable Long id, @RequestBody CompaniaDTO dto) {
        logger.info("PUT /api/companias/{}", id);
        return companiaService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /api/companias/{}", id);
        boolean deleted = companiaService.delete(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/empleados")
    public ResponseEntity<List<Empleado>> getEmpleados(@PathVariable Long id) {
        logger.info("GET /api/companias/{}/empleados", id);
        return ResponseEntity.ok(empleadoService.getByCompaniaId(id));
    }

    @PostMapping("/con-empleados")
    public ResponseEntity<Compania> createConEmpleados(@RequestBody CompaniaConEmpleadosDTO dto) {
        logger.info("POST /api/companias/con-empleados");
        Compania created = companiaService.createConEmpleados(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}


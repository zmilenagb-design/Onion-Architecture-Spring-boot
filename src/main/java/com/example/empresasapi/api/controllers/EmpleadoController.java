package com.example.empresasapi.api.controllers;

import com.example.empresasapi.application.dtos.*;
import com.example.empresasapi.application.services.EmpleadoService;
import com.example.empresasapi.domain.entities.Empleado;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);

    @Autowired
    private EmpleadoService empleadoService;

    // GET paginado con filtros
    @GetMapping
    public ResponseEntity<PaginadoDTO<Empleado>> getAll(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "apellido") String orden,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String buscar) {
        logger.info("GET /api/empleados?pagina={}&tamano={}&orden={}&dir={}&buscar={}", pagina, tamano, orden, dir, buscar);
        return ResponseEntity.ok(empleadoService.getPaged(pagina, tamano, orden, dir, buscar));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getById(@PathVariable Long id) {
        logger.info("GET /api/empleados/{}", id);
        return empleadoService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Empleado> create(@Valid @RequestBody EmpleadoDTO dto) {
        logger.info("POST /api/empleados");
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleado> update(@PathVariable Long id, @Valid @RequestBody EmpleadoDTO dto) {
        logger.info("PUT /api/empleados/{}", id);
        return empleadoService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Empleado> patch(@PathVariable Long id, @Valid @RequestBody EmpleadoPatchDTO dto) {
        logger.info("PATCH /api/empleados/{}", id);
        return empleadoService.patch(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("DELETE /api/empleados/{}", id);
        return empleadoService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // POST /api/empleados/lote — creación masiva
    @PostMapping("/lote")
    public ResponseEntity<List<Empleado>> createLote(@Valid @RequestBody List<@Valid EmpleadoDTO> dtos) {
        logger.info("POST /api/empleados/lote - {} empleados", dtos.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.createLote(dtos));
    }

    // DELETE /api/empleados/lote — eliminación múltiple
    @DeleteMapping("/lote")
    public ResponseEntity<Void> deleteLote(@Valid @RequestBody EliminarLoteDTO dto) {
        logger.info("DELETE /api/empleados/lote - ids: {}", dto.getIds());
        empleadoService.deleteLote(dto.getIds());
        return ResponseEntity.noContent().build();
    }
}
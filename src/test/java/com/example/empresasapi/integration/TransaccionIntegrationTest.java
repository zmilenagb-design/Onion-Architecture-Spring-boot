package com.example.empresasapi.integration;

import com.example.empresasapi.application.dtos.EmpleadoDTO;
import com.example.empresasapi.application.services.EmpleadoService;
import com.example.empresasapi.domain.entities.Compania;
import com.example.empresasapi.domain.interfaces.ICompaniaRepository;
import com.example.empresasapi.domain.interfaces.IUnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class TransaccionIntegrationTest {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private IUnitOfWork unitOfWork;

    private Compania compania;

    @BeforeEach
    void setUp() {
        // Crear compañía real en H2 antes de cada prueba
        compania = new Compania();
        compania.setNombre("Tech S.A.S");
        compania.setDireccion("Calle 123");
        compania.setTelefono("3001234567");
        compania.setFechaCreacion(LocalDate.now());
        unitOfWork.getCompanias().save(compania);
        unitOfWork.save();
    }

    // ─── PRUEBA #13 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should rollback transaction when error occurs")
    void shouldRollbackTransactionWhenErrorOccurs() {
        // Arrange
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNombre("Juan");
        dto.setApellido("Perez");
        dto.setCorreo("juan@test.com");
        dto.setCargo("Analista");
        dto.setSalario(3000000.0);
        dto.setCompaniaId(999L); // ID inexistente — forzará rollback

        // Act & Assert
        assertThatThrownBy(() -> empleadoService.create(dto))
                .isInstanceOf(RuntimeException.class);

        // Verificar que ningún empleado fue persistido
        assertThat(unitOfWork.getEmpleados().findAll()).isEmpty();
    }

    // ─── PRUEBA #14 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should persist employee in database")
    void shouldPersistEmployeeInDatabase() {
        // Arrange
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNombre("Carlos");
        dto.setApellido("Perez");
        dto.setCorreo("carlos@test.com");
        dto.setCargo("Desarrollador");
        dto.setSalario(4000000.0);
        dto.setCompaniaId(compania.getId());

        // Act
        empleadoService.create(dto);

        // Assert
        assertThat(unitOfWork.getEmpleados().findAll()).hasSize(1);
        assertThat(unitOfWork.getEmpleados().findAll().get(0).getCorreo())
                .isEqualTo("carlos@test.com");
    }

    // ─── PRUEBA #15 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should persist company in database")
    void shouldPersistCompanyInDatabase() {
        // Arrange — compañía ya creada en setUp()

        // Act
        var resultado = unitOfWork.getCompanias().findById(compania.getId());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Tech S.A.S");
    }

    // ─── PRUEBA #16 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should find employee by email")
    void shouldFindEmployeeByEmail() {
        // Arrange
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNombre("Maria");
        dto.setApellido("Lopez");
        dto.setCorreo("maria@test.com");
        dto.setCargo("QA");
        dto.setSalario(3000000.0);
        dto.setCompaniaId(compania.getId());
        empleadoService.create(dto);

        // Act
        boolean existe = unitOfWork.getEmpleados().existsByCorreo("maria@test.com");

        // Assert
        assertThat(existe).isTrue();
    }

    // ─── PRUEBA #17 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should return paged employees")
    void shouldReturnPagedEmployees() {
        // Arrange — crear 3 empleados
        for (int i = 1; i <= 3; i++) {
            EmpleadoDTO dto = new EmpleadoDTO();
            dto.setNombre("Empleado" + i);
            dto.setApellido("Test");
            dto.setCorreo("empleado" + i + "@test.com");
            dto.setCargo("Cargo");
            dto.setSalario(3000000.0);
            dto.setCompaniaId(compania.getId());
            empleadoService.create(dto);
        }

        // Act
        var resultado = empleadoService.getPaged(1, 2, "apellido", "asc", null);

        // Assert
        assertThat(resultado.getDatos()).hasSize(2);
        assertThat(resultado.getTotal()).isEqualTo(3);
        assertThat(resultado.getTotalPaginas()).isEqualTo(2);
    }
}
package com.example.empresasapi.application.services;

import com.example.empresasapi.application.dtos.EmpleadoDTO;
import com.example.empresasapi.application.exceptions.BusinessException;
import com.example.empresasapi.domain.entities.Compania;
import com.example.empresasapi.domain.entities.Empleado;
import com.example.empresasapi.domain.interfaces.ICompaniaRepository;
import com.example.empresasapi.domain.interfaces.IEmpleadoRepository;
import com.example.empresasapi.domain.interfaces.IUnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private IUnitOfWork unitOfWork;

    @Mock
    private ICompaniaRepository companiaRepository;

    @Mock
    private IEmpleadoRepository empleadoRepository;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private EmpleadoService empleadoService;

    private EmpleadoDTO dto;
    private Compania compania;
    private Empleado empleado;

    @BeforeEach
    void setUp() {
        dto = new EmpleadoDTO();
        dto.setNombre("Laura");
        dto.setApellido("Gomez");
        dto.setCorreo("laura@test.com");
        dto.setCargo("Desarrolladora");
        dto.setSalario(4500000.0);
        dto.setCompaniaId(1L);

        compania = new Compania();
        compania.setId(1L);
        compania.setNombre("Tech S.A.S");

        empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombre("Laura");
        empleado.setApellido("Gomez");
        empleado.setCorreo("laura@test.com");
        empleado.setCargo("Desarrolladora");
        empleado.setSalario(4500000.0);
        empleado.setCompania(compania);

        when(unitOfWork.getCompanias()).thenReturn(companiaRepository);
        when(unitOfWork.getEmpleados()).thenReturn(empleadoRepository);
    }

    // ─── PRUEBA #1 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should create employee successfully when all data is valid")
    void shouldCreateEmployeeSuccessfully() {
        // Arrange
        when(companiaRepository.existsById(1L)).thenReturn(true);
        when(empleadoRepository.existsByCorreo("laura@test.com")).thenReturn(false);
        when(companiaRepository.findById(1L)).thenReturn(Optional.of(compania));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        // Act
        Empleado resultado = empleadoService.create(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Laura");
        assertThat(resultado.getCorreo()).isEqualTo("laura@test.com");
    }

    // ─── PRUEBA #2 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should fail when company does not exist")
    void shouldFailWhenCompanyDoesNotExist() {
        // Arrange
        when(companiaRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> empleadoService.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No existe una compañía con el id");
    }

    // ─── PRUEBA #3 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should fail when email already exists")
    void shouldFailWhenEmailAlreadyExists() {
        // Arrange
        when(companiaRepository.existsById(1L)).thenReturn(true);
        when(empleadoRepository.existsByCorreo("laura@test.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> empleadoService.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya está registrado");
    }

    // ─── PRUEBA #4 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should delete existing employee successfully")
    void shouldDeleteExistingEmployee() {
        // Arrange
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));

        // Act
        boolean resultado = empleadoService.delete(1L);

        // Assert
        assertThat(resultado).isTrue();
        verify(empleadoRepository).delete(empleado);
    }

    // ─── PRUEBA #5 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should return false when deleting non existing employee")
    void shouldReturnFalseWhenDeletingNonExistingEmployee() {
        // Arrange
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        boolean resultado = empleadoService.delete(99L);

        // Assert
        assertThat(resultado).isFalse();
        verify(empleadoRepository, never()).delete(any(Empleado.class));
    }

    // ─── PRUEBA #6 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should update employee successfully")
    void shouldUpdateEmployeeSuccessfully() {
        // Arrange
        EmpleadoDTO dtoActualizado = new EmpleadoDTO();
        dtoActualizado.setNombre("Laura Actualizada");
        dtoActualizado.setApellido("Gomez");
        dtoActualizado.setCorreo("laura@test.com");
        dtoActualizado.setCargo("Tech Lead");
        dtoActualizado.setSalario(6000000.0);
        dtoActualizado.setCompaniaId(1L);

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        // Act
        var resultado = empleadoService.update(1L, dtoActualizado);

        // Assert
        assertThat(resultado).isPresent();
        verify(empleadoRepository).save(any(Empleado.class));
    }

    // ─── PRUEBA #8 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should call save when creating employee")
    void shouldCallSaveWhenCreatingEmployee() {
        // Arrange
        when(companiaRepository.existsById(1L)).thenReturn(true);
        when(empleadoRepository.existsByCorreo("laura@test.com")).thenReturn(false);
        when(companiaRepository.findById(1L)).thenReturn(Optional.of(compania));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        // Act
        empleadoService.create(dto);

        // Assert
        verify(empleadoRepository).save(any(Empleado.class));
        verify(unitOfWork).save();
    }

    // ─── PRUEBA #9 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should never call delete when employee is not found")
    void shouldNeverCallDeleteWhenEmployeeNotFound() {
        // Arrange
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        empleadoService.delete(99L);

        // Assert
        verify(empleadoRepository, never()).delete(any(Empleado.class));
        verify(unitOfWork, never()).save();
    }

    // ─── PRUEBA #10 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should fail when email is invalid")
    void shouldFailWhenEmailIsInvalid() {
        // Arrange
        dto.setCorreo("correo-invalido");
        when(companiaRepository.existsById(1L)).thenReturn(true);
        when(empleadoRepository.existsByCorreo("correo-invalido")).thenReturn(false);
        when(companiaRepository.findById(1L)).thenReturn(Optional.of(compania));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        // Act
        Empleado resultado = empleadoService.create(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getCorreo()).isNotEqualTo("correo-invalido");
    }

    // ─── PRUEBA #11 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should fail when name is empty")
    void shouldFailWhenNameIsEmpty() {
        // Arrange
        dto.setNombre("");
        when(companiaRepository.existsById(1L)).thenReturn(true);
        when(empleadoRepository.existsByCorreo("laura@test.com")).thenReturn(false);
        when(companiaRepository.findById(1L)).thenReturn(Optional.of(compania));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        // Act
        Empleado resultado = empleadoService.create(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isNotEmpty();
    }

    // ─── PRUEBA #12 ───────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should fail when salary is negative")
    void shouldFailWhenSalaryIsNegative() {
        // Arrange
        dto.setSalario(-1000.0);
        when(companiaRepository.existsById(1L)).thenReturn(true);
        when(empleadoRepository.existsByCorreo("laura@test.com")).thenReturn(false);
        when(companiaRepository.findById(1L)).thenReturn(Optional.of(compania));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        // Act
        Empleado resultado = empleadoService.create(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getSalario()).isPositive();
    }
}
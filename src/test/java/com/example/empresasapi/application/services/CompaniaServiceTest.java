package com.example.empresasapi.application.services;

import com.example.empresasapi.application.dtos.CompaniaDTO;
import com.example.empresasapi.domain.entities.Compania;
import com.example.empresasapi.domain.interfaces.ICompaniaRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CompaniaServiceTest {

    @Mock
    private IUnitOfWork unitOfWork;

    @Mock
    private ICompaniaRepository companiaRepository;

    @InjectMocks
    private CompaniaService companiaService;

    private CompaniaDTO dto;
    private Compania compania;

    @BeforeEach
    void setUp() {
        dto = new CompaniaDTO();
        dto.setNombre("Tech S.A.S");
        dto.setDireccion("Calle 123");
        dto.setTelefono("3001234567");

        compania = new Compania();
        compania.setId(1L);
        compania.setNombre("Tech S.A.S");
        compania.setDireccion("Calle 123");
        compania.setTelefono("3001234567");

        when(unitOfWork.getCompanias()).thenReturn(companiaRepository);
    }

    // ─── PRUEBA #7 ────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Should create company successfully when all data is valid")
    void shouldCreateCompanySuccessfully() {
        // Arrange
        when(companiaRepository.save(any(Compania.class))).thenReturn(compania);

        // Act
        Compania resultado = companiaService.create(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Tech S.A.S");
        assertThat(resultado.getTelefono()).isEqualTo("3001234567");
        verify(companiaRepository).save(any(Compania.class));
    }
}
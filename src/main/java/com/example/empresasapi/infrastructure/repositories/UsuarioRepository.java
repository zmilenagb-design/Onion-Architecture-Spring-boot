package com.example.empresasapi.infrastructure.repositories;

import com.example.empresasapi.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca usuario por username — usado en login y UserDetailsService
    Optional<Usuario> findByUsername(String username);

    // Busca usuario por email — usado para validar duplicados en registro
    Optional<Usuario> findByEmail(String email);

    // Verifica si ya existe un username — evita duplicados en registro
    boolean existsByUsername(String username);

    // Verifica si ya existe un email — evita duplicados en registro
    boolean existsByEmail(String email);
}
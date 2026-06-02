package com.example.empresasapi.domain.entities;

import com.example.empresasapi.domain.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    // ─── Campos de la entidad ───────────────────────────────────────────────

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "compania_id")
    private Long companiaId;

    @Column(name = "limite_salario")
    private Double limiteSalario;

    // ─── Métodos de UserDetails ─────────────────────────────────────────────

    // Le dice a Spring Security qué permisos/roles tiene este usuario
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    // Spring Security usa este método para obtener el nombre de usuario
    @Override
    public String getUsername() {
        return username;
    }

    // Spring Security usa este método para obtener la contraseña (ya encriptada)
    @Override
    public String getPassword() {
        return password;
    }

    // La cuenta no está expirada (sin lógica de expiración por ahora)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // La cuenta no está bloqueada (sin lógica de bloqueo por ahora)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Las credenciales no están expiradas (sin lógica de expiración por ahora)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // El usuario está habilitado (sin lógica de habilitación por ahora)
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Asigna automáticamente la fecha de creación antes de persistir
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
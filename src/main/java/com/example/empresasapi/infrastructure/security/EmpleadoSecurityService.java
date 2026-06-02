package com.example.empresasapi.infrastructure.security;

import com.example.empresasapi.application.exceptions.ResourceNotFoundException;
import com.example.empresasapi.infrastructure.jwt.JwtService;
import com.example.empresasapi.infrastructure.repositories.EmpleadoRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component("empleadoSecurity")
@RequiredArgsConstructor
@Slf4j
public class EmpleadoSecurityService {

    private final EmpleadoRepository empleadoRepository;
    private final JwtService jwtService;

    public boolean isOwner(Long empleadoId, Authentication authentication) {

        // 1. Buscar el empleado — si no existe, lanzar 404 antes de evaluar ownership
        var empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empleado no encontrado con id: " + empleadoId));

        // 2. Si el usuario es ADMIN, tiene acceso sin restricción
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (esAdmin) {
            log.info("Acceso concedido por rol ADMIN al empleado id: {}", empleadoId);
            return true;
        }

        // 3. Extraer el token JWT del request actual
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Acceso denegado — no se encontró token en el request. Usuario: {}",
                    authentication.getName());
            return false;
        }

        String token = authHeader.substring(7);

        // 4. Extraer companiaId del token
        Object companiaIdClaim = jwtService.extractClaim(token, "companiaId");

        if (companiaIdClaim == null) {
            log.warn("Acceso denegado — claim companiaId no encontrado. Usuario: {}",
                    authentication.getName());
            return false;
        }

        // 5. Comparar companiaId del token con el del empleado
        Long tokenCompaniaId = Long.valueOf(companiaIdClaim.toString());
        Long empleadoCompaniaId = empleado.getCompania() != null
                ? empleado.getCompania().getId() : null;

        if (tokenCompaniaId.equals(empleadoCompaniaId)) {
            log.info("Acceso concedido por ownership. Usuario: {}, Compañía: {}",
                    authentication.getName(), tokenCompaniaId);
            return true;
        }

        log.warn("Acceso denegado — ownership no coincide. Usuario: {}, Token companiaId: {}, Empleado companiaId: {}",
                authentication.getName(), tokenCompaniaId, empleadoCompaniaId);
        return false;
    }

    public boolean puedeAsignarSalario(Double salario, Authentication authentication) {

        // ADMIN no tiene restricción de salario
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (esAdmin) return true;

        // Extraer el token del request actual
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Acceso denegado — no se encontró token. Usuario: {}",
                    authentication.getName());
            return false;
        }

        String token = authHeader.substring(7);

        // Extraer limiteSalario del token
        Object limiteClaim = jwtService.extractClaim(token, "limiteSalario");

        if (limiteClaim == null) {
            // Si no tiene límite definido, se permite cualquier salario
            return true;
        }

        Double limite = Double.valueOf(limiteClaim.toString());

        if (salario <= limite) {
            return true;
        }

        log.warn("Acceso denegado — salario {} supera el límite {}. Usuario: {}",
                salario, limite, authentication.getName());
        return false;
    }
}
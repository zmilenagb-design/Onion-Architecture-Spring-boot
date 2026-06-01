package com.example.empresasapi.infrastructure.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Leer el header "Authorization" del request
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza con "Bearer ", dejar pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (quitar el prefijo "Bearer ")
        final String jwt = authHeader.substring(7);

        try {
            // 4. Extraer el username del token
            final String username = jwtService.extractUsername(jwt);

            // 5. Si hay username y el usuario aún no está autenticado en este request
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Cargar el usuario desde la base de datos
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 7. Validar que el token sea válido para este usuario
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // 8. Crear el objeto de autenticación de Spring Security
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,                        // Sin credenciales (ya validamos con JWT)
                            userDetails.getAuthorities() // Roles del usuario
                    );

                    // 9. Agregar detalles del request (IP, session, etc.)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 10. Registrar la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token inválido, expirado o malformado — dejar pasar sin autenticar
            // Spring Security retornará 401 automáticamente
            SecurityContextHolder.clearContext();
        }

        // 11. Continuar con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}
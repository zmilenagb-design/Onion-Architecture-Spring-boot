package com.example.empresasapi.application.services;


import com.example.empresasapi.application.dtos.AuthResponse;
import com.example.empresasapi.application.dtos.LoginRequest;
import com.example.empresasapi.application.dtos.RegisterRequest;
import com.example.empresasapi.domain.entities.Usuario;
import com.example.empresasapi.infrastructure.jwt.JwtService;
import com.example.empresasapi.infrastructure.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class    AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    // ─── Registro ────────────────────────────────────────────────────────────

    public AuthResponse register(RegisterRequest request) {

        // 1. Verificar que el username no exista ya en la BD
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya está en uso: " + request.getUsername());
        }

        // 2. Verificar que el email no exista ya en la BD
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + request.getEmail());
        }

        // 3. Construir el usuario con password encriptado (nunca texto plano)
        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol())
                .companiaId(request.getCompaniaId())
                .limiteSalario(request.getLimiteSalario())
                .build();

        // 4. Guardar el usuario en la BD
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // 5. Generar el token JWT para el nuevo usuario
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("companiaId", usuarioGuardado.getCompaniaId());
        extraClaims.put("limiteSalario", usuarioGuardado.getLimiteSalario());
        String token = jwtService.generateToken(extraClaims, usuarioGuardado);

        // 6. Retornar la respuesta con el token e info del usuario
        return AuthResponse.builder()
                .token(token)
                .id(usuarioGuardado.getId())
                .username(usuarioGuardado.getUsername())
                .email(usuarioGuardado.getEmail())
                .rol(usuarioGuardado.getRol())
                .build();
    }

    // ─── Login ───────────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {

        // 1. Autenticar con AuthenticationManager (valida username + password)
        //    Si las credenciales son incorrectas, lanza una excepción automáticamente
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Buscar el usuario en la BD (ya sabemos que existe porque authenticate() pasó)
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Generar el token JWT
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("companiaId", usuario.getCompaniaId());
        extraClaims.put("limiteSalario", usuario.getLimiteSalario());
        String token = jwtService.generateToken(extraClaims, usuario);

        // 4. Retornar la respuesta con el token e info del usuario
        return AuthResponse.builder()
                .token(token)
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }
}
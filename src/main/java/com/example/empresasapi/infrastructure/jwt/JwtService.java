package com.example.empresasapi.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Lee la clave secreta desde application.properties
    @Value("${app.jwt.secret}")
    private String secretKey;

    // Lee el tiempo de expiración desde application.properties (en ms)
    @Value("${app.jwt.expiration}")
    private long expirationTime;

    // ─── Métodos públicos ────────────────────────────────────────────────────

    // Genera un token JWT para el usuario dado (sin claims extra)
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Genera un token JWT con claims adicionales (ej: rol)
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)                          // Claims adicionales (vacío por ahora)
                .subject(userDetails.getUsername())           // El "sujeto" del token = username
                .issuedAt(new Date(System.currentTimeMillis()))         // Fecha de emisión
                .expiration(new Date(System.currentTimeMillis() + expirationTime)) // Fecha de expiración
                .signWith(getSecretKey())                     // Firma con la clave secreta
                .compact();                                   // Construye el token como String
    }

    // Verifica si el token es válido para el usuario dado
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // El token es válido si el username coincide y no está expirado
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Extrae el username (subject) del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrae un claim personalizado del token por su nombre
    public Object extractClaim(String token, String claimName) {
        return extractAllClaims(token).get(claimName);
    }

    // ─── Métodos privados ────────────────────────────────────────────────────

    // Extrae un claim específico del token usando una función
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrae todos los claims del token (verifica la firma internamente)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())   // Verifica que la firma sea válida
                .build()
                .parseSignedClaims(token)
                .getPayload();                // Retorna el payload con todos los claims
    }

    // Verifica si el token ya expiró
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extrae la fecha de expiración del token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Convierte la clave secreta (String) en un objeto SecretKey para firmar/verificar
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
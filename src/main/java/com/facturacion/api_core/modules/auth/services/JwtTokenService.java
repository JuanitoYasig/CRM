package com.facturacion.api_core.modules.auth.services;

import com.facturacion.api_core.modules.auth.domain.model.Rol;
import com.facturacion.api_core.modules.auth.domain.model.UserPrincipal;
import com.facturacion.api_core.modules.auth.domain.model.Usuario;
import com.facturacion.api_core.shared.exception.TenantAccessDeniedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servicio de emisión y validación de tokens JWT (RFC 7519).
 * Implementa firma HMAC-SHA256 (HS256) con prevención de ataques de temporización (Timing Attacks).
 * Codifica las claims de Tenant y Rol para aislamiento estricto (OWASP A01).
 */
@Service
public class JwtTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String JWT_HEADER_B64 = Base64.getUrlEncoder().withoutPadding()
            .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));

    private final byte[] secretKeyBytes;
    private final long expirationSeconds;

    public JwtTokenService(
            @Value("${jwt.secret:gad_crm_secret_key_super_segura_2026_con_mas_de_256_bits_de_longitud!}") String secret,
            @Value("${jwt.expiration-seconds:28800}") long expirationSeconds) {
        this.secretKeyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    /**
     * Genera un token JWT para un usuario institucional.
     */
    public String generarToken(Usuario usuario) {
        long iat = Instant.now().getEpochSecond();
        long exp = iat + expirationSeconds;

        String payloadJson = String.format(
                "{\"iss\":\"gad-crm\",\"sub\":\"%s\",\"userId\":%d,\"tenantId\":\"%s\",\"departamento\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                escapeJson(usuario.getUsername()),
                usuario.getId(),
                escapeJson(usuario.getTenantId()),
                escapeJson(usuario.getDepartamento()),
                usuario.getRol().name(),
                iat,
                exp
        );

        String payloadB64 = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

        String dataToSign = JWT_HEADER_B64 + "." + payloadB64;
        String signatureB64 = sign(dataToSign);

        return dataToSign + "." + signatureB64;
    }

    /**
     * Valida el token y extrae el UserPrincipal autenticado.
     */
    public UserPrincipal validarYExtraerPrincipal(String token) {
        if (token == null || token.isBlank()) {
            throw new TenantAccessDeniedException("Token de autorización no proporcionado");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new TenantAccessDeniedException("Formato de token JWT inválido");
        }

        String dataToSign = parts[0] + "." + parts[1];
        String expectedSignature = sign(dataToSign);

        // Validación en tiempo constante (previene ataques de temporización)
        if (!MessageDigest.isEqual(expectedSignature.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new TenantAccessDeniedException("Firma de token JWT inválida o alterada");
        }

        // Decodificar payload
        byte[] payloadBytes;
        try {
            payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
        } catch (IllegalArgumentException e) {
            throw new TenantAccessDeniedException("Codificación de token ilegible");
        }

        String payload = new String(payloadBytes, StandardCharsets.UTF_8);

        long exp = extractLongClaim(payload, "exp");
        if (Instant.now().getEpochSecond() > exp) {
            throw new TenantAccessDeniedException("La sesión institucional ha expirado. Por favor inicie sesión nuevamente.");
        }

        Long userId = extractLongClaim(payload, "userId");
        String username = extractStringClaim(payload, "sub");
        String tenantId = extractStringClaim(payload, "tenantId");
        String departamento = extractStringClaim(payload, "departamento");
        String roleStr = extractStringClaim(payload, "role");

        Rol rol = Rol.valueOf(roleStr);

        return new UserPrincipal(userId, username, null, username, tenantId, departamento, rol);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secretKeyBytes, HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception e) {
            throw new IllegalStateException("Error al firmar token criptográfico", e);
        }
    }

    private String extractStringClaim(String json, String claimName) {
        Pattern pattern = Pattern.compile("\"" + claimName + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new TenantAccessDeniedException("Claim obligatoria no encontrada en el token: " + claimName);
    }

    private long extractLongClaim(String json, String claimName) {
        Pattern pattern = Pattern.compile("\"" + claimName + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw new TenantAccessDeniedException("Claim numérica no encontrada en el token: " + claimName);
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"");
    }
}

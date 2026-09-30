package com.facturacion.api_core.modules.auth.dto;

/**
 * DTO de respuesta para una sesión iniciada exitosamente con JWT.
 */
public record LoginResponse(
        String token,
        String tokenType,
        String username,
        String nombreCompleto,
        String departamento,
        String rol,
        String rolDescripcion,
        String tenantId,
        long expiraEnSegundos
) {
}

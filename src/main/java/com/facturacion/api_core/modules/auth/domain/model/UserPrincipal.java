package com.facturacion.api_core.modules.auth.domain.model;

/**
 * Representa la identidad autenticada del usuario dentro del ciclo de vida del request.
 */
public record UserPrincipal(
        Long id,
        String username,
        String email,
        String nombreCompleto,
        String tenantId,
        String departamento,
        Rol rol
) {
}

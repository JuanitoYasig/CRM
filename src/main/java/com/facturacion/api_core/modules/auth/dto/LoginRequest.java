package com.facturacion.api_core.modules.auth.dto;

/**
 * DTO para la petición de inicio de sesión de funcionarios del GAD.
 */
public record LoginRequest(
        String username,
        String password,
        String tenantId
) {
}

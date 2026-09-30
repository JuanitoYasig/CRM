package com.facturacion.api_core.modules.auth.dto;

import com.facturacion.api_core.modules.auth.domain.model.Rol;

/**
 * DTO para la creación de un nuevo funcionario dentro del GAD.
 */
public record RegistrarUsuarioRequest(
        String username,
        String password,
        String email,
        String nombreCompleto,
        String departamento,
        Rol rol
) {
}

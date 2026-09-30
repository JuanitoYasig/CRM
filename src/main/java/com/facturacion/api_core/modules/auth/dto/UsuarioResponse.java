package com.facturacion.api_core.modules.auth.dto;

import com.facturacion.api_core.modules.auth.domain.model.Usuario;
import java.time.LocalDateTime;

/**
 * DTO para la representación pública segura de un usuario (ocultando passwords y hashes).
 */
public record UsuarioResponse(
        Long id,
        String tenantId,
        String username,
        String email,
        String nombreCompleto,
        String departamento,
        String rol,
        String rolDescripcion,
        boolean activo,
        boolean bloqueado,
        LocalDateTime createdAt
) {
    public static UsuarioResponse fromDomain(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getTenantId(),
                u.getUsername(),
                u.getEmail(),
                u.getNombreCompleto(),
                u.getDepartamento(),
                u.getRol().name(),
                u.getRol().getDescripcion(),
                u.isActivo(),
                u.estaBloqueado(),
                u.getCreatedAt()
        );
    }
}

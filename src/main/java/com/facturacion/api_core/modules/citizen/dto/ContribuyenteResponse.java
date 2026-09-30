package com.facturacion.api_core.modules.citizen.dto;

import com.facturacion.api_core.modules.citizen.domain.model.Contribuyente;
import java.time.LocalDateTime;

public record ContribuyenteResponse(
        Long id,
        String tenantId,
        String tipoIdentificacion,
        String tipoDescripcion,
        String numeroIdentificacion,
        String nombres,
        String apellidos,
        String nombreCompleto,
        String email,
        String telefono,
        DireccionDTO direccion,
        boolean activo,
        LocalDateTime createdAt
) {
    public static ContribuyenteResponse fromDomain(Contribuyente c) {
        return new ContribuyenteResponse(
                c.getId(),
                c.getTenantId(),
                c.getTipoIdentificacion().name(),
                c.getTipoIdentificacion().getDescripcion(),
                c.getNumeroIdentificacion(),
                c.getNombres(),
                c.getApellidos(),
                c.getNombreCompleto(),
                c.getEmail(),
                c.getTelefono(),
                DireccionDTO.fromDomain(c.getDireccion()),
                c.isActivo(),
                c.getCreatedAt()
        );
    }
}

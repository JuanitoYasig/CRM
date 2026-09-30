package com.facturacion.api_core.modules.citizen.dto;

import com.facturacion.api_core.modules.citizen.domain.model.TipoIdentificacion;

public record RegistrarContribuyenteRequest(
        TipoIdentificacion tipoIdentificacion,
        String numeroIdentificacion,
        String nombres,
        String apellidos,
        String email,
        String telefono,
        DireccionDTO direccion
) {
}

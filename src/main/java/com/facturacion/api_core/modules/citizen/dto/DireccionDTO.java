package com.facturacion.api_core.modules.citizen.dto;

import com.facturacion.api_core.modules.citizen.domain.model.Direccion;

public record DireccionDTO(
        String callePrincipal,
        String calleSecundaria,
        String numeroPredio,
        String referencia,
        String parroquia
) {
    public Direccion toDomain() {
        return new Direccion(callePrincipal, calleSecundaria, numeroPredio, referencia, parroquia);
    }

    public static DireccionDTO fromDomain(Direccion direccion) {
        if (direccion == null) return null;
        return new DireccionDTO(
                direccion.getCallePrincipal(),
                direccion.getCalleSecundaria(),
                direccion.getNumeroPredio(),
                direccion.getReferencia(),
                direccion.getParroquia()
        );
    }
}

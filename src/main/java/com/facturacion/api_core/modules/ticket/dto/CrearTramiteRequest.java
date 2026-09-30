package com.facturacion.api_core.modules.ticket.dto;

import com.facturacion.api_core.modules.ticket.domain.model.CanalRecepcion;
import com.facturacion.api_core.modules.ticket.domain.model.PrioridadTramite;

public record CrearTramiteRequest(
        Long contribuyenteId,
        String tipoServicio,
        String asunto,
        String descripcion,
        String departamentoDestino,
        PrioridadTramite prioridad,
        CanalRecepcion canal
) {
}

package com.facturacion.api_core.modules.ticket.dto;

import com.facturacion.api_core.modules.ticket.domain.model.Tramite;

import java.time.LocalDateTime;

public record TramiteResponse(
        Long id,
        String tenantId,
        String codigoTramite,
        String asunto,
        String descripcion,
        String departamentoDestino,
        Long contribuyenteId,
        String estado,
        String estadoDescripcion,
        String prioridad,
        String canal,
        LocalDateTime fechaVencimientoSla,
        boolean estaVencido,
        LocalDateTime fechaResolucion,
        String resolucionTexto,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TramiteResponse fromDomain(Tramite t) {
        return new TramiteResponse(
                t.getId(),
                t.getTenantId(),
                t.getCodigoTramite(),
                t.getAsunto(),
                t.getDescripcion(),
                t.getDepartamentoDestino(),
                t.getContribuyenteId(),
                t.getEstado().name(),
                t.getEstado().getDescripcion(),
                t.getPrioridad().name(),
                t.getCanal().name(),
                t.getFechaVencimientoSla(),
                t.estaVencido(),
                t.getFechaResolucion(),
                t.getResolucionTexto(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}

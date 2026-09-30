package com.facturacion.api_core.modules.ticket.dto;

import java.util.Map;

public record DashboardMetricasResponse(
        String tenantId,
        long totalTramites,
        long tramitesCreados,
        long tramitesEnRevision,
        long tramitesDerivados,
        long tramitesResueltos,
        long tramitesCerrados,
        long tramitesVencidosSla,
        Map<String, Long> tramitesPorDepartamento
) {
}

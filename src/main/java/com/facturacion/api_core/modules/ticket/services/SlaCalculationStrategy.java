package com.facturacion.api_core.modules.ticket.services;

import com.facturacion.api_core.modules.ticket.domain.model.PrioridadTramite;
import java.time.LocalDateTime;

public interface SlaCalculationStrategy {

    boolean supports(String tipoServicio);

    LocalDateTime calcularFechaVencimiento(PrioridadTramite prioridad);
}

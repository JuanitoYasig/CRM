package com.facturacion.api_core.modules.ticket.services;

import com.facturacion.api_core.modules.ticket.domain.model.PrioridadTramite;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmergenciaVialSlaStrategy implements SlaCalculationStrategy {

    @Override
    public boolean supports(String tipoServicio) {
        return "EMERGENCIA_VIAL".equalsIgnoreCase(tipoServicio) || "AGUA_POTABLE_ROTURA".equalsIgnoreCase(tipoServicio);
    }

    @Override
    public LocalDateTime calcularFechaVencimiento(PrioridadTramite prioridad) {
        return LocalDateTime.now().plusHours(6);
    }
}

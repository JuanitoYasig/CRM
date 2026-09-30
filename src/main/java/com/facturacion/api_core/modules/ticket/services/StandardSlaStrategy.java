package com.facturacion.api_core.modules.ticket.services;

import com.facturacion.api_core.modules.ticket.domain.model.PrioridadTramite;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StandardSlaStrategy implements SlaCalculationStrategy {

    @Override
    public boolean supports(String tipoServicio) {
        return tipoServicio == null || (!tipoServicio.equalsIgnoreCase("EMERGENCIA_VIAL") && !tipoServicio.equalsIgnoreCase("AGUA_POTABLE_ROTURA"));
    }

    @Override
    public LocalDateTime calcularFechaVencimiento(PrioridadTramite prioridad) {
        int horas = (prioridad != null) ? prioridad.getHorasMaximasResolucion() : PrioridadTramite.MEDIA.getHorasMaximasResolucion();
        return LocalDateTime.now().plusHours(horas);
    }
}

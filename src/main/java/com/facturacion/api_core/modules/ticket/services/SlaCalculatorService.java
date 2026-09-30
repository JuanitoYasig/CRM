package com.facturacion.api_core.modules.ticket.services;

import com.facturacion.api_core.modules.ticket.domain.model.PrioridadTramite;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SlaCalculatorService {

    private final List<SlaCalculationStrategy> strategies;
    private final StandardSlaStrategy fallbackStrategy;

    public SlaCalculatorService(List<SlaCalculationStrategy> strategies, StandardSlaStrategy fallbackStrategy) {
        this.strategies = strategies;
        this.fallbackStrategy = fallbackStrategy;
    }

    public LocalDateTime calcularVencimiento(String tipoServicio, PrioridadTramite prioridad) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(tipoServicio))
                .findFirst()
                .orElse(fallbackStrategy)
                .calcularFechaVencimiento(prioridad);
    }
}

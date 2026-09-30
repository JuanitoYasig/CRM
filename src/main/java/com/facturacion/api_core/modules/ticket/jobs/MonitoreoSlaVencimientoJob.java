package com.facturacion.api_core.modules.ticket.jobs;

import com.facturacion.api_core.modules.ticket.domain.model.Tramite;
import com.facturacion.api_core.modules.ticket.domain.repository.TramiteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MonitoreoSlaVencimientoJob {

    private static final Logger log = LoggerFactory.getLogger(MonitoreoSlaVencimientoJob.class);

    private final TramiteRepository tramiteRepository;

    public MonitoreoSlaVencimientoJob(TramiteRepository tramiteRepository) {
        this.tramiteRepository = tramiteRepository;
    }

    @Scheduled(fixedRate = 1800000, initialDelay = 60000)
    public void ejecutarMonitoreoSla() {
        log.info("[JOB SLA] Iniciando barrido programado de acuerdos de servicio de tramites...");

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime ventanaAlerta = ahora.plusHours(2);

        List<Tramite> tramitesEnRiesgo = tramiteRepository.findTramitesPendientesPorVencer("gad-central", ventanaAlerta);

        long vencidos = tramitesEnRiesgo.stream().filter(Tramite::estaVencido).count();
        long proximos = tramitesEnRiesgo.size() - vencidos;

        log.info("[JOB SLA] Barrido finalizado. Tramites vencidos: {}, Tramites en riesgo inminente: {}",
                vencidos, proximos);
    }
}

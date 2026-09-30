package com.facturacion.api_core.modules.ticket.actions;

import com.facturacion.api_core.modules.citizen.domain.repository.ContribuyenteRepository;
import com.facturacion.api_core.modules.ticket.domain.model.Tramite;
import com.facturacion.api_core.modules.ticket.domain.repository.TramiteRepository;
import com.facturacion.api_core.modules.ticket.dto.CrearTramiteRequest;
import com.facturacion.api_core.modules.ticket.dto.TramiteResponse;
import com.facturacion.api_core.modules.ticket.services.SlaCalculatorService;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CrearTramiteAction {

    private final TramiteRepository tramiteRepository;
    private final ContribuyenteRepository contribuyenteRepository;
    private final SlaCalculatorService slaCalculatorService;

    public CrearTramiteAction(TramiteRepository tramiteRepository,
                              ContribuyenteRepository contribuyenteRepository,
                              SlaCalculatorService slaCalculatorService) {
        this.tramiteRepository = tramiteRepository;
        this.contribuyenteRepository = contribuyenteRepository;
        this.slaCalculatorService = slaCalculatorService;
    }

    @Transactional
    public TramiteResponse execute(CrearTramiteRequest request) {
        Objects.requireNonNull(request, "La solicitud de tramite no puede ser nula");
        String tenantId = TenantContext.getTenantId();

        contribuyenteRepository.findByIdAndTenantId(request.contribuyenteId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Contribuyente", request.contribuyenteId()));

        LocalDateTime fechaVencimiento = slaCalculatorService.calcularVencimiento(
                request.tipoServicio(),
                request.prioridad()
        );

        String codigoTramite = generarCodigoTramite();

        Tramite tramite = new Tramite(
                codigoTramite,
                request.asunto(),
                request.descripcion(),
                request.departamentoDestino(),
                request.contribuyenteId(),
                request.prioridad(),
                request.canal(),
                fechaVencimiento
        );

        Tramite guardado = tramiteRepository.save(tramite);
        return TramiteResponse.fromDomain(guardado);
    }

    private String generarCodigoTramite() {
        int year = Year.now().getValue();
        int randomSeq = ThreadLocalRandom.current().nextInt(10000, 99999);
        return String.format("GAD-%d-%d", year, randomSeq);
    }
}

package com.facturacion.api_core.modules.ticket.actions;

import com.facturacion.api_core.modules.ticket.domain.model.Tramite;
import com.facturacion.api_core.modules.ticket.domain.repository.TramiteRepository;
import com.facturacion.api_core.modules.ticket.dto.DerivarTramiteRequest;
import com.facturacion.api_core.modules.ticket.dto.TramiteResponse;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DerivarTramiteAction {

    private final TramiteRepository tramiteRepository;

    public DerivarTramiteAction(TramiteRepository tramiteRepository) {
        this.tramiteRepository = tramiteRepository;
    }

    @Transactional
    public TramiteResponse execute(Long tramiteId, DerivarTramiteRequest request) {
        String tenantId = TenantContext.getTenantId();
        Tramite tramite = tramiteRepository.findByIdAndTenantId(tramiteId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tramite", tramiteId));

        tramite.derivar(request.nuevoDepartamento(), request.observacion());
        Tramite actualizado = tramiteRepository.save(tramite);
        return TramiteResponse.fromDomain(actualizado);
    }
}

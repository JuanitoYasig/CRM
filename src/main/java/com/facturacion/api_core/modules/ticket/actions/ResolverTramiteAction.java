package com.facturacion.api_core.modules.ticket.actions;

import com.facturacion.api_core.modules.ticket.domain.model.Tramite;
import com.facturacion.api_core.modules.ticket.domain.repository.TramiteRepository;
import com.facturacion.api_core.modules.ticket.dto.ResolverTramiteRequest;
import com.facturacion.api_core.modules.ticket.dto.TramiteResponse;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ResolverTramiteAction {

    private final TramiteRepository tramiteRepository;

    public ResolverTramiteAction(TramiteRepository tramiteRepository) {
        this.tramiteRepository = tramiteRepository;
    }

    @Transactional
    public TramiteResponse execute(Long tramiteId, ResolverTramiteRequest request) {
        String tenantId = TenantContext.getTenantId();
        Tramite tramite = tramiteRepository.findByIdAndTenantId(tramiteId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tramite", tramiteId));

        tramite.resolver(request.resolucionTexto());
        Tramite resuelto = tramiteRepository.save(tramite);
        return TramiteResponse.fromDomain(resuelto);
    }
}

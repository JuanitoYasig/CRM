package com.facturacion.api_core.modules.citizen.services;

import com.facturacion.api_core.modules.citizen.domain.repository.ContribuyenteRepository;
import com.facturacion.api_core.modules.citizen.dto.ContribuyenteResponse;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.exception.ResourceNotFoundException;
import com.facturacion.api_core.shared.security.DataSanitizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContribuyenteQueryService {

    private final ContribuyenteRepository contribuyenteRepository;

    public ContribuyenteQueryService(ContribuyenteRepository contribuyenteRepository) {
        this.contribuyenteRepository = contribuyenteRepository;
    }

    public ContribuyenteResponse buscarPorId(Long id) {
        String tenantId = TenantContext.getTenantId();
        return contribuyenteRepository.findByIdAndTenantId(id, tenantId)
                .map(ContribuyenteResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Contribuyente", id));
    }

    public ContribuyenteResponse buscarPorDocumento(String documento) {
        String tenantId = TenantContext.getTenantId();
        String docNormalizado = DataSanitizer.normalizeDocument(documento);

        return contribuyenteRepository.findByNumeroIdentificacionAndTenantId(docNormalizado, tenantId)
                .map(ContribuyenteResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Contribuyente", documento));
    }

    public List<ContribuyenteResponse> listarTodos() {
        String tenantId = TenantContext.getTenantId();
        return contribuyenteRepository.findAllByTenantId(tenantId)
                .stream()
                .map(ContribuyenteResponse::fromDomain)
                .toList();
    }
}

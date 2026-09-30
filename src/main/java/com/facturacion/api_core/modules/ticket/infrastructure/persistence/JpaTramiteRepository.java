package com.facturacion.api_core.modules.ticket.infrastructure.persistence;

import com.facturacion.api_core.modules.ticket.domain.model.EstadoTramite;
import com.facturacion.api_core.modules.ticket.domain.model.Tramite;
import com.facturacion.api_core.modules.ticket.domain.repository.TramiteRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JpaTramiteRepository implements TramiteRepository {

    private final SpringDataTramiteRepository springDataRepository;

    public JpaTramiteRepository(SpringDataTramiteRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<Tramite> findByIdAndTenantId(Long id, String tenantId) {
        return springDataRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public Optional<Tramite> findByCodigoTramiteAndTenantId(String codigoTramite, String tenantId) {
        return springDataRepository.findByCodigoTramiteAndTenantId(codigoTramite, tenantId);
    }

    @Override
    public List<Tramite> findAllByTenantId(String tenantId) {
        return springDataRepository.findAllByTenantId(tenantId);
    }

    @Override
    public List<Tramite> findByTenantIdAndEstado(String tenantId, EstadoTramite estado) {
        return springDataRepository.findByTenantIdAndEstado(tenantId, estado);
    }

    @Override
    public List<Tramite> findByTenantIdAndDepartamentoDestino(String tenantId, String departamentoDestino) {
        return springDataRepository.findByTenantIdAndDepartamentoDestino(tenantId, departamentoDestino);
    }

    @Override
    public List<Tramite> findByTenantIdAndContribuyenteId(String tenantId, Long contribuyenteId) {
        return springDataRepository.findByTenantIdAndContribuyenteId(tenantId, contribuyenteId);
    }

    @Override
    public List<Tramite> findTramitesPendientesPorVencer(String tenantId, LocalDateTime limite) {
        return springDataRepository.findPendientesPorVencer(tenantId, limite);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByTenantIdAndEstado(String tenantId, EstadoTramite estado) {
        return springDataRepository.countByTenantIdAndEstado(tenantId, estado);
    }

    @Override
    public Tramite save(Tramite tramite) {
        return springDataRepository.save(tramite);
    }
}

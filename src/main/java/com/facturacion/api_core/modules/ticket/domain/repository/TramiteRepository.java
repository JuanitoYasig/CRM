package com.facturacion.api_core.modules.ticket.domain.repository;

import com.facturacion.api_core.modules.ticket.domain.model.EstadoTramite;
import com.facturacion.api_core.modules.ticket.domain.model.Tramite;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TramiteRepository {

    Optional<Tramite> findByIdAndTenantId(Long id, String tenantId);

    Optional<Tramite> findByCodigoTramiteAndTenantId(String codigoTramite, String tenantId);

    List<Tramite> findAllByTenantId(String tenantId);

    List<Tramite> findByTenantIdAndEstado(String tenantId, EstadoTramite estado);

    List<Tramite> findByTenantIdAndDepartamentoDestino(String tenantId, String departamentoDestino);

    List<Tramite> findByTenantIdAndContribuyenteId(String tenantId, Long contribuyenteId);

    List<Tramite> findTramitesPendientesPorVencer(String tenantId, LocalDateTime limite);

    long countByTenantId(String tenantId);

    long countByTenantIdAndEstado(String tenantId, EstadoTramite estado);

    Tramite save(Tramite tramite);
}

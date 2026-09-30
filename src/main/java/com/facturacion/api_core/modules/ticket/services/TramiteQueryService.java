package com.facturacion.api_core.modules.ticket.services;

import com.facturacion.api_core.modules.ticket.domain.model.EstadoTramite;
import com.facturacion.api_core.modules.ticket.domain.model.Tramite;
import com.facturacion.api_core.modules.ticket.domain.repository.TramiteRepository;
import com.facturacion.api_core.modules.ticket.dto.DashboardMetricasResponse;
import com.facturacion.api_core.modules.ticket.dto.TramiteResponse;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TramiteQueryService {

    private final TramiteRepository tramiteRepository;

    public TramiteQueryService(TramiteRepository tramiteRepository) {
        this.tramiteRepository = tramiteRepository;
    }

    public TramiteResponse buscarPorId(Long id) {
        String tenantId = TenantContext.getTenantId();
        return tramiteRepository.findByIdAndTenantId(id, tenantId)
                .map(TramiteResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Tramite", id));
    }

    public TramiteResponse buscarPorCodigo(String codigo) {
        String tenantId = TenantContext.getTenantId();
        return tramiteRepository.findByCodigoTramiteAndTenantId(codigo, tenantId)
                .map(TramiteResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Tramite", codigo));
    }

    public List<TramiteResponse> listar(EstadoTramite estado, String departamento) {
        String tenantId = TenantContext.getTenantId();
        List<Tramite> lista;

        if (estado != null) {
            lista = tramiteRepository.findByTenantIdAndEstado(tenantId, estado);
        } else if (departamento != null && !departamento.isBlank()) {
            lista = tramiteRepository.findByTenantIdAndDepartamentoDestino(tenantId, departamento.trim().toUpperCase());
        } else {
            lista = tramiteRepository.findAllByTenantId(tenantId);
        }

        return lista.stream()
                .map(TramiteResponse::fromDomain)
                .toList();
    }

    public DashboardMetricasResponse obtenerMetricasDashboard() {
        String tenantId = TenantContext.getTenantId();
        List<Tramite> todos = tramiteRepository.findAllByTenantId(tenantId);

        long creados = todos.stream().filter(t -> t.getEstado() == EstadoTramite.CREADO).count();
        long enRevision = todos.stream().filter(t -> t.getEstado() == EstadoTramite.EN_REVISION).count();
        long derivados = todos.stream().filter(t -> t.getEstado() == EstadoTramite.DERIVADO).count();
        long resueltos = todos.stream().filter(t -> t.getEstado() == EstadoTramite.RESUELTO).count();
        long cerrados = todos.stream().filter(t -> t.getEstado() == EstadoTramite.CERRADO).count();
        long vencidos = todos.stream().filter(Tramite::estaVencido).count();

        Map<String, Long> porDepartamento = todos.stream()
                .collect(Collectors.groupingBy(Tramite::getDepartamentoDestino, Collectors.counting()));

        return new DashboardMetricasResponse(
                tenantId,
                todos.size(),
                creados,
                enRevision,
                derivados,
                resueltos,
                cerrados,
                vencidos,
                porDepartamento
        );
    }
}

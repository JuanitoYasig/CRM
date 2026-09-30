package com.facturacion.api_core.modules.ticket.infrastructure.persistence;

import com.facturacion.api_core.modules.ticket.domain.model.EstadoTramite;
import com.facturacion.api_core.modules.ticket.domain.model.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataTramiteRepository extends JpaRepository<Tramite, Long> {

    Optional<Tramite> findByIdAndTenantId(Long id, String tenantId);

    Optional<Tramite> findByCodigoTramiteAndTenantId(String codigoTramite, String tenantId);

    List<Tramite> findAllByTenantId(String tenantId);

    List<Tramite> findByTenantIdAndEstado(String tenantId, EstadoTramite estado);

    List<Tramite> findByTenantIdAndDepartamentoDestino(String tenantId, String departamentoDestino);

    List<Tramite> findByTenantIdAndContribuyenteId(String tenantId, Long contribuyenteId);

    long countByTenantId(String tenantId);

    long countByTenantIdAndEstado(String tenantId, EstadoTramite estado);

    @Query("SELECT t FROM Tramite t WHERE t.tenantId = :tenantId AND t.estado NOT IN ('RESUELTO', 'CERRADO') AND t.fechaVencimientoSla <= :limite")
    List<Tramite> findPendientesPorVencer(@Param("tenantId") String tenantId, @Param("limite") LocalDateTime limite);
}

package com.facturacion.api_core.modules.citizen.infrastructure.persistence;

import com.facturacion.api_core.modules.citizen.domain.model.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataContribuyenteRepository extends JpaRepository<Contribuyente, Long> {

    Optional<Contribuyente> findByIdAndTenantId(Long id, String tenantId);

    Optional<Contribuyente> findByNumeroIdentificacionAndTenantId(String numeroIdentificacion, String tenantId);

    boolean existsByNumeroIdentificacionAndTenantId(String numeroIdentificacion, String tenantId);

    List<Contribuyente> findAllByTenantId(String tenantId);
}

package com.facturacion.api_core.modules.citizen.domain.repository;

import com.facturacion.api_core.modules.citizen.domain.model.Contribuyente;
import java.util.List;
import java.util.Optional;

public interface ContribuyenteRepository {

    Optional<Contribuyente> findByIdAndTenantId(Long id, String tenantId);

    Optional<Contribuyente> findByNumeroIdentificacionAndTenantId(String numeroIdentificacion, String tenantId);

    boolean existsByNumeroIdentificacionAndTenantId(String numeroIdentificacion, String tenantId);

    Contribuyente save(Contribuyente contribuyente);

    List<Contribuyente> findAllByTenantId(String tenantId);
}

package com.facturacion.api_core.modules.citizen.infrastructure.persistence;

import com.facturacion.api_core.modules.citizen.domain.model.Contribuyente;
import com.facturacion.api_core.modules.citizen.domain.repository.ContribuyenteRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaContribuyenteRepository implements ContribuyenteRepository {

    private final SpringDataContribuyenteRepository springDataRepository;

    public JpaContribuyenteRepository(SpringDataContribuyenteRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<Contribuyente> findByIdAndTenantId(Long id, String tenantId) {
        return springDataRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public Optional<Contribuyente> findByNumeroIdentificacionAndTenantId(String numeroIdentificacion, String tenantId) {
        return springDataRepository.findByNumeroIdentificacionAndTenantId(numeroIdentificacion, tenantId);
    }

    @Override
    public boolean existsByNumeroIdentificacionAndTenantId(String numeroIdentificacion, String tenantId) {
        return springDataRepository.existsByNumeroIdentificacionAndTenantId(numeroIdentificacion, tenantId);
    }

    @Override
    public Contribuyente save(Contribuyente contribuyente) {
        return springDataRepository.save(contribuyente);
    }

    @Override
    public List<Contribuyente> findAllByTenantId(String tenantId) {
        return springDataRepository.findAllByTenantId(tenantId);
    }
}

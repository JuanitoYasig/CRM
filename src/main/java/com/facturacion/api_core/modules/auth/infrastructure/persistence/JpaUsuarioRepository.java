package com.facturacion.api_core.modules.auth.infrastructure.persistence;

import com.facturacion.api_core.modules.auth.domain.model.Usuario;
import com.facturacion.api_core.modules.auth.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de infraestructura que implementa UsuarioRepository del dominio (DIP).
 */
@Component
public class JpaUsuarioRepository implements UsuarioRepository {

    private final SpringDataUsuarioRepository springDataRepository;

    public JpaUsuarioRepository(SpringDataUsuarioRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<Usuario> findByIdAndTenantId(Long id, String tenantId) {
        return springDataRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public Optional<Usuario> findByUsernameAndTenantId(String username, String tenantId) {
        return springDataRepository.findByUsernameAndTenantId(username, tenantId);
    }

    @Override
    public boolean existsByUsernameAndTenantId(String username, String tenantId) {
        return springDataRepository.existsByUsernameAndTenantId(username, tenantId);
    }

    @Override
    public boolean existsByEmailAndTenantId(String email, String tenantId) {
        return springDataRepository.existsByEmailAndTenantId(email, tenantId);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return springDataRepository.save(usuario);
    }

    @Override
    public List<Usuario> findAllByTenantId(String tenantId) {
        return springDataRepository.findAllByTenantId(tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }
}

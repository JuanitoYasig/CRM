package com.facturacion.api_core.modules.auth.domain.repository;

import com.facturacion.api_core.modules.auth.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de repositorio del Dominio para el Agregado Usuario (DIP).
 */
public interface UsuarioRepository {

    Optional<Usuario> findByIdAndTenantId(Long id, String tenantId);

    Optional<Usuario> findByUsernameAndTenantId(String username, String tenantId);

    boolean existsByUsernameAndTenantId(String username, String tenantId);

    boolean existsByEmailAndTenantId(String email, String tenantId);

    Usuario save(Usuario usuario);

    List<Usuario> findAllByTenantId(String tenantId);

    long countByTenantId(String tenantId);
}

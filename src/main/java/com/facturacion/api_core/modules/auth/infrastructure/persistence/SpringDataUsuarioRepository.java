package com.facturacion.api_core.modules.auth.infrastructure.persistence;

import com.facturacion.api_core.modules.auth.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para Usuarios.
 */
@Repository
public interface SpringDataUsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByIdAndTenantId(Long id, String tenantId);

    Optional<Usuario> findByUsernameAndTenantId(String username, String tenantId);

    boolean existsByUsernameAndTenantId(String username, String tenantId);

    boolean existsByEmailAndTenantId(String email, String tenantId);

    List<Usuario> findAllByTenantId(String tenantId);

    long countByTenantId(String tenantId);

    @Query("SELECT u FROM Usuario u WHERE u.bloqueadoHasta IS NOT NULL AND u.bloqueadoHasta <= :ahora")
    List<Usuario> findUsuariosConBloqueoExpirado(@Param("ahora") LocalDateTime ahora);
}

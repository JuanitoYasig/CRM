package com.facturacion.api_core.modules.auth.services;

import com.facturacion.api_core.modules.auth.domain.repository.UsuarioRepository;
import com.facturacion.api_core.modules.auth.dto.UsuarioResponse;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de consultas institucionales de usuarios y funcionarios.
 */
@Service
@Transactional(readOnly = true)
public class UsuarioQueryService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioQueryService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse buscarPorId(Long id) {
        String tenantId = TenantContext.getTenantId();
        return usuarioRepository.findByIdAndTenantId(id, tenantId)
                .map(UsuarioResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    public UsuarioResponse buscarPorUsername(String username) {
        String tenantId = TenantContext.getTenantId();
        return usuarioRepository.findByUsernameAndTenantId(username.toLowerCase(), tenantId)
                .map(UsuarioResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", username));
    }

    public List<UsuarioResponse> listarTodos() {
        String tenantId = TenantContext.getTenantId();
        return usuarioRepository.findAllByTenantId(tenantId)
                .stream()
                .map(UsuarioResponse::fromDomain)
                .toList();
    }
}

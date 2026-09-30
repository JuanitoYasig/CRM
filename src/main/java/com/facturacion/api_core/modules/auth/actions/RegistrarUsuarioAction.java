package com.facturacion.api_core.modules.auth.actions;

import com.facturacion.api_core.modules.auth.domain.model.Usuario;
import com.facturacion.api_core.modules.auth.domain.repository.UsuarioRepository;
import com.facturacion.api_core.modules.auth.dto.RegistrarUsuarioRequest;
import com.facturacion.api_core.modules.auth.dto.UsuarioResponse;
import com.facturacion.api_core.modules.auth.services.PasswordCryptoService;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.exception.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Action para el registro y provisión de nuevos funcionarios municipales.
 */
@Component
public class RegistrarUsuarioAction {

    private final UsuarioRepository usuarioRepository;
    private final PasswordCryptoService cryptoService;

    public RegistrarUsuarioAction(UsuarioRepository usuarioRepository, PasswordCryptoService cryptoService) {
        this.usuarioRepository = usuarioRepository;
        this.cryptoService = cryptoService;
    }

    @Transactional
    public UsuarioResponse execute(RegistrarUsuarioRequest request) {
        Objects.requireNonNull(request, "Los datos de registro no pueden ser nulos");

        String tenantId = TenantContext.getTenantId();
        String username = request.username() != null ? request.username().trim().toLowerCase() : "";

        if (username.length() < 3) {
            throw new ValidationException("El nombre de usuario debe contener al menos 3 caracteres");
        }

        if (request.password() == null || request.password().length() < 8) {
            throw new ValidationException("La contraseña institucional debe tener al menos 8 caracteres");
        }

        if (usuarioRepository.existsByUsernameAndTenantId(username, tenantId)) {
            throw new ValidationException("El nombre de usuario ya está asignado en esta jurisdicción municipal");
        }

        if (request.email() != null && usuarioRepository.existsByEmailAndTenantId(request.email().trim().toLowerCase(), tenantId)) {
            throw new ValidationException("El correo electrónico ya se encuentra registrado");
        }

        // Hashing criptográfico robusto
        PasswordCryptoService.HashResult hashResult = cryptoService.hashPassword(request.password());

        Usuario nuevoUsuario = new Usuario(
                username,
                hashResult.hash(),
                hashResult.salt(),
                request.email(),
                request.nombreCompleto(),
                request.departamento(),
                request.rol()
        );

        Usuario guardado = usuarioRepository.save(nuevoUsuario);
        return UsuarioResponse.fromDomain(guardado);
    }
}

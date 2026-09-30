package com.facturacion.api_core.modules.auth.actions;

import com.facturacion.api_core.modules.auth.domain.model.Usuario;
import com.facturacion.api_core.modules.auth.domain.repository.UsuarioRepository;
import com.facturacion.api_core.modules.auth.dto.LoginRequest;
import com.facturacion.api_core.modules.auth.dto.LoginResponse;
import com.facturacion.api_core.modules.auth.services.JwtTokenService;
import com.facturacion.api_core.modules.auth.services.PasswordCryptoService;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.exception.TenantAccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * Action (Caso de uso atómico) para la autenticación institucional de funcionarios.
 * Mitiga OWASP A07 (Identification and Authentication Failures) aplicando:
 *  - Bloqueo por fuerza bruta (5 intentos fallidos -> bloqueo temporal de 15 minutos).
 *  - Mensajes de error genéricos para prevenir la enumeración de usuarios.
 *  - Validación estricta de aislamiento de Tenant.
 */
@Component
public class AutenticarUsuarioAction {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

    private final UsuarioRepository usuarioRepository;
    private final PasswordCryptoService cryptoService;
    private final JwtTokenService jwtTokenService;

    public AutenticarUsuarioAction(UsuarioRepository usuarioRepository,
                                  PasswordCryptoService cryptoService,
                                  JwtTokenService jwtTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.cryptoService = cryptoService;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public LoginResponse execute(LoginRequest request) {
        Objects.requireNonNull(request, "Las credenciales no pueden ser nulas");

        // 1. Establecer el Tenant explícito de la petición o usar el del contexto
        if (request.tenantId() != null && !request.tenantId().isBlank()) {
            TenantContext.setTenantId(request.tenantId());
        }
        String tenantId = TenantContext.getTenantId();

        String usernameNormalizado = request.username() != null ? request.username().trim().toLowerCase() : "";

        // 2. Buscar usuario en la jurisdicción del Tenant
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameAndTenantId(usernameNormalizado, tenantId);

        if (usuarioOpt.isEmpty()) {
            // Mensaje genérico para mitigar enumeración de cuentas (OWASP A07)
            throw new TenantAccessDeniedException("Credenciales institucionales inválidas para esta jurisdicción");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.isActivo()) {
            throw new TenantAccessDeniedException("La cuenta institucional se encuentra inactiva. Contacte al Administrador.");
        }

        // 3. Verificar si la cuenta está bloqueada por ataques de fuerza bruta
        if (usuario.estaBloqueado()) {
            throw new TenantAccessDeniedException("Cuenta bloqueada temporalmente por exceso de intentos fallidos. Intente nuevamente en unos minutos.");
        }

        // 4. Validar contraseña con PBKDF2 y comparación en tiempo constante
        boolean passwordValido = cryptoService.verifyPassword(request.password(), usuario.getPasswordHash(), usuario.getPasswordSalt());

        if (!passwordValido) {
            usuario.registrarIntentoFallido(MAX_FAILED_ATTEMPTS, LOCKOUT_MINUTES);
            usuarioRepository.save(usuario);
            throw new TenantAccessDeniedException("Credenciales institucionales inválidas para esta jurisdicción");
        }

        // 5. Autenticación exitosa: reiniciar contador de fallos
        usuario.resetearIntentos();
        usuarioRepository.save(usuario);

        // 6. Generar JWT firmado
        String token = jwtTokenService.generarToken(usuario);

        return new LoginResponse(
                token,
                "Bearer",
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getDepartamento(),
                usuario.getRol().name(),
                usuario.getRol().getDescripcion(),
                usuario.getTenantId(),
                jwtTokenService.getExpirationSeconds()
        );
    }
}

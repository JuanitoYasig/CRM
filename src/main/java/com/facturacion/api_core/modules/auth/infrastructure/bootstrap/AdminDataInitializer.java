package com.facturacion.api_core.modules.auth.infrastructure.bootstrap;

import com.facturacion.api_core.modules.auth.domain.model.Rol;
import com.facturacion.api_core.modules.auth.domain.model.Usuario;
import com.facturacion.api_core.modules.auth.domain.repository.UsuarioRepository;
import com.facturacion.api_core.modules.auth.services.PasswordCryptoService;
import com.facturacion.api_core.shared.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos semilla (Seed Data).
 * Garantiza que exista un usuario Administrador inicial para la gestión del GAD
 * si la base de datos se encuentra vacía al desplegar.
 */
@Component
public class AdminDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminDataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordCryptoService cryptoService;

    public AdminDataInitializer(UsuarioRepository usuarioRepository, PasswordCryptoService cryptoService) {
        this.usuarioRepository = usuarioRepository;
        this.cryptoService = cryptoService;
    }

    @Override
    public void run(String... args) {
        String defaultTenant = TenantContext.DEFAULT_TENANT;
        TenantContext.setTenantId(defaultTenant);

        try {
            if (!usuarioRepository.existsByUsernameAndTenantId("admin", defaultTenant)) {
                log.info("[BOOTSTRAP] Creando usuario Administrador inicial para el tenant '{}'...", defaultTenant);

                PasswordCryptoService.HashResult hashResult = cryptoService.hashPassword("AdminGAD2026!");

                Usuario admin = new Usuario(
                        "admin",
                        hashResult.hash(),
                        hashResult.salt(),
                        "admin@gad.gob.ec",
                        "Administrador General del GAD",
                        "ALCALDIA_DIRECCION_GENERAL",
                        Rol.ADMIN_GENERAL
                );
                admin.setTenantId(defaultTenant);

                usuarioRepository.save(admin);
                log.info("[BOOTSTRAP] Usuario inicial 'admin' creado exitosamente con credenciales seguras por defecto.");
            }
        } finally {
            TenantContext.clear();
        }
    }
}

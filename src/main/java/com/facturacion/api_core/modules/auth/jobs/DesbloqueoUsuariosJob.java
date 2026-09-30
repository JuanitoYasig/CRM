package com.facturacion.api_core.modules.auth.jobs;

import com.facturacion.api_core.modules.auth.domain.model.Usuario;
import com.facturacion.api_core.modules.auth.infrastructure.persistence.SpringDataUsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Job programado (Background Task) para la auditoría y desbloqueo de cuentas de funcionarios
 * cuyo tiempo de penalización por intentos fallidos ha concluido (OWASP A07).
 */
@Component
public class DesbloqueoUsuariosJob {

    private static final Logger log = LoggerFactory.getLogger(DesbloqueoUsuariosJob.class);

    private final SpringDataUsuarioRepository usuarioRepository;

    public DesbloqueoUsuariosJob(SpringDataUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Scheduled(fixedRate = 300000, initialDelay = 120000) // Cada 5 minutos
    @Transactional
    public void ejecutarDesbloqueoAutomatico() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Usuario> bloqueadosExpirados = usuarioRepository.findUsuariosConBloqueoExpirado(ahora);

        if (!bloqueadosExpirados.isEmpty()) {
            log.info("[JOB SEGURIDAD] Desbloqueando automáticamente {} cuentas de usuario con penalización expirada...",
                    bloqueadosExpirados.size());

            for (Usuario u : bloqueadosExpirados) {
                u.resetearIntentos();
                usuarioRepository.save(u);
                log.info("[SEGURIDAD AUDITORIA] Cuenta '{}' restaurada y operativa en tenant '{}'",
                        u.getUsername(), u.getTenantId());
            }
        }
    }
}

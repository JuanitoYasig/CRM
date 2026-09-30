package com.facturacion.api_core.modules.auth.domain.model;

import com.facturacion.api_core.shared.domain.BaseEntity;
import com.facturacion.api_core.shared.security.DataSanitizer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Agregado raíz que representa un Funcionario o Usuario autenticable del GAD Municipal.
 * Encapsula la política de seguridad contra ataques de fuerza bruta (OWASP A07).
 */
@Entity
@Table(
    name = "crm_usuarios",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_username", columnNames = {"tenant_id", "username"}),
        @UniqueConstraint(name = "uk_tenant_email", columnNames = {"tenant_id", "email"})
    },
    indexes = {
        @Index(name = "idx_usuario_tenant_username", columnList = "tenant_id, username"),
        @Index(name = "idx_usuario_tenant_rol", columnList = "tenant_id, rol")
    }
)
public class Usuario extends BaseEntity {

    @Column(name = "username", nullable = false, length = 60)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "password_salt", nullable = false, length = 64)
    private String passwordSalt;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "departamento", nullable = false, length = 80)
    private String departamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 40)
    private Rol rol;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    protected Usuario() {
    }

    public Usuario(String username, String passwordHash, String passwordSalt, String email,
                   String nombreCompleto, String departamento, Rol rol) {
        this.username = DataSanitizer.sanitizeText(username != null ? username.trim().toLowerCase() : null);
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.nombreCompleto = DataSanitizer.sanitizeText(nombreCompleto);
        this.departamento = departamento != null ? departamento.trim().toUpperCase() : "GENERAL";
        this.rol = rol != null ? rol : Rol.TECNICO_OPERATIVO;
        this.activo = true;
        this.intentosFallidos = 0;
    }

    // Reglas de negocio de seguridad (OWASP A07 - Brute Force Defense)
    public boolean estaBloqueado() {
        if (bloqueadoHasta == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(bloqueadoHasta)) {
            // El periodo de castigo ha terminado, desbloqueo automático
            this.bloqueadoHasta = null;
            this.intentosFallidos = 0;
            return false;
        }
        return true;
    }

    public void registrarIntentoFallido(int limiteMaximo, int minutosBloqueo) {
        this.intentosFallidos++;
        if (this.intentosFallidos >= limiteMaximo) {
            this.bloqueadoHasta = LocalDateTime.now().plusMinutes(minutosBloqueo);
        }
    }

    public void resetearIntentos() {
        this.intentosFallidos = 0;
        this.bloqueadoHasta = null;
    }

    public void cambiarPassword(String nuevoHash, String nuevoSalt) {
        this.passwordHash = nuevoHash;
        this.passwordSalt = nuevoSalt;
        this.resetearIntentos();
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public String getEmail() {
        return email;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getDepartamento() {
        return departamento;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public LocalDateTime getBloqueadoHasta() {
        return bloqueadoHasta;
    }
}

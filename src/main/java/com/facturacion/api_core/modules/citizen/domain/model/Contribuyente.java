package com.facturacion.api_core.modules.citizen.domain.model;

import com.facturacion.api_core.shared.domain.BaseEntity;
import com.facturacion.api_core.shared.exception.ValidationException;
import com.facturacion.api_core.shared.security.DataSanitizer;
import com.facturacion.api_core.shared.util.EcuadorDocumentValidator;
import jakarta.persistence.*;

@Entity
@Table(
    name = "crm_contribuyentes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_identificacion", columnNames = {"tenant_id", "numero_identificacion"})
    },
    indexes = {
        @Index(name = "idx_contribuyente_tenant_doc", columnList = "tenant_id, numero_identificacion"),
        @Index(name = "idx_contribuyente_tenant_email", columnList = "tenant_id, email")
    }
)
public class Contribuyente extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_identificacion", nullable = false, length = 30)
    private TipoIdentificacion tipoIdentificacion;

    @Column(name = "numero_identificacion", nullable = false, length = 20)
    private String numeroIdentificacion;

    @Column(name = "nombres", nullable = false, length = 120)
    private String nombres;

    @Column(name = "apellidos", length = 120)
    private String apellidos;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Embedded
    private Direccion direccion;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    protected Contribuyente() {
    }

    public Contribuyente(TipoIdentificacion tipoIdentificacion, String numeroIdentificacion,
                         String nombres, String apellidos, String email, String telefono, Direccion direccion) {
        validarIdentificacionEcuatoriana(tipoIdentificacion, numeroIdentificacion);
        this.tipoIdentificacion = tipoIdentificacion;
        this.numeroIdentificacion = DataSanitizer.normalizeDocument(numeroIdentificacion);
        this.nombres = DataSanitizer.sanitizeText(nombres);
        this.apellidos = DataSanitizer.sanitizeText(apellidos);
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.telefono = telefono != null ? telefono.trim() : null;
        this.direccion = direccion;
        this.activo = true;
    }

    private void validarIdentificacionEcuatoriana(TipoIdentificacion tipo, String documento) {
        if (!EcuadorDocumentValidator.esDocumentoValido(documento, tipo.name())) {
            throw new ValidationException(String.format("El numero de documento '%s' no es valido para '%s' en Ecuador.",
                    documento, tipo.getDescripcion()));
        }
    }

    public void actualizarDatosContacto(String email, String telefono, Direccion nuevaDireccion) {
        if (email != null && !email.trim().isEmpty()) {
            this.email = email.trim().toLowerCase();
        }
        if (telefono != null && !telefono.trim().isEmpty()) {
            this.telefono = telefono.trim();
        }
        if (nuevaDireccion != null) {
            this.direccion = nuevaDireccion;
        }
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    public String getNombreCompleto() {
        if (apellidos == null || apellidos.isBlank()) {
            return nombres;
        }
        return nombres + " " + apellidos;
    }

    public TipoIdentificacion getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public boolean isActivo() {
        return activo;
    }
}

package com.facturacion.api_core.modules.ticket.domain.model;

import com.facturacion.api_core.shared.domain.BaseEntity;
import com.facturacion.api_core.shared.exception.DomainException;
import com.facturacion.api_core.shared.security.DataSanitizer;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "crm_tramites",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_codigo_tramite", columnNames = {"tenant_id", "codigo_tramite"})
    },
    indexes = {
        @Index(name = "idx_tramite_tenant_estado", columnList = "tenant_id, estado"),
        @Index(name = "idx_tramite_tenant_depto", columnList = "tenant_id, departamento_destino"),
        @Index(name = "idx_tramite_tenant_contribuyente", columnList = "tenant_id, contribuyente_id")
    }
)
public class Tramite extends BaseEntity {

    @Column(name = "codigo_tramite", nullable = false, length = 32)
    private String codigoTramite;

    @Column(name = "asunto", nullable = false, length = 200)
    private String asunto;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "departamento_destino", nullable = false, length = 80)
    private String departamentoDestino;

    @Column(name = "contribuyente_id", nullable = false)
    private Long contribuyenteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoTramite estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false, length = 20)
    private PrioridadTramite prioridad;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false, length = 30)
    private CanalRecepcion canal;

    @Column(name = "fecha_vencimiento_sla", nullable = false)
    private LocalDateTime fechaVencimientoSla;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "resolucion_texto", columnDefinition = "TEXT")
    private String resolucionTexto;

    protected Tramite() {
    }

    public Tramite(String codigoTramite, String asunto, String descripcion, String departamentoDestino,
                   Long contribuyenteId, PrioridadTramite prioridad, CanalRecepcion canal,
                   LocalDateTime fechaVencimientoSla) {
        if (contribuyenteId == null) {
            throw new DomainException("Un tramite municipal debe estar asociado a un contribuyente valido");
        }
        this.codigoTramite = codigoTramite;
        this.asunto = DataSanitizer.sanitizeText(asunto);
        this.descripcion = DataSanitizer.sanitizeText(descripcion);
        this.departamentoDestino = departamentoDestino != null ? departamentoDestino.trim().toUpperCase() : "VENTANILLA_GENERAL";
        this.contribuyenteId = contribuyenteId;
        this.prioridad = prioridad != null ? prioridad : PrioridadTramite.MEDIA;
        this.canal = canal != null ? canal : CanalRecepcion.VENTANILLA_PRESENCIAL;
        this.estado = EstadoTramite.CREADO;
        this.fechaVencimientoSla = fechaVencimientoSla;
    }

    public void derivar(String nuevoDepartamento, String observacion) {
        if (this.estado == EstadoTramite.CERRADO || this.estado == EstadoTramite.RESUELTO) {
            throw new DomainException(String.format("No se puede derivar un tramite que ya esta en estado '%s'", this.estado));
        }
        this.departamentoDestino = nuevoDepartamento.trim().toUpperCase();
        this.estado = EstadoTramite.DERIVADO;
        if (observacion != null && !observacion.isBlank()) {
            this.descripcion += "\n[Derivado a " + this.departamentoDestino + "]: " + DataSanitizer.sanitizeText(observacion);
        }
    }

    public void iniciarRevisionTecnica() {
        if (this.estado == EstadoTramite.CERRADO) {
            throw new DomainException("No se puede iniciar revision de un tramite cerrado");
        }
        this.estado = EstadoTramite.EN_REVISION;
    }

    public void resolver(String textoResolucion) {
        if (textoResolucion == null || textoResolucion.trim().isEmpty()) {
            throw new DomainException("Debe proporcionar un dictamen o resolucion tecnica valida");
        }
        if (this.estado == EstadoTramite.CERRADO) {
            throw new DomainException("El tramite ya fue cerrado previamente");
        }
        this.resolucionTexto = DataSanitizer.sanitizeText(textoResolucion);
        this.fechaResolucion = LocalDateTime.now();
        this.estado = EstadoTramite.RESUELTO;
    }

    public void cerrar() {
        if (this.estado != EstadoTramite.RESUELTO) {
            throw new DomainException("Solo se pueden cerrar tramites que cuenten con resolucion previa");
        }
        this.estado = EstadoTramite.CERRADO;
    }

    public boolean estaVencido() {
        if (this.estado == EstadoTramite.RESUELTO || this.estado == EstadoTramite.CERRADO) {
            return false;
        }
        return LocalDateTime.now().isAfter(this.fechaVencimientoSla);
    }

    public String getCodigoTramite() {
        return codigoTramite;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDepartamentoDestino() {
        return departamentoDestino;
    }

    public Long getContribuyenteId() {
        return contribuyenteId;
    }

    public EstadoTramite getEstado() {
        return estado;
    }

    public PrioridadTramite getPrioridad() {
        return prioridad;
    }

    public CanalRecepcion getCanal() {
        return canal;
    }

    public LocalDateTime getFechaVencimientoSla() {
        return fechaVencimientoSla;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public String getResolucionTexto() {
        return resolucionTexto;
    }
}

package com.facturacion.api_core.modules.ticket.domain.model;

public enum EstadoTramite {
    CREADO("Registrado en Ventanilla / Portal"),
    EN_REVISION("En analisis tecnico"),
    DERIVADO("Derivado a Direccion Operativa"),
    RESUELTO("Atendido y con resolucion emitida"),
    CERRADO("Tramite finalizado y notificado");

    private final String descripcion;

    EstadoTramite(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

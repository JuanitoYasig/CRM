package com.facturacion.api_core.modules.ticket.domain.model;

public enum PrioridadTramite {
    BAJA(120),
    MEDIA(72),
    ALTA(24),
    URGENTE(8);

    private final int horasMaximasResolucion;

    PrioridadTramite(int horasMaximasResolucion) {
        this.horasMaximasResolucion = horasMaximasResolucion;
    }

    public int getHorasMaximasResolucion() {
        return horasMaximasResolucion;
    }
}

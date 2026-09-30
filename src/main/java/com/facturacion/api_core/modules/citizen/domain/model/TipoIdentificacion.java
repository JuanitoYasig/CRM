package com.facturacion.api_core.modules.citizen.domain.model;

public enum TipoIdentificacion {
    CEDULA("Cedula de Ciudadania"),
    RUC_NATURAL("RUC Persona Natural"),
    RUC_PRIVADA("RUC Sociedad Privada"),
    RUC_PUBLICA("RUC Institucion Publica"),
    PASAPORTE("Pasaporte");

    private final String descripcion;

    TipoIdentificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

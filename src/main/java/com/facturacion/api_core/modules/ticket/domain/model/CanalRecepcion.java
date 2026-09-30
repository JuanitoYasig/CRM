package com.facturacion.api_core.modules.ticket.domain.model;

public enum CanalRecepcion {
    VENTANILLA_PRESENCIAL("Ventanilla Unica Municipal"),
    PORTAL_WEB("Portal Web Ciudadano"),
    APP_MOVIL("Aplicacion Movil GAD"),
    LINEA_TELEFONICA("Linea 1800 Municipal");

    private final String canal;

    CanalRecepcion(String canal) {
        this.canal = canal;
    }

    public String getCanal() {
        return canal;
    }
}

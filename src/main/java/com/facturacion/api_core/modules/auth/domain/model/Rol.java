package com.facturacion.api_core.modules.auth.domain.model;

/**
 * Roles institucionales del GAD Municipal para control de acceso basado en roles (RBAC - OWASP A01).
 */
public enum Rol {
    ADMIN_GENERAL("Administrador General del Sistema"),
    DIRECTOR_DEPARTAMENTAL("Director / Jefe de Área Departamental"),
    VENTANILLA_ATENCION("Funcionario de Ventanilla Única"),
    TECNICO_OPERATIVO("Técnico Especialista de Operaciones"),
    AUDITOR_INTERNO("Auditor de Control y Transparencia");

    private final String descripcion;

    Rol(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

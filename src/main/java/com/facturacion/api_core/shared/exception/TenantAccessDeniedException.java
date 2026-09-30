package com.facturacion.api_core.shared.exception;

public class TenantAccessDeniedException extends DomainException {

    public TenantAccessDeniedException(String message) {
        super("TENANT_ACCESS_DENIED", message);
    }
}

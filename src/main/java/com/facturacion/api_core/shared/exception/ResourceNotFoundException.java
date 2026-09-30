package com.facturacion.api_core.shared.exception;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("RESOURCE_NOT_FOUND", String.format("El recurso '%s' con identificador '%s' no existe en esta jurisdiccion", resourceName, identifier));
    }
}

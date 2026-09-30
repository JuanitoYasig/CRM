package com.facturacion.api_core.shared.exception;

public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}

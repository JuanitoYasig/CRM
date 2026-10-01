package com.facturacion.api_core.shared.exception;

import com.facturacion.api_core.shared.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manejador global de excepciones estandarizado.
 * Mitiga OWASP A05 (Security Misconfiguration) evitando que trazas de error o detalles internos
 * de la base de datos se filtren al cliente HTTP.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        return buildResponse(HttpStatus.valueOf(422), ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(TenantAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleTenantDenied(TenantAccessDeniedException ex) {
        log.warn("Alerta de Seguridad Multi-Tenant: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomain(DomainException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        String incidentId = UUID.randomUUID().toString();
        log.error("Error no controlado [Incidente ID: {}]: {}", incidentId, ex.getMessage(), ex);

        // Nunca filtrar stack traces a producción para cumplir con OWASP A05
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Ha ocurrido un error interno en el sistema del GAD. Código de seguimiento: " + incidentId
        );
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.toString());
        body.put("code", code);
        body.put("message", message != null ? message : "Error no especificado");
        body.put("tenantId", TenantContext.getTenantId() != null ? TenantContext.getTenantId() : "NONE");
        return ResponseEntity.status(status).body(body);
    }
}

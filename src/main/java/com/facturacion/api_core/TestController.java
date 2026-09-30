package com.facturacion.api_core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/status")
    public Map<String, String> verificarEstado() {
        // Spring Boot convierte automáticamente este Map (diccionario) en un JSON
        return Map.of(
            "mensaje", "¡Hola desde Spring Boot!",
            "estado", "Online",
            "base_datos", "Conectada a XAMPP"
        );
    }
}
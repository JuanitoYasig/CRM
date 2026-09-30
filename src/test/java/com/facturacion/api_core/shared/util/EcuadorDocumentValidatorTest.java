package com.facturacion.api_core.shared.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Algoritmos de Validacion de Identidad Ecuador")
class EcuadorDocumentValidatorTest {

    @Test
    @DisplayName("Debe validar correctamente cedulas ecuatorianas validas")
    void testCedulasValidas() {
        assertTrue(EcuadorDocumentValidator.esCedulaValida("1710034065"));
        assertTrue(EcuadorDocumentValidator.esCedulaValida("0922488806"));
    }

    @Test
    @DisplayName("Debe rechazar cedulas con longitud o digito verificador invalido")
    void testCedulasInvalidas() {
        assertFalse(EcuadorDocumentValidator.esCedulaValida("1710034069"));
        assertFalse(EcuadorDocumentValidator.esCedulaValida("12345"));
        assertFalse(EcuadorDocumentValidator.esCedulaValida("9910034065"));
        assertFalse(EcuadorDocumentValidator.esCedulaValida("abcdefghij"));
        assertFalse(EcuadorDocumentValidator.esCedulaValida(null));
    }

    @Test
    @DisplayName("Debe validar RUC Persona Natural terminado en 001")
    void testRucPersonaNatural() {
        assertTrue(EcuadorDocumentValidator.esRucPersonaNatural("1710034065001"));
        assertFalse(EcuadorDocumentValidator.esRucPersonaNatural("1710034065000"));
        assertFalse(EcuadorDocumentValidator.esRucPersonaNatural("1710034069001"));
    }
}

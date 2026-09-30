package com.facturacion.api_core.modules.auth.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Criptografía de Contraseñas (OWASP A02)")
class PasswordCryptoServiceTest {

    private final PasswordCryptoService cryptoService = new PasswordCryptoService();

    @Test
    @DisplayName("Debe generar hash con salt y verificar contraseña correctamente")
    void testHashYVerificacionExitosa() {
        String passwordPlana = "MiClaveSegura2026!";
        PasswordCryptoService.HashResult resultado = cryptoService.hashPassword(passwordPlana);

        assertNotNull(resultado.hash());
        assertNotNull(resultado.salt());

        boolean valida = cryptoService.verifyPassword(passwordPlana, resultado.hash(), resultado.salt());
        assertTrue(valida);
    }

    @Test
    @DisplayName("Debe rechazar contraseña incorrecta")
    void testPasswordIncorrecta() {
        String passwordPlana = "MiClaveSegura2026!";
        PasswordCryptoService.HashResult resultado = cryptoService.hashPassword(passwordPlana);

        boolean invalida = cryptoService.verifyPassword("ClaveEquivocada123!", resultado.hash(), resultado.salt());
        assertFalse(invalida);
    }

    @Test
    @DisplayName("Dos hashes de la misma contraseña deben tener salts distintos")
    void testSaltsDistintos() {
        String password = "PasswordComun123!";
        PasswordCryptoService.HashResult r1 = cryptoService.hashPassword(password);
        PasswordCryptoService.HashResult r2 = cryptoService.hashPassword(password);

        assertNotEquals(r1.salt(), r2.salt());
        assertNotEquals(r1.hash(), r2.hash());
    }
}

package com.facturacion.api_core.modules.auth.services;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Servicio criptográfico de contraseñas de alta seguridad.
 * Implementa PBKDF2 con HMAC-SHA-512 y 210,000 iteraciones según directrices de OWASP
 * (OWASP Password Storage Cheat Sheet).
 * Utiliza comparación en tiempo constante (constant-time) para prevenir ataques de canal lateral (Timing Attacks).
 */
@Service
public class PasswordCryptoService {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_BYTES = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    public record HashResult(String hash, String salt) {}

    /**
     * Genera un nuevo salt seguro y calcula el hash de la contraseña.
     */
    public HashResult hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        byte[] salt = new byte[SALT_BYTES];
        secureRandom.nextBytes(salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);

        String hash = computeHash(rawPassword.toCharArray(), salt);
        return new HashResult(hash, saltBase64);
    }

    /**
     * Valida si una contraseña en texto plano coincide con el hash y salt almacenados.
     */
    public boolean verifyPassword(String rawPassword, String storedHash, String storedSalt) {
        if (rawPassword == null || storedHash == null || storedSalt == null) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(storedSalt);
        String computedHash = computeHash(rawPassword.toCharArray(), salt);

        // Comparación en tiempo constante para mitigar ataques de timing
        return MessageDigest.isEqual(storedHash.getBytes(), computedHash.getBytes());
    }

    private String computeHash(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Error al ejecutar derivación criptográfica de claves", e);
        }
    }
}

package com.facturacion.api_core.shared.util;

import java.util.Objects;
import java.util.regex.Pattern;

public final class EcuadorDocumentValidator {

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^\\d+$");
    private static final int PROVINCES_COUNT = 24;

    private EcuadorDocumentValidator() {
    }

    public static boolean esCedulaValida(String cedula) {
        if (cedula == null || cedula.length() != 10 || !NUMERIC_PATTERN.matcher(cedula).matches()) {
            return false;
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if ((provincia < 1 || provincia > PROVINCES_COUNT) && provincia != 30) {
            return false;
        }

        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (tercerDigito < 0 || tercerDigito >= 6) {
            return false;
        }

        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;

        for (int i = 0; i < coeficientes.length; i++) {
            int digito = Character.getNumericValue(cedula.charAt(i));
            int producto = digito * coeficientes[i];
            if (producto >= 10) {
                producto -= 9;
            }
            suma += producto;
        }

        int digitoVerificadorCalculado = (suma % 10 == 0) ? 0 : 10 - (suma % 10);
        int digitoVerificadorReal = Character.getNumericValue(cedula.charAt(9));

        return digitoVerificadorCalculado == digitoVerificadorReal;
    }

    public static boolean esRucPersonaNatural(String ruc) {
        if (ruc == null || ruc.length() != 13 || !NUMERIC_PATTERN.matcher(ruc).matches()) {
            return false;
        }
        if (!ruc.endsWith("001")) {
            return false;
        }
        return esCedulaValida(ruc.substring(0, 10));
    }

    public static boolean esRucSociedadPrivada(String ruc) {
        if (ruc == null || ruc.length() != 13 || !NUMERIC_PATTERN.matcher(ruc).matches()) {
            return false;
        }

        int provincia = Integer.parseInt(ruc.substring(0, 2));
        if ((provincia < 1 || provincia > PROVINCES_COUNT) && provincia != 30) {
            return false;
        }

        int tercerDigito = Character.getNumericValue(ruc.charAt(2));
        if (tercerDigito != 9) {
            return false;
        }

        if (Integer.parseInt(ruc.substring(10, 13)) < 1) {
            return false;
        }

        int[] coeficientes = {4, 3, 2, 7, 6, 5, 4, 3, 2};
        int suma = 0;

        for (int i = 0; i < coeficientes.length; i++) {
            suma += Character.getNumericValue(ruc.charAt(i)) * coeficientes[i];
        }

        int residuo = suma % 11;
        int digitoVerificador = (residuo == 0) ? 0 : 11 - residuo;
        int digitoReal = Character.getNumericValue(ruc.charAt(9));

        return digitoVerificador == digitoReal;
    }

    public static boolean esRucSociedadPublica(String ruc) {
        if (ruc == null || ruc.length() != 13 || !NUMERIC_PATTERN.matcher(ruc).matches()) {
            return false;
        }

        int provincia = Integer.parseInt(ruc.substring(0, 2));
        if ((provincia < 1 || provincia > PROVINCES_COUNT) && provincia != 30) {
            return false;
        }

        int tercerDigito = Character.getNumericValue(ruc.charAt(2));
        if (tercerDigito != 6) {
            return false;
        }

        if (Integer.parseInt(ruc.substring(9, 13)) < 1) {
            return false;
        }

        int[] coeficientes = {3, 2, 7, 6, 5, 4, 3, 2};
        int suma = 0;

        for (int i = 0; i < coeficientes.length; i++) {
            suma += Character.getNumericValue(ruc.charAt(i)) * coeficientes[i];
        }

        int residuo = suma % 11;
        int digitoVerificador = (residuo == 0) ? 0 : 11 - residuo;
        int digitoReal = Character.getNumericValue(ruc.charAt(8));

        return digitoVerificador == digitoReal;
    }

    public static boolean esDocumentoValido(String documento, String tipo) {
        Objects.requireNonNull(documento, "El documento no puede ser nulo");
        Objects.requireNonNull(tipo, "El tipo de documento no puede ser nulo");

        String docTrimmed = documento.trim();
        return switch (tipo.toUpperCase()) {
            case "CEDULA" -> esCedulaValida(docTrimmed);
            case "RUC_NATURAL" -> esRucPersonaNatural(docTrimmed);
            case "RUC_PRIVADA" -> esRucSociedadPrivada(docTrimmed);
            case "RUC_PUBLICA" -> esRucSociedadPublica(docTrimmed);
            case "PASAPORTE" -> docTrimmed.length() >= 5 && docTrimmed.length() <= 20;
            default -> false;
        };
    }
}

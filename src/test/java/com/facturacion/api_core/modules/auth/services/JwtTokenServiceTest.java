package com.facturacion.api_core.modules.auth.services;

import com.facturacion.api_core.modules.auth.domain.model.Rol;
import com.facturacion.api_core.modules.auth.domain.model.UserPrincipal;
import com.facturacion.api_core.modules.auth.domain.model.Usuario;
import com.facturacion.api_core.shared.exception.TenantAccessDeniedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Seguridad JWT (RFC 7519 & OWASP A01/A02)")
class JwtTokenServiceTest {

    private final JwtTokenService jwtTokenService = new JwtTokenService(
            "gad_crm_secret_key_super_segura_2026_con_mas_de_256_bits_de_longitud!",
            3600
    );

    @Test
    @DisplayName("Debe generar token válido y extraer claims correctamente")
    void testGeneracionYValidacion() {
        Usuario usuario = new Usuario(
                "jperez",
                "dummyHash",
                "dummySalt",
                "jperez@gad.gob.ec",
                "Juan Perez",
                "OBRAS_PUBLICAS",
                Rol.TECNICO_OPERATIVO
        );
        usuario.setId(10L);
        usuario.setTenantId("gad-milagro");

        String token = jwtTokenService.generarToken(usuario);
        assertNotNull(token);
        assertTrue(token.contains("."));

        UserPrincipal principal = jwtTokenService.validarYExtraerPrincipal(token);
        assertEquals("jperez", principal.username());
        assertEquals("gad-milagro", principal.tenantId());
        assertEquals(Rol.TECNICO_OPERATIVO, principal.rol());
        assertEquals("OBRAS_PUBLICAS", principal.departamento());
        assertEquals(10L, principal.id());
    }

    @Test
    @DisplayName("Debe rechazar token con firma alterada")
    void testFirmaAlterada() {
        Usuario usuario = new Usuario("jperez", "h", "s", "j@gad.ec", "Juan", "RENTAS", Rol.VENTANILLA_ATENCION);
        usuario.setId(5L);
        usuario.setTenantId("gad-central");

        String token = jwtTokenService.generarToken(usuario);
        String tokenAlterado = token.substring(0, token.length() - 5) + "ABCDE";

        assertThrows(TenantAccessDeniedException.class, () -> jwtTokenService.validarYExtraerPrincipal(tokenAlterado));
    }
}

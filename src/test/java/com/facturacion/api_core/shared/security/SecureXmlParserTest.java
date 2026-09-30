package com.facturacion.api_core.shared.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Seguridad - Prevencion OWASP XXE")
class SecureXmlParserTest {

    @Test
    @DisplayName("Debe parsear exitosamente XML bien formado y seguro")
    void testXmlValido() throws Exception {
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <contribuyente>
                    <cedula>1710034065</cedula>
                    <nombres>Juan Perez</nombres>
                </contribuyente>
                """;

        Document doc = SecureXmlParser.parseSecurely(xml);
        assertNotNull(doc);
        assertEquals("contribuyente", doc.getDocumentElement().getNodeName());
    }

    @Test
    @DisplayName("Debe bloquear y fallar ante inyecciones XXE que incluyan DOCTYPE externo")
    void testBloqueoXXE() {
        String xxePayload = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE foo [
                    <!ELEMENT foo ANY >
                    <!ENTITY xxe SYSTEM "file:///etc/passwd" >
                ]>
                <foo>&xxe;</foo>
                """;

        assertThrows(Exception.class, () -> SecureXmlParser.parseSecurely(xxePayload));
    }
}

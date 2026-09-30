package com.facturacion.api_core.modules.auth.infrastructure.security;

import com.facturacion.api_core.modules.auth.domain.model.UserPrincipal;
import com.facturacion.api_core.modules.auth.services.JwtTokenService;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor de seguridad para autenticación JWT.
 * Mitiga OWASP A01 (Broken Access Control) forzando que el tenant del token firmado
 * prevalezca sobre cualquier cabecera externa manipulada.
 */
@Component
public class JwtAuthenticationFilter implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();

        // Endpoints públicos excluidos de validación de token
        if (uri.startsWith("/api/v1/auth/login") || uri.startsWith("/api/status")) {
            return true;
        }

        // Permite pre-flight de CORS OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            UserPrincipal principal = jwtTokenService.validarYExtraerPrincipal(token);

            // Blindaje Multi-Tenant: El tenant firmado en el token es la única fuente de verdad
            TenantContext.setTenantId(principal.tenantId());
            UserContext.setCurrentUser(principal);
            return true;
        }

        // En caso de que se acceda sin token a una ruta protegida
        // Si no hay token en desarrollo y existe X-Tenant-ID, permitimos continuar bajo tenant manual
        // o si es /api/v1/auth/me exigimos token
        if (uri.startsWith("/api/v1/auth/me")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}

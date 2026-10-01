package com.facturacion.api_core.modules.auth.infrastructure.security;

import com.facturacion.api_core.modules.auth.domain.model.UserPrincipal;
import com.facturacion.api_core.modules.auth.services.JwtTokenService;
import com.facturacion.api_core.shared.context.TenantContext;
import com.facturacion.api_core.shared.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthenticationFilter implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/v1/auth/login") || uri.startsWith("/api/status")) {
            return true;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            UserPrincipal principal = jwtTokenService.validarYExtraerPrincipal(token);

            String tokenTenantId = principal.tenantId();
            
            if (principal.rol() != null && "ADMIN_GENERAL".equals(principal.rol().name())) {
                String requestedTenantId = request.getHeader("X-Tenant-ID");
                if (requestedTenantId != null && !requestedTenantId.isBlank()) {
                    tokenTenantId = requestedTenantId;
                }
            }
            
            TenantContext.setTenantId(tokenTenantId);
            UserContext.setCurrentUser(principal);
            return true;
        }

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

package com.facturacion.api_core.shared.config;

import com.facturacion.api_core.modules.auth.infrastructure.security.JwtAuthenticationFilter;
import com.facturacion.api_core.shared.security.TenantInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring Web MVC.
 * Registra interceptores de seguridad (Tenant y JWT) y establece políticas de CORS seguras.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public WebMvcConfig(TenantInterceptor tenantInterceptor, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.tenantInterceptor = tenantInterceptor;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. Resolver Tenant de la cabecera o fallback
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**");

        // 2. Validar JWT y fijar el Tenant firmado en el token (anti-tamper)
        registry.addInterceptor(jwtAuthenticationFilter)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configuración defensiva de CORS para Frontend React + Vite
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://localhost:3000",
                        "http://127.0.0.1:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-Tenant-ID", "Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

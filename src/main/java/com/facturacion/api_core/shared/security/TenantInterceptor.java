package com.facturacion.api_core.shared.security;

import com.facturacion.api_core.shared.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantHeader = request.getHeader(TenantContext.TENANT_HEADER);
        if (tenantHeader == null || tenantHeader.trim().isEmpty()) {
            tenantHeader = request.getParameter("tenantId");
        }

        TenantContext.setTenantId(tenantHeader);
        MDC.put("tenantId", TenantContext.getTenantId());
        response.setHeader(TenantContext.TENANT_HEADER, TenantContext.getTenantId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
        MDC.remove("tenantId");
    }
}

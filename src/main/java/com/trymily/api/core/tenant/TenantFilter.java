package com.trymily.api.core.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Try to get tenant from Authentication context (JWT claim)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String tenantIdFromJwt = null;

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            tenantIdFromJwt = jwt.getClaimAsString("tenant_id");
        }

        if (tenantIdFromJwt != null && !tenantIdFromJwt.isBlank()) {
            setTenantContext(tenantIdFromJwt, "JWT");
        } else {
            // 2. Fallback to header (useful for public endpoints or initial setup)
            String tenantHeader = request.getHeader(TENANT_HEADER);
            if (tenantHeader != null && !tenantHeader.isBlank()) {
                setTenantContext(tenantHeader, "Header");
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }

    private void setTenantContext(String tenantIdStr, String source) {
        try {
            UUID tenantId = UUID.fromString(tenantIdStr);
            log.trace("Setting tenant context to {} from {}", tenantId, source);
            TenantContextHolder.setTenantId(tenantId);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid tenant ID format from {}: {}", source, tenantIdStr);
        }
    }
}

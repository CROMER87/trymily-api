package com.trymily.api.core.tenant;

import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

@Slf4j
public class TenantContextHolder {

    private static final ThreadLocal<UUID> CONTEXT = new ThreadLocal<>();

    public static void setTenantId(UUID tenantId) {
        log.debug("Setting tenant ID: {}", tenantId);
        CONTEXT.set(tenantId);
    }

    public static UUID getTenantId() {
        return CONTEXT.get();
    }

    public static void clear() {
        log.debug("Clearing tenant ID");
        CONTEXT.remove();
    }
}

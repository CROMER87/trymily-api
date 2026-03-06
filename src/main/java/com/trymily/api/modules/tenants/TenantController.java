package com.trymily.api.modules.tenants;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public List<Tenant> getAll() {
        return tenantService.findAll();
    }

    @GetMapping("/{id}")
    public Tenant getById(@PathVariable UUID id) {
        return tenantService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
    }

    @GetMapping("/slug/{slug}")
    public Tenant getBySlug(@PathVariable String slug) {
        return tenantService.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Tenant create(@RequestBody Tenant tenant) {
        return tenantService.create(tenant);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only salon admins can update their profile
    public Tenant update(@PathVariable UUID id, @RequestBody Tenant tenant) {
        return tenantService.update(id, tenant);
    }
}

package com.trymily.api.modules.tenants;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    public Optional<Tenant> findById(UUID id) {
        return tenantRepository.findById(id);
    }

    @Transactional
    public Tenant create(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant update(UUID id, Tenant tenantDetails) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        tenant.setName(tenantDetails.getName());
        tenant.setAddress(tenantDetails.getAddress());
        tenant.setPhone(tenantDetails.getPhone());
        tenant.setLogoUrl(tenantDetails.getLogoUrl());
        tenant.setSettings(tenantDetails.getSettings());
        tenant.setStatus(tenantDetails.getStatus());

        return tenantRepository.save(tenant);
    }
}

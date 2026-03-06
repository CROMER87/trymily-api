package com.trymily.api.modules.tenants;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
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

    public Optional<Tenant> findBySlug(String slug) {
        return tenantRepository.findBySlug(slug);
    }

    @Transactional
    public Tenant create(Tenant tenant) {
        if (tenant.getSlug() == null || tenant.getSlug().isBlank()) {
            tenant.setSlug(generateSlug(tenant.getName()));
        }
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

        // Regenerate slug when name changes
        tenant.setSlug(generateSlug(tenantDetails.getName()));

        return tenantRepository.save(tenant);
    }

    private String generateSlug(String name) {
        if (name == null) return UUID.randomUUID().toString().substring(0, 8);
        // Remove accents
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        // Lowercase, replace non-alphanumeric with hyphens, collapse multiple hyphens
        String slug = normalized.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        // Check uniqueness, append suffix if needed
        String candidate = slug;
        int counter = 1;
        while (tenantRepository.findBySlug(candidate).isPresent()) {
            candidate = slug + "-" + counter;
            counter++;
        }
        return candidate;
    }
}

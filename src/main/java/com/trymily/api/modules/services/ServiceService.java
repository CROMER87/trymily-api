package com.trymily.api.modules.services;

import com.trymily.api.core.tenant.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public List<com.trymily.api.modules.services.Service> findAll() {
        return serviceRepository.findAll();
    }

    public Optional<com.trymily.api.modules.services.Service> findById(UUID id) {
        return serviceRepository.findById(id);
    }

    public List<com.trymily.api.modules.services.Service> findByTenantId(UUID tenantId) {
        return serviceRepository.findByTenantId(tenantId);
    }

    @Transactional
    public com.trymily.api.modules.services.Service create(com.trymily.api.modules.services.Service service) {
        // Automatically set tenant from context if not provided
        if (service.getTenantId() == null) {
            UUID currentTenantId = TenantContextHolder.getTenantId();
            if (currentTenantId == null) {
                throw new IllegalStateException("Cannot create service without tenant context");
            }
            service.setTenantId(currentTenantId);
        }
        return serviceRepository.save(service);
    }

    @Transactional
    public com.trymily.api.modules.services.Service update(UUID id, com.trymily.api.modules.services.Service details) {
        com.trymily.api.modules.services.Service existing = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        existing.setName(details.getName());
        existing.setDescription(details.getDescription());
        existing.setPrice(details.getPrice());
        existing.setDurationMinutes(details.getDurationMinutes());
        existing.setStatus(details.getStatus());

        return serviceRepository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        serviceRepository.deleteById(id);
    }
}

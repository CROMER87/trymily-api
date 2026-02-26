package com.trymily.api.modules.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public List<Service> getAll() {
        return serviceService.findAll();
    }

    @GetMapping("/{id}")
    public Service getById(@PathVariable UUID id) {
        return serviceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public Service create(@RequestBody Service service) {
        return serviceService.create(service);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Service update(@PathVariable UUID id, @RequestBody Service service) {
        return serviceService.update(id, service);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        serviceService.delete(id);
    }
}

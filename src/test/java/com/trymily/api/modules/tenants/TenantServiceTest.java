package com.trymily.api.modules.tenants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    @Test
    void shouldFindAllTenants() {
        // Given
        Tenant tenant1 = Tenant.builder().name("Tenant 1").build();
        Tenant tenant2 = Tenant.builder().name("Tenant 2").build();
        when(tenantRepository.findAll()).thenReturn(Arrays.asList(tenant1, tenant2));

        // When
        List<Tenant> result = tenantService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(tenant1, tenant2);
        verify(tenantRepository, times(1)).findAll();
    }

    @Test
    void shouldFindTenantById() {
        // Given
        UUID id = UUID.randomUUID();
        Tenant tenant = Tenant.builder().id(id).name("Test Tenant").build();
        when(tenantRepository.findById(id)).thenReturn(Optional.of(tenant));

        // When
        Optional<Tenant> result = tenantService.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(tenant);
        verify(tenantRepository, times(1)).findById(id);
    }

    @Test
    void shouldCreateTenant() {
        // Given
        Tenant tenant = Tenant.builder().name("New Tenant").build();
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        // When
        Tenant result = tenantService.create(tenant);

        // Then
        assertThat(result).isEqualTo(tenant);
        verify(tenantRepository, times(1)).save(tenant);
    }
}

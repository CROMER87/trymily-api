package com.trymily.api.modules.tenants;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TenantRepositoryTest {

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void shouldSaveAndFindTenant() {
        // Given
        Tenant tenant = Tenant.builder().name("Sample Tenant").build();

        // When
        Tenant savedTenant = tenantRepository.save(tenant);
        Optional<Tenant> result = tenantRepository.findById(savedTenant.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Sample Tenant");
        assertThat(result.get().getStatus()).isEqualTo("ACTIVE");
    }
}

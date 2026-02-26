package com.trymily.api.modules.services;

import com.trymily.api.core.tenant.TenantContextHolder;
import com.trymily.api.modules.tenants.Tenant;
import com.trymily.api.modules.tenants.TenantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ServiceRepositoryTest {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private UUID tenant1Id;

    @BeforeEach
    void setUp() {
        TenantContextHolder.clear();
        serviceRepository.deleteAll();
        tenantRepository.deleteAll();

        Tenant tenant1 = tenantRepository.save(Tenant.builder()
                .name("Salon A")
                .status("ACTIVE")
                .build());
        tenant1Id = tenant1.getId();
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldPersistServiceWithTenantId() {
        TenantContextHolder.setTenantId(tenant1Id);

        Service service = Service.builder()
                .tenantId(tenant1Id)
                .name("Classic Haircut")
                .description("Standard cut and style")
                .price(new BigDecimal("35.00"))
                .durationMinutes(45)
                .status("ACTIVE")
                .build();

        Service savedService = serviceRepository.save(service);

        assertThat(savedService.getId()).isNotNull();
        assertThat(savedService.getTenantId()).isEqualTo(tenant1Id);
        assertThat(savedService.getName()).isEqualTo("Classic Haircut");
    }
}

package com.trymily.api.modules.tenants;

import com.trymily.api.modules.tenants.settings.BusinessHours;
import com.trymily.api.modules.tenants.settings.TenantSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TenantRepositoryTest {

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void shouldSaveAndFindTenantWithSettings() {
        // Given
        TenantSettings settings = TenantSettings.builder()
                .appointmentBufferMinutes(15)
                .businessHours(Map.of(
                        "MONDAY", BusinessHours.builder().open(true).openTime("08:00").closeTime("18:00").build()
                ))
                .build();

        Tenant tenant = Tenant.builder()
                .name("Modern Salon")
                .settings(settings)
                .build();

        // When
        Tenant savedTenant = tenantRepository.save(tenant);
        Optional<Tenant> result = tenantRepository.findById(savedTenant.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Modern Salon");
        assertThat(result.get().getSettings().getAppointmentBufferMinutes()).isEqualTo(15);
        assertThat(result.get().getSettings().getBusinessHours().get("MONDAY").getOpenTime()).isEqualTo("08:00");
    }
}

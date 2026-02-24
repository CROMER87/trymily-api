package com.trymily.api.modules.appointments;

import com.trymily.api.modules.tenants.Tenant;
import com.trymily.api.modules.tenants.TenantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void shouldSaveAndFindAppointment() {
        // Given
        Tenant tenant = tenantRepository.save(Tenant.builder().name("Sample Tenant").build());
        
        Appointment appointment = Appointment.builder()
                .tenant(tenant)
                .customerName("John Doe")
                .startTime(ZonedDateTime.now())
                .endTime(ZonedDateTime.now().plusHours(1))
                .build();

        // When
        Appointment savedAppointment = appointmentRepository.save(appointment);
        Optional<Appointment> result = appointmentRepository.findById(savedAppointment.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCustomerName()).isEqualTo("John Doe");
        assertThat(result.get().getTenant().getId()).isEqualTo(tenant.getId());
    }
}

package com.trymily.api.core.tenant;

import com.trymily.api.modules.appointments.Appointment;
import com.trymily.api.modules.appointments.AppointmentRepository;
import com.trymily.api.modules.tenants.Tenant;
import com.trymily.api.modules.tenants.TenantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RlsIsolationTest {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Tenant tenantA;
    private Tenant tenantB;

    @BeforeEach
    void setUp() {
        // Clear context to allow global access for setup (assuming test runner has bypass or we set it)
        TenantContextHolder.clear();

        tenantA = tenantRepository.save(Tenant.builder().name("Tenant A").build());
        tenantB = tenantRepository.save(Tenant.builder().name("Tenant B").build());

        TenantContextHolder.setTenantId(tenantA.getId());
        appointmentRepository.save(Appointment.builder()
                .tenant(tenantA)
                .customerName("Customer A")
                .startTime(ZonedDateTime.now())
                .endTime(ZonedDateTime.now().plusHours(1))
                .build());

        TenantContextHolder.setTenantId(tenantB.getId());
        appointmentRepository.save(Appointment.builder()
                .tenant(tenantB)
                .customerName("Customer B")
                .startTime(ZonedDateTime.now())
                .endTime(ZonedDateTime.now().plusHours(1))
                .build());
        
        TenantContextHolder.clear();
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
        appointmentRepository.deleteAll();
        tenantRepository.deleteAll();
    }

    @Test
    void shouldOnlySeeTenantADataWhenContextIsSetToTenantA() {
        // Given
        TenantContextHolder.setTenantId(tenantA.getId());

        // When
        List<Appointment> appointments = appointmentRepository.findAll();

        // Then
        assertThat(appointments).hasSize(1);
        assertThat(appointments.get(0).getCustomerName()).isEqualTo("Customer A");
        assertThat(appointments.get(0).getTenant().getId()).isEqualTo(tenantA.getId());
    }

    @Test
    void shouldOnlySeeTenantBDataWhenContextIsSetToTenantB() {
        // Given
        TenantContextHolder.setTenantId(tenantB.getId());

        // When
        List<Appointment> appointments = appointmentRepository.findAll();

        // Then
        assertThat(appointments).hasSize(1);
        assertThat(appointments.get(0).getCustomerName()).isEqualTo("Customer B");
        assertThat(appointments.get(0).getTenant().getId()).isEqualTo(tenantB.getId());
    }

    @Test
    void shouldSeeNoDataWhenNoTenantContextIsSet() {
        // Given
        TenantContextHolder.clear();

        // When
        List<Appointment> appointments = appointmentRepository.findAll();

        // Then
        assertThat(appointments).isEmpty();
    }
}

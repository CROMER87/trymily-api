package com.trymily.api.modules.dashboard;

import com.trymily.api.modules.appointments.Appointment;
import com.trymily.api.modules.appointments.AppointmentRepository;
import com.trymily.api.modules.tenants.Tenant;
import com.trymily.api.modules.tenants.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = "spring.jpa.properties.hibernate.integration.envers.enabled=false")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private UUID tenantId;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        tenantRepository.deleteAll();

        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name("Dashboard Salon")
                .status("ACTIVE")
                .build());
        tenantId = tenant.getId();

        // Today's appointments
        appointmentRepository.save(Appointment.builder()
                .tenant(tenant)
                .customerName("Client 1")
                .startTime(ZonedDateTime.now())
                .endTime(ZonedDateTime.now().plusHours(1))
                .status("CONFIRMED")
                .build());

        appointmentRepository.save(Appointment.builder()
                .tenant(tenant)
                .customerName("Client 2")
                .startTime(ZonedDateTime.now().plusHours(2))
                .endTime(ZonedDateTime.now().plusHours(3))
                .status("PENDING")
                .build());

        // Future appointment (not today)
        appointmentRepository.save(Appointment.builder()
                .tenant(tenant)
                .customerName("Client 3")
                .startTime(ZonedDateTime.now().plusDays(1))
                .endTime(ZonedDateTime.now().plusDays(1).plusHours(1))
                .status("PENDING")
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/summary")
                .header("X-Tenant-ID", tenantId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todayTotal").value(2))
                .andExpect(jsonPath("$.pendingCount").value(2));
    }
}

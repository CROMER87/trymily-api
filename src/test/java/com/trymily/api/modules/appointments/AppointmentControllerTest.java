package com.trymily.api.modules.appointments;

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
class AppointmentControllerTest {

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
                .name("Filter Salon")
                .status("ACTIVE")
                .build());
        tenantId = tenant.getId();

        // Appointment today
        appointmentRepository.save(Appointment.builder()
                .tenant(tenant)
                .customerName("Today Client")
                .startTime(ZonedDateTime.now())
                .endTime(ZonedDateTime.now().plusHours(1))
                .status("CONFIRMED")
                .build());

        // Appointment tomorrow
        appointmentRepository.save(Appointment.builder()
                .tenant(tenant)
                .customerName("Tomorrow Client")
                .startTime(ZonedDateTime.now().plusDays(1))
                .endTime(ZonedDateTime.now().plusDays(1).plusHours(1))
                .status("CONFIRMED")
                .build());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldFilterAppointmentsByDateRange() throws Exception {
        ZonedDateTime start = ZonedDateTime.now().minusHours(1);
        ZonedDateTime end = ZonedDateTime.now().plusHours(2);

        mockMvc.perform(get("/api/v1/appointments")
                .header("X-Tenant-ID", tenantId.toString())
                .param("start", start.toString())
                .param("end", end.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerName").value("Today Client"));
    }
}

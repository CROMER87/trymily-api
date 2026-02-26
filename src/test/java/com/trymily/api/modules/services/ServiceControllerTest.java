package com.trymily.api.modules.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trymily.api.core.tenant.TenantContextHolder;
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

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = "spring.jpa.properties.hibernate.integration.envers.enabled=false")
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID tenantId;

    @BeforeEach
    void setUp() {
        TenantContextHolder.clear();
        serviceRepository.deleteAll();
        tenantRepository.deleteAll();

        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name("Test Salon")
                .status("ACTIVE")
                .build());
        tenantId = tenant.getId();
        TenantContextHolder.setTenantId(tenantId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateService() throws Exception {
        Service service = Service.builder()
                .name("New Service")
                .price(new BigDecimal("50.00"))
                .durationMinutes(60)
                .status("ACTIVE")
                .build();

        mockMvc.perform(post("/api/v1/services")
                .header("X-Tenant-ID", tenantId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(service)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Service"))
                .andExpect(jsonPath("$.price").value(50.00))
                .andExpect(jsonPath("$.tenantId").value(tenantId.toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAllServices() throws Exception {
        serviceRepository.save(Service.builder()
                .tenantId(tenantId)
                .name("Existing Service")
                .price(new BigDecimal("25.00"))
                .durationMinutes(30)
                .status("ACTIVE")
                .build());

        mockMvc.perform(get("/api/v1/services")
                .header("X-Tenant-ID", tenantId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Existing Service"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateService() throws Exception {
        Service existing = serviceRepository.save(Service.builder()
                .tenantId(tenantId)
                .name("Old Name")
                .price(new BigDecimal("20.00"))
                .durationMinutes(20)
                .status("ACTIVE")
                .build());

        Service update = Service.builder()
                .name("Updated Name")
                .price(new BigDecimal("30.00"))
                .durationMinutes(30)
                .status("ACTIVE")
                .build();

        mockMvc.perform(put("/api/v1/services/" + existing.getId())
                .header("X-Tenant-ID", tenantId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.price").value(30.00));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteService() throws Exception {
        Service service = serviceRepository.save(Service.builder()
                .tenantId(tenantId)
                .name("To Delete")
                .price(new BigDecimal("10.00"))
                .durationMinutes(10)
                .status("ACTIVE")
                .build());

        mockMvc.perform(delete("/api/v1/services/" + service.getId())
                .header("X-Tenant-ID", tenantId.toString()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/services/" + service.getId())
                .header("X-Tenant-ID", tenantId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyCreateForNonAdmin() throws Exception {
        Service service = Service.builder()
                .name("Unauthorized")
                .price(new BigDecimal("1.00"))
                .durationMinutes(1)
                .build();

        mockMvc.perform(post("/api/v1/services")
                .header("X-Tenant-ID", tenantId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(service)))
                .andExpect(status().isForbidden());
    }
}

package com.trymily.api.core.config.seed;

import com.trymily.api.modules.services.Service;
import com.trymily.api.modules.services.ServiceRepository;
import com.trymily.api.modules.tenants.Tenant;
import com.trymily.api.modules.tenants.TenantRepository;
import com.trymily.api.modules.tenants.settings.BusinessHours;
import com.trymily.api.modules.tenants.settings.TenantSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DummyDataSeeder implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public void run(String... args) throws Exception {
        if (tenantRepository.count() > 0) {
            log.info("Database already contains tenants. Skipping dummy data seeder.");
            return;
        }

        log.info("Populating database with dummy Tenants and Services...");

        // Create standard business hours
        Map<String, BusinessHours> standardHours = new HashMap<>();
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
        for (String day : days) {
            standardHours.put(day, BusinessHours.builder()
                .open(true)
                .openTime("09:00")
                .closeTime("18:00")
                .build());
        }
        standardHours.put("SUNDAY", BusinessHours.builder().open(false).build());
        
        TenantSettings settings = new TenantSettings();
        settings.setBusinessHours(standardHours);

        // 1. Barbearia do Zé
        Tenant barbershop = tenantRepository.save(Tenant.builder()
            .name("Barbearia do Zé")
            .phone("11999999991")
            .address("Rua das Tesouras, 123 - Centro")
            .status("ACTIVE")
            .settings(settings)
            .build());

        serviceRepository.saveAll(List.of(
            Service.builder().tenantId(barbershop.getId()).name("Corte Masculino").durationMinutes(30).price(new BigDecimal("40.00")).status("ACTIVE").build(),
            Service.builder().tenantId(barbershop.getId()).name("Barba Completa").durationMinutes(30).price(new BigDecimal("35.00")).status("ACTIVE").build(),
            Service.builder().tenantId(barbershop.getId()).name("Corte e Barba").durationMinutes(60).price(new BigDecimal("70.00")).status("ACTIVE").build()
        ));

        // 2. Salão da Maria
        Tenant salon = tenantRepository.save(Tenant.builder()
            .name("Salão Beleza Pura")
            .phone("11999999992")
            .address("Av. das Rosas, 456 - Jardim das Flores")
            .status("ACTIVE")
            .settings(settings)
            .build());

        serviceRepository.saveAll(List.of(
            Service.builder().tenantId(salon.getId()).name("Corte Feminino").durationMinutes(60).price(new BigDecimal("80.00")).status("ACTIVE").build(),
            Service.builder().tenantId(salon.getId()).name("Manicure").durationMinutes(45).price(new BigDecimal("30.00")).status("ACTIVE").build(),
            Service.builder().tenantId(salon.getId()).name("Pedicure").durationMinutes(45).price(new BigDecimal("35.00")).status("ACTIVE").build(),
            Service.builder().tenantId(salon.getId()).name("Hidratação").durationMinutes(60).price(new BigDecimal("120.00")).status("ACTIVE").build()
        ));

        // 3. Pet Shop Cão Feliz
        Tenant petshop = tenantRepository.save(Tenant.builder()
            .name("Pet Shop Cão Feliz")
            .phone("11999999993")
            .address("Rua dos Animais, 789 - Vila Madalena")
            .status("ACTIVE")
            .settings(settings)
            .build());

        serviceRepository.saveAll(List.of(
            Service.builder().tenantId(petshop.getId()).name("Banho P").durationMinutes(60).price(new BigDecimal("50.00")).status("ACTIVE").build(),
            Service.builder().tenantId(petshop.getId()).name("Tosa Higiênica").durationMinutes(30).price(new BigDecimal("40.00")).status("ACTIVE").build(),
            Service.builder().tenantId(petshop.getId()).name("Banho e Tosa Completo").durationMinutes(120).price(new BigDecimal("120.00")).status("ACTIVE").build()
        ));

        log.info("Dummy data generated successfully!");
    }
}

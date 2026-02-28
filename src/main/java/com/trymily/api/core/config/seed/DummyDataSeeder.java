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
        if (tenantRepository.count() >= 13) {
            log.info("Database already contains >= 13 tenants. Skipping dummy data seeder.");
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
        Tenant barbershop = tenantRepository.save(Tenant.builder().name("Barbearia do Zé").phone("11999999991").address("Rua das Tesouras, 123 - Centro").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(
            Service.builder().tenantId(barbershop.getId()).name("Corte Masculino").durationMinutes(30).price(new BigDecimal("40.00")).status("ACTIVE").build(),
            Service.builder().tenantId(barbershop.getId()).name("Barba Completa").durationMinutes(30).price(new BigDecimal("35.00")).status("ACTIVE").build(),
            Service.builder().tenantId(barbershop.getId()).name("Corte e Barba").durationMinutes(60).price(new BigDecimal("70.00")).status("ACTIVE").build()
        ));

        // 2. Salão da Maria
        Tenant salon = tenantRepository.save(Tenant.builder().name("Salão Beleza Pura").phone("11999999992").address("Av. das Rosas, 456 - Jardim das Flores").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(
            Service.builder().tenantId(salon.getId()).name("Corte Feminino").durationMinutes(60).price(new BigDecimal("80.00")).status("ACTIVE").build(),
            Service.builder().tenantId(salon.getId()).name("Manicure").durationMinutes(45).price(new BigDecimal("30.00")).status("ACTIVE").build()
        ));

        // 3. Pet Shop Cão Feliz
        Tenant petshop = tenantRepository.save(Tenant.builder().name("Pet Shop Cão Feliz").phone("11999999993").address("Rua dos Animais, 789 - Vila Madalena").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(
            Service.builder().tenantId(petshop.getId()).name("Banho P").durationMinutes(60).price(new BigDecimal("50.00")).status("ACTIVE").build(),
            Service.builder().tenantId(petshop.getId()).name("Tosa Higiênica").durationMinutes(30).price(new BigDecimal("40.00")).status("ACTIVE").build()
        ));

        // 4. Barbearia Machado
        Tenant t4 = tenantRepository.save(Tenant.builder().name("Barbearia Machado").phone("11999999994").address("Av. Paulista, 1000 - Bela Vista").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(Service.builder().tenantId(t4.getId()).name("Corte Clássico").durationMinutes(45).price(new BigDecimal("50.00")).status("ACTIVE").build()));

        // 5. Salão Sublime
        Tenant t5 = tenantRepository.save(Tenant.builder().name("Salão Sublime").phone("11999999995").address("Rua Augusta, 500 - Consolação").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(Service.builder().tenantId(t5.getId()).name("Mechas").durationMinutes(120).price(new BigDecimal("250.00")).status("ACTIVE").build()));

        // 6. Studio Beauty
        Tenant t6 = tenantRepository.save(Tenant.builder().name("Studio Beauty").phone("11999999996").address("Rua Oscar Freire, 200 - Jardins").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(Service.builder().tenantId(t6.getId()).name("Maquiagem").durationMinutes(60).price(new BigDecimal("150.00")).status("ACTIVE").build()));

        // 7. Espaço Zen Spa
        Tenant t7 = tenantRepository.save(Tenant.builder().name("Espaço Zen Spa").phone("11999999997").address("Av. Brasil, 300 - Ibirapuera").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(Service.builder().tenantId(t7.getId()).name("Massagem Relaxante").durationMinutes(60).price(new BigDecimal("180.00")).status("ACTIVE").build()));

        // 8. Oficina do Cabelo
        Tenant t8 = tenantRepository.save(Tenant.builder().name("Oficina do Cabelo").phone("11999999998").address("Rua Direita, 40 - Sé").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(Service.builder().tenantId(t8.getId()).name("Platinado Masculino").durationMinutes(90).price(new BigDecimal("120.00")).status("ACTIVE").build()));

        // 9. Clínica Vet Amor
        Tenant t9 = tenantRepository.save(Tenant.builder().name("Clínica Vet Amor").phone("11999999999").address("Rua dos Trilhos, 900 - Mooca").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(Service.builder().tenantId(t9.getId()).name("Consulta Veterinária").durationMinutes(45).price(new BigDecimal("150.00")).status("ACTIVE").build()));

        // 10. Estética Avançada
        Tenant t10 = tenantRepository.save(Tenant.builder().name("Estética Avançada").phone("11999999910").address("Av. Faria Lima, 3000 - Itaim Bibi").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(Service.builder().tenantId(t10.getId()).name("Limpeza de Pele").durationMinutes(60).price(new BigDecimal("120.00")).status("ACTIVE").build()));

        // 11. Salão Kids Plim Plim
        Tenant t11 = tenantRepository.save(Tenant.builder().name("Salão Kids Plim Plim").phone("11999999911").address("Rua Turiassu, 1500 - Perdizes").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(Service.builder().tenantId(t11.getId()).name("Corte Infantil").durationMinutes(40).price(new BigDecimal("60.00")).status("ACTIVE").build()));

        // 12. Barba & Cia
        Tenant t12 = tenantRepository.save(Tenant.builder().name("Barba & Cia").phone("11999999912").address("Rua Teodoro Sampaio, 800 - Pinheiros").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(Service.builder().tenantId(t12.getId()).name("Barboterapia").durationMinutes(45).price(new BigDecimal("55.00")).status("ACTIVE").build()));

        // 13. Pet Shop Pata Fina
        Tenant t13 = tenantRepository.save(Tenant.builder().name("Pet Shop Pata Fina").phone("11999999913").address("Av. Santo Amaro, 4000 - Brooklin").status("ACTIVE").settings(settings).build());
        serviceRepository.saveAll(List.of(Service.builder().tenantId(t13.getId()).name("Banho Gato").durationMinutes(50).price(new BigDecimal("70.00")).status("ACTIVE").build()));

        log.info("Dummy data generated successfully!");
    }
}

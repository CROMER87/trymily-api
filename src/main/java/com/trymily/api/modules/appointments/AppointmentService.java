package com.trymily.api.modules.appointments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.trymily.api.modules.tenants.TenantRepository;
import com.trymily.api.modules.tenants.Tenant;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TenantRepository tenantRepository;

    public List<AppointmentDTO> findByDateRange(ZonedDateTime start, ZonedDateTime end) {
        return appointmentRepository.findByStartTimeBetween(start, end)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AppointmentDTO toDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .customerName(appointment.getCustomerName())
                .establishmentName(appointment.getEstablishmentName())
                .serviceId(appointment.getServiceId())
                .serviceName(appointment.getServiceName())
                .servicePrice(appointment.getServicePrice())
                .serviceDuration(appointment.getServiceDuration())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .build();
    }

    @Transactional
    public AppointmentDTO bookAppointment(BookAppointmentRequest request, String customerName) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Appointment appointment = Appointment.builder()
                .tenant(tenant)
                .establishmentName(tenant.getName())
                .customerName(customerName)
                .serviceId(request.getServiceId())
                .serviceName(request.getServiceName())
                .servicePrice(request.getServicePrice())
                .serviceDuration(request.getServiceDuration())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status("PENDING")
                .build();

        return toDTO(appointmentRepository.save(appointment));
    }

    public List<AppointmentDTO> findMyBookings(String customerName) {
        return appointmentRepository.findByCustomerNameOrderByStartTimeDesc(customerName)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> findMyEstablishmentBookings(java.util.UUID tenantId) {
        return appointmentRepository.findByTenantIdOrderByStartTimeAsc(tenantId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

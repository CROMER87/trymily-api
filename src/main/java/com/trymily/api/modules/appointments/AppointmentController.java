package com.trymily.api.modules.appointments;

import com.trymily.api.modules.users.User;
import com.trymily.api.modules.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<AppointmentDTO> getAppointments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime end) {
        return appointmentService.findByDateRange(start, end);
    }

    @PostMapping
    public AppointmentDTO book(@RequestBody @Valid BookAppointmentRequest request, Principal principal) {
        String customerName = request.getCustomerName();
        if (principal != null) {
            try {
                UUID userId = UUID.fromString(principal.getName());
                User user = userRepository.findById(userId).orElse(null);
                if (user != null && user.getFullName() != null && !user.getFullName().isBlank()) {
                    customerName = user.getFullName();
                } else if (user != null) {
                    customerName = user.getEmail();
                }
            } catch (IllegalArgumentException e) {
                // principal.getName() is not a UUID, use it as-is
                customerName = principal.getName();
            }
        }
        return appointmentService.bookAppointment(request, customerName);
    }

    @GetMapping("/my")
    public List<AppointmentDTO> getMyBookings(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Unauthorized");
        }
        return appointmentService.findMyBookings(principal.getName());
    }

    @GetMapping("/tenant")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<AppointmentDTO> getEstablishmentBookings() {
        java.util.UUID tenantId = com.trymily.api.core.tenant.TenantContextHolder.getTenantId();
        if (tenantId == null) {
            throw new RuntimeException("Tenant context not found");
        }
        return appointmentService.findMyEstablishmentBookings(tenantId);
    }
}

package com.trymily.api.modules.appointments;

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

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<AppointmentDTO> getAppointments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime end) {
        return appointmentService.findByDateRange(start, end);
    }

    @PostMapping
    public AppointmentDTO book(@RequestBody @Valid BookAppointmentRequest request, Principal principal) {
        String customerName = principal != null ? principal.getName() : request.getCustomerName();
        return appointmentService.bookAppointment(request, customerName);
    }

    @GetMapping("/my")
    public List<AppointmentDTO> getMyBookings(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Unauthorized");
        }
        return appointmentService.findMyBookings(principal.getName());
    }
}

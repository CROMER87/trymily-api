package com.trymily.api.modules.dashboard;

import com.trymily.api.modules.appointments.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AppointmentRepository appointmentRepository;

    public DashboardSummaryDTO getSummary() {
        ZonedDateTime startOfDay = ZonedDateTime.of(LocalDate.now(), LocalTime.MIN, ZoneId.systemDefault());
        ZonedDateTime endOfDay = ZonedDateTime.of(LocalDate.now(), LocalTime.MAX, ZoneId.systemDefault());

        long todayTotal = appointmentRepository.countByStartTimeBetween(startOfDay, endOfDay);
        long pendingCount = appointmentRepository.countByStatus("PENDING");

        return DashboardSummaryDTO.builder()
                .todayTotal(todayTotal)
                .pendingCount(pendingCount)
                .build();
    }
}

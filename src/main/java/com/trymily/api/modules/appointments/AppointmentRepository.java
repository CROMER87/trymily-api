package com.trymily.api.modules.appointments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    long countByStatus(String status);
    long countByStartTimeBetween(ZonedDateTime start, ZonedDateTime end);
    List<Appointment> findByStartTimeBetween(ZonedDateTime start, ZonedDateTime end);
}

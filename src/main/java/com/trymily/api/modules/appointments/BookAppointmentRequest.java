package com.trymily.api.modules.appointments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookAppointmentRequest {
    @NotNull
    private UUID tenantId;
    
    @NotBlank
    private String customerName;
    
    @NotNull
    private UUID serviceId;

    @NotBlank
    private String serviceName;

    @NotNull
    private Double servicePrice;

    @NotNull
    private Integer serviceDuration;

    @NotNull
    private ZonedDateTime startTime;
    
    @NotNull
    private ZonedDateTime endTime;
}

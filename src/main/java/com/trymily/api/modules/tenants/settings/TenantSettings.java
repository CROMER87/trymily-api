package com.trymily.api.modules.tenants.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSettings implements Serializable {
    
    // Key: Day of week (MONDAY, TUESDAY, etc.)
    private Map<String, BusinessHours> businessHours;
    
    private int appointmentBufferMinutes;
    
    private String primaryColor;
    
    @Builder.Default
    private boolean autoConfirmAppointments = false;
}

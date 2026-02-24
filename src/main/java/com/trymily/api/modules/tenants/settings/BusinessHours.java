package com.trymily.api.modules.tenants.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHours {
    private boolean open;
    private String openTime;  // Store as String for JSON simplicity (e.g. "09:00")
    private String closeTime; // Store as String for JSON simplicity (e.g. "18:00")
}

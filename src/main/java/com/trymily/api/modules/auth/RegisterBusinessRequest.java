package com.trymily.api.modules.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterBusinessRequest {

    // User details
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String fullName;

    // Business (Tenant) details
    @NotBlank
    private String businessName;

    @NotBlank
    private String businessAddress;

    @NotBlank
    private String businessNeighborhood;

    @NotBlank
    private String businessType;

    private String businessPhone;
}

package com.trymily.api.modules.users;

import com.trymily.api.modules.tenants.Tenant;
import com.trymily.api.modules.tenants.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(String email, String password, String fullName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .provider("LOCAL")
                .role("ROLE_CUSTOMER")
                .build();

        return userRepository.save(newUser);
    }

    @Transactional
    public User registerBusiness(String email, String password, String fullName, String businessName, String businessType, String businessAddress, String businessNeighborhood, String businessPhone) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este e-mail já está cadastrado.");
        }

        String fullAddress = businessAddress + " - Bairro: " + businessNeighborhood;

        // 1. Create the new Business (Tenant)
        Tenant newTenant = Tenant.builder()
                .name(businessName)
                .type(businessType)
                .address(fullAddress)
                .phone(businessPhone)
                .status("ACTIVE")
                .build();
        tenantRepository.save(newTenant);

        // 2. Create the Admin User for this Business
        User adminUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .provider("LOCAL")
                .role("ROLE_ADMIN") // Set explicit Admin role
                .tenant(newTenant)  // Link to the newly created tenant
                .build();

        return userRepository.save(adminUser);
    }

    @Transactional
    public User processSocialLogin(String email, String fullName, String pictureUrl, String provider, String providerId) {
        log.info("Processing social login for email: {} from provider: {}", email, provider);
        
        Optional<User> existingUser = userRepository.findByProviderId(providerId);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            log.debug("Updating existing user: {}", user.getId());
            user.setFullName(fullName);
            user.setPictureUrl(pictureUrl);
            user.setEmail(email); // Keep email in sync if changed in Google
            return userRepository.save(user);
        }

        log.info("Provisioning new user for email: {}", email);
        User newUser = User.builder()
                .email(email)
                .fullName(fullName)
                .pictureUrl(pictureUrl)
                .provider(provider)
                .providerId(providerId)
                .role("ROLE_CUSTOMER") // Default role for social login
                .build();
        
        return userRepository.save(newUser);
    }
}

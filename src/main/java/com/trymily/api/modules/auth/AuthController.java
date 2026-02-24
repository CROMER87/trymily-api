package com.trymily.api.modules.auth;

import com.trymily.api.core.config.security.JwtService;
import com.trymily.api.modules.users.User;
import com.trymily.api.modules.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/google")
    public LoginResponse googleLogin(@AuthenticationPrincipal Jwt googleJwt) {
        // The user was already provisioned/updated by CustomJwtAuthenticationConverter
        String providerId = googleJwt.getSubject();
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found after social login"));

        String internalToken = jwtService.generateToken(user);

        return LoginResponse.builder()
                .accessToken(internalToken)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .tenantId(user.getTenant() != null ? user.getTenant().getId().toString() : null)
                .build();
    }
}

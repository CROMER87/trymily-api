package com.trymily.api.modules.auth;

import com.trymily.api.core.config.security.JwtService;
import com.trymily.api.modules.users.User;
import com.trymily.api.modules.users.UserRepository;
import com.trymily.api.modules.users.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public LoginResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getEmail(), request.getPassword(), request.getFullName());
        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .accessToken(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    @PostMapping("/register-business")
    public LoginResponse registerBusiness(@Valid @RequestBody RegisterBusinessRequest request) {
        User user = userService.registerBusiness(
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getBusinessName(),
                request.getBusinessType(),
                request.getBusinessAddress(),
                request.getBusinessNeighborhood(),
                request.getBusinessPhone()
        );
        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .accessToken(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .tenantId(user.getTenant() != null ? user.getTenant().getId().toString() : null)
                .build();
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .accessToken(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .tenantId(user.getTenant() != null ? user.getTenant().getId().toString() : null)
                .build();
    }

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

package com.trymily.api.modules.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

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

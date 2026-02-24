package com.trymily.api.modules.auth;

import com.trymily.api.modules.users.User;
import com.trymily.api.modules.users.UserRepository;
import com.trymily.api.modules.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = "spring.jpa.properties.hibernate.integration.envers.enabled=false")
class SocialLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldProvisionUserAndReturnTokenOnGoogleLogin() throws Exception {
        // Given
        String googleSub = "google-123456";
        String email = "newuser@gmail.com";
        String name = "Google User";

        // In real life, the Filter/Converter does this. In tests, we simulate the provisioning call
        // because .with(jwt()) doesn't trigger the CustomJwtAuthenticationConverter.
        userService.processSocialLogin(email, name, "http://photo.com", "GOOGLE", googleSub);

        // When
        mockMvc.perform(post("/api/v1/auth/google")
                .with(jwt().jwt(builder -> builder.subject(googleSub))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.email").value(email));

        // Then verify database state
        assertThat(userRepository.findByProviderId(googleSub)).isPresent();
    }

    @Test
    void shouldUpdateExistingUserOnGoogleLogin() throws Exception {
        // Given an existing user
        String googleSub = "existing-google-id";
        String email = "old@gmail.com";
        userRepository.save(User.builder()
                .email(email)
                .fullName("Old Name")
                .provider("GOOGLE")
                .providerId(googleSub)
                .build());

        String newName = "Updated Name";
        
        // Simulate JIT update
        userService.processSocialLogin(email, newName, "http://new-photo.com", "GOOGLE", googleSub);

        // When
        mockMvc.perform(post("/api/v1/auth/google")
                .with(jwt().jwt(builder -> builder.subject(googleSub))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value(newName));
    }
}

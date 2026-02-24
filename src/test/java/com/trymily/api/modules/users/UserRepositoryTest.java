package com.trymily.api.modules.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByProviderId() {
        // Given
        User user = User.builder()
                .email("test@google.com")
                .fullName("Test User")
                .provider("GOOGLE")
                .providerId("google_123")
                .build();

        // When
        userRepository.save(user);
        Optional<User> result = userRepository.findByProviderId("google_123");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@google.com");
    }
}

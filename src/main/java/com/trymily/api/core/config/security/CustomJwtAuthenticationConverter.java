package com.trymily.api.core.config.security;

import com.trymily.api.modules.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserService userService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract info from Google Token
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String picture = jwt.getClaimAsString("picture");
        String providerId = jwt.getSubject(); // 'sub' in Google tokens is the unique ID

        // JIT Provisioning
        userService.processSocialLogin(email, name, picture, "GOOGLE", providerId);

        // Return standard authentication token
        return new JwtAuthenticationToken(jwt, null, providerId);
    }
}

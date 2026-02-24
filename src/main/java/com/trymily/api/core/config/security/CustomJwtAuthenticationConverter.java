package com.trymily.api.core.config.security;

import com.trymily.api.modules.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserService userService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract info from Internal Token (if issuer is trymily-api) or Google
        String issuer = jwt.getClaimAsString("iss");
        
        if ("trymily-api".equals(issuer)) {
            String role = jwt.getClaimAsString("role");
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
            return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
        }

        // Standard logic for Google tokens (initial login)
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String picture = jwt.getClaimAsString("picture");
        String providerId = jwt.getSubject();

        userService.processSocialLogin(email, name, picture, "GOOGLE", providerId);

        return new JwtAuthenticationToken(jwt, null, providerId);
    }
}

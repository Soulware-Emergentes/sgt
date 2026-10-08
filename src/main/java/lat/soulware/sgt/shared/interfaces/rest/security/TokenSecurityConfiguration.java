package lat.soulware.sgt.shared.interfaces.rest.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Admits a request only with a bearer token staff issued: signed with one of the keys staff
 * publishes, naming staff as its issuer, and addressed to sgt. The issuer and the audience come from
 * {@code spring.security.oauth2.resourceserver.jwt.*}, which Boot turns into the decoder checking them.
 *
 * <p>Only a person's token is admitted: staff marks it {@code token_use: user}, so a token a service
 * got for itself is refused here even when addressed to sgt.
 *
 * <p>A request without such a token is answered by {@link UnauthenticatedEntryPoint}, whether the
 * token is missing, which surfaces when the anonymous request is refused, or present and rejected by
 * the bearer filter. Stateless, so there is no session to forge a request against and no CSRF token
 * either.
 */
@Configuration
public class TokenSecurityConfiguration {

    private static final String TOKEN_USE_CLAIM = "token_use";
    private static final String PERSON_TOKEN_USE = "user";

    @Bean
    SecurityFilterChain tokenSecurity(HttpSecurity http, UnauthenticatedEntryPoint entryPoint) throws Exception {
        return http.csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> requests
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults())
                .authenticationEntryPoint(entryPoint)
            )
            .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(entryPoint))
            .build();
    }

    /** Refuses an access token not issued to a person, which Boot adds to the decoder it builds. */
    @Bean
    OAuth2TokenValidator<Jwt> personTokensOnly() {
        return new JwtClaimValidator<String>(TOKEN_USE_CLAIM, PERSON_TOKEN_USE::equals);
    }

    /** Lets the frontend call sgt from the browser. */
    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${sgt.web.origin}") String webOrigin) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(webOrigin));
        cors.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cors);
        return source;
    }
}

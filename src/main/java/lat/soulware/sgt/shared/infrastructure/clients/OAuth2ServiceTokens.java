package lat.soulware.sgt.shared.infrastructure.clients;

import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;

/**
 * Gets sgt's tokens from staff with the client credentials grant, through the registrations under
 * {@code spring.security.oauth2.client.registration}. Tokens are cached until they expire and fetched
 * again then.
 *
 * <p>sgt acts as itself here, never as the person whose request led to the call, so every token is
 * held under one service principal, whichever thread sends the request: a controller's or an event
 * listener's.
 */
@Component
public class OAuth2ServiceTokens implements ServiceTokens {

    private static final Authentication SGT = new AnonymousAuthenticationToken(
        "sgt",
        "sgt",
        AuthorityUtils.createAuthorityList("ROLE_SERVICE")
    );

    private final OAuth2AuthorizedClientManager clientManager;

    public OAuth2ServiceTokens(
        ClientRegistrationRepository registrations,
        OAuth2AuthorizedClientService authorizedClients
    ) {
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
            new AuthorizedClientServiceOAuth2AuthorizedClientManager(registrations, authorizedClients);
        manager.setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder()
            .clientCredentials()
            .build()
        );
        this.clientManager = manager;
    }

    @Override
    public ClientHttpRequestInterceptor bearerFor(String registrationId) {
        OAuth2ClientHttpRequestInterceptor interceptor = new OAuth2ClientHttpRequestInterceptor(this.clientManager);
        interceptor.setClientRegistrationIdResolver(request -> registrationId);
        interceptor.setPrincipalResolver(request -> SGT);
        return interceptor;
    }
}

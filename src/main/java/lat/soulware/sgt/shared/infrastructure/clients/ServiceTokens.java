package lat.soulware.sgt.shared.infrastructure.clients;

import org.springframework.http.client.ClientHttpRequestInterceptor;

/**
 * The tokens sgt gets for itself to call another service of the program. A client adapter adds the
 * interceptor for the API it calls, and every request it sends carries a token staff issued to sgt
 * for that API alone.
 */
public interface ServiceTokens {

    /**
     * @param registrationId the registration naming the API, its scopes, and sgt's credentials
     * @return an interceptor sending each request with a bearer token for that API
     */
    ClientHttpRequestInterceptor bearerFor(String registrationId);
}

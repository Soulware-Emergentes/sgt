package lat.soulware.sgt.support;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Stands in for staff as an issuer: serves its discovery document and its signing keys over HTTP on
 * a free local port, and signs tokens the way staff does. sgt is pointed at it through the same
 * {@code issuer-uri} it uses in production, so it checks tokens with the decoder Boot builds from its
 * real configuration, discovery, issuer, audience and token use included, and a test can send a
 * token staff would never issue and watch it be refused.
 */
public final class TestTokens {

    public static final String AUDIENCE = "sgt-api-dev";

    private static final RSAKey KEY = generateKey();
    private static final NimbusJwtEncoder ENCODER = new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(KEY)));
    private static final HttpServer ISSUER_SERVER = startIssuer();

    /** Where the stand-in issuer answers, which is also the {@code iss} its tokens carry. */
    public static final String ISSUER = "http://localhost:" + ISSUER_SERVER.getAddress().getPort();

    private TestTokens() {
    }

    /** @return the issuer sgt is pointed at, started on first use */
    public static String issuer() {
        return ISSUER;
    }

    /**
     * @param person the person's objectGUID
     * @param roles  the roles they act as
     * @return a person's access token for sgt, as staff issues it
     */
    public static String person(UUID person, String... roles) {
        return issue(ISSUER, AUDIENCE, "user", person.toString(), List.of(roles));
    }

    /**
     * @return a token any field of which may differ from what staff issues to a person for sgt
     */
    public static String issue(String issuer, String audience, String tokenUse, String subject, List<String> roles) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .audience(List.of(audience))
            .subject(subject)
            .issuedAt(now)
            .notBefore(now)
            .expiresAt(now.plusSeconds(300))
            .claim("token_use", tokenUse)
            .claim("roles", roles)
            .build();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();

        return ENCODER.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private static RSAKey generateKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            return new RSAKey.Builder((RSAPublicKey) pair.getPublic())
                .privateKey((RSAPrivateKey) pair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No RSA key pair generator", e);
        }
    }

    /** Serves the two documents a resource server reads from its issuer: discovery and the keys. */
    private static HttpServer startIssuer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            String issuer = "http://localhost:" + server.getAddress().getPort();
            String discovery = """
                {"issuer": "%s", "jwks_uri": "%s/oauth2/jwks", "subject_types_supported": ["public"]}"""
                .formatted(issuer, issuer);
            String keys = new JWKSet(KEY.toPublicJWK()).toString();
            server.createContext("/.well-known/openid-configuration", exchange -> respond(exchange, discovery));
            server.createContext("/oauth2/jwks", exchange -> respond(exchange, keys));
            server.start();
            return server;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not start the stand-in issuer", e);
        }
    }

    private static void respond(HttpExchange exchange, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }
}

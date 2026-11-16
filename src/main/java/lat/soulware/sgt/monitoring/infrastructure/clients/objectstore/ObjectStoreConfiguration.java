package lat.soulware.sgt.monitoring.infrastructure.clients.objectstore;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * The S3-compatible object store holding field sheets' scanned PDFs. Path-style addressing keeps it
 * working against stores other than AWS's own.
 */
@Configuration
public class ObjectStoreConfiguration {

    private final URI endpoint;
    private final Region region;
    private final StaticCredentialsProvider credentials;
    private final S3Configuration pathStyle = S3Configuration.builder()
        .pathStyleAccessEnabled(true)
        .build();

    public ObjectStoreConfiguration(
        @Value("${monitoring.scans.endpoint}") String endpoint,
        @Value("${monitoring.scans.region}") String region,
        @Value("${monitoring.scans.access-key}") String accessKey,
        @Value("${monitoring.scans.secret-key}") String secretKey
    ) {
        this.endpoint = URI.create(endpoint);
        this.region = Region.of(region);
        this.credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    @Bean
    public S3Client scanStoreClient() {
        return S3Client.builder()
            .endpointOverride(this.endpoint)
            .region(this.region)
            .credentialsProvider(this.credentials)
            .serviceConfiguration(this.pathStyle)
            .build();
    }

    @Bean
    public S3Presigner scanStorePresigner() {
        return S3Presigner.builder()
            .endpointOverride(this.endpoint)
            .region(this.region)
            .credentialsProvider(this.credentials)
            .serviceConfiguration(this.pathStyle)
            .build();
    }
}

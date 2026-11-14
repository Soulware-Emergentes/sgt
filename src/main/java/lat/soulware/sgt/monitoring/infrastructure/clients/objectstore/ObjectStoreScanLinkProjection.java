package lat.soulware.sgt.monitoring.infrastructure.clients.objectstore;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanUploadSlot;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections.ScanLinkProjection;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.ScanLinkResult;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

/**
 * Signs links into the object store, so field agents upload scanned PDFs and reviewers download
 * them without the bytes passing through sgt. Signing happens locally and reaches no store.
 */
@Component
public class ObjectStoreScanLinkProjection implements ScanLinkProjection {

    private static final Duration LINK_LIFETIME = Duration.ofMinutes(15);
    private static final String KEY_FORMAT = "field-sheets/%s.pdf";
    private static final String PDF = "application/pdf";

    private final S3Presigner presigner;
    private final String bucket;

    public ObjectStoreScanLinkProjection(S3Presigner presigner, @Value("${monitoring.scans.bucket}") String bucket) {
        this.presigner = presigner;
        this.bucket = bucket;
    }

    @Override
    public ScanLinkResult uploadLink(ScanUploadSlot criteria) {
        String key = KEY_FORMAT.formatted(UUID.randomUUID());
        PresignedPutObjectRequest upload = this.presigner.presignPutObject(request -> request
            .signatureDuration(LINK_LIFETIME)
            .putObjectRequest(object -> object
                .bucket(this.bucket)
                .key(key)
                .contentType(PDF)
            )
        );

        return new ScanLinkResult(key, upload.url().toString(), upload.expiration());
    }

    @Override
    public ScanLinkResult downloadLink(String key) {
        PresignedGetObjectRequest download = this.presigner.presignGetObject(request -> request
            .signatureDuration(LINK_LIFETIME)
            .getObjectRequest(object -> object
                .bucket(this.bucket)
                .key(key)
            )
        );

        return new ScanLinkResult(key, download.url().toString(), download.expiration());
    }
}

package lat.soulware.sgt.monitoring.infrastructure.clients.objectstore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lat.soulware.sgt.monitoring.domain.model.exceptions.ScanNotUploadedException;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetScan;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.monitoring.domain.services.ScanStore;
import lat.soulware.sgt.shared.infrastructure.exceptions.InfrastructureException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

/** Checks scanned PDFs in the object store by reading each object's metadata. */
@Component
public class ObjectStoreScanStore implements ScanStore {

    private final S3Client client;
    private final String bucket;

    public ObjectStoreScanStore(S3Client client, @Value("${monitoring.scans.bucket}") String bucket) {
        this.client = client;
        this.bucket = bucket;
    }

    @Override
    public FieldSheetScan inspect(ScanKey key) {
        try {
            HeadObjectResponse object = this.client.headObject(request -> request
                .bucket(this.bucket)
                .key(key.value())
            );

            return new FieldSheetScan(key, object.contentLength());
        } catch (NoSuchKeyException e) {
            throw new ScanNotUploadedException(key);
        } catch (SdkException e) {
            throw new InfrastructureException("Inspecting scan %s failed".formatted(key.value()), e);
        }
    }
}

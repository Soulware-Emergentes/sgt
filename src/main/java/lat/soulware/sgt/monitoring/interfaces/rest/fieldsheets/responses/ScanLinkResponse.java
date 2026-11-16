package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses;

import java.time.Instant;

/**
 * A signed link to a scanned PDF in the object store.
 *
 * @param key       where the PDF sits, or is to be uploaded
 * @param url       the signed link
 * @param expiresAt when the link stops working
 */
public record ScanLinkResponse(String key, String url, Instant expiresAt) {
}

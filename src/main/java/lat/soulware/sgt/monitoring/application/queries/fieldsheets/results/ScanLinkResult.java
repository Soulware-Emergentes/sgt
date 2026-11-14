package lat.soulware.sgt.monitoring.application.queries.fieldsheets.results;

import java.time.Instant;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * A signed link to a scanned PDF in the object store, valid for a short while.
 *
 * @param key       where the PDF sits, or is to be uploaded
 * @param url       the signed link
 * @param expiresAt when the link stops working
 */
public record ScanLinkResult(String key, String url, Instant expiresAt) implements QueryResult {
}

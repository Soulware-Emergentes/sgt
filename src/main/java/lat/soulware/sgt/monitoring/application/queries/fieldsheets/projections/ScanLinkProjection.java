package lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections;

import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanUploadSlot;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.ScanLinkResult;
import lat.soulware.sgt.shared.application.queries.projections.Projection;

/** Signed links into the object store holding field sheets' scanned PDFs. */
public interface ScanLinkProjection extends Projection {

    /**
     * @param criteria the upload to prepare
     * @return a fresh key and a signed link to upload a PDF there
     */
    ScanLinkResult uploadLink(ScanUploadSlot criteria);

    /**
     * @param key where the PDF sits
     * @return a signed link to download it
     */
    ScanLinkResult downloadLink(String key);
}

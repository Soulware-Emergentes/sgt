package lat.soulware.sgt.monitoring.domain.services;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetScan;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.shared.domain.services.DomainService;

/**
 * The object store holding field sheets' scanned PDFs. Field agents upload to it directly, so sgt
 * only checks what landed there.
 */
public interface ScanStore extends DomainService {

    /**
     * @param key where the PDF was uploaded
     * @return the PDF as the store holds it; fails with
     *         {@link lat.soulware.sgt.monitoring.domain.model.exceptions.ScanNotUploadedException}
     *         when nothing was uploaded there
     */
    FieldSheetScan inspect(ScanKey key);
}

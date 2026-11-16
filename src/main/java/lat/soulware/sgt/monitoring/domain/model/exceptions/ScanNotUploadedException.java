package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet is submitted for a scan the object store does not hold. */
public final class ScanNotUploadedException extends BusinessRuleViolationException {

    public ScanNotUploadedException(ScanKey key) {
        super(key.value());
    }
}

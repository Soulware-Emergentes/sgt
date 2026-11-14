package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey} is blank. */
public final class BlankScanKeyException extends BusinessRuleViolationException {

    public BlankScanKeyException() {
        super();
    }
}

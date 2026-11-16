package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a scanned PDF holds no bytes. */
public final class EmptyScanException extends BusinessRuleViolationException {

    public EmptyScanException() {
        super();
    }
}

package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet is submitted without its scanned PDF. */
public final class MissingScanException extends BusinessRuleViolationException {

    public MissingScanException() {
        super();
    }
}

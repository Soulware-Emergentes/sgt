package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet is built without its code. */
public final class MissingFieldSheetCodeException extends BusinessRuleViolationException {

    public MissingFieldSheetCodeException() {
        super();
    }
}

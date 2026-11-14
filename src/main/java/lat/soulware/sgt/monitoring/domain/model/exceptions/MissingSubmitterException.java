package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet names no field agent as its submitter. */
public final class MissingSubmitterException extends BusinessRuleViolationException {

    public MissingSubmitterException() {
        super();
    }
}

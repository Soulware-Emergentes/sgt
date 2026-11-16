package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet is submitted without naming the task it is submitted for. */
public final class MissingTaskCodeException extends BusinessRuleViolationException {

    public MissingTaskCodeException() {
        super();
    }
}

package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet carries no submission date. */
public final class MissingSubmissionDateException extends BusinessRuleViolationException {

    public MissingSubmissionDateException() {
        super();
    }
}

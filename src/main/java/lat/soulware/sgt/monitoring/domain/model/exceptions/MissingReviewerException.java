package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet is reviewed without naming the reviewer. */
public final class MissingReviewerException extends BusinessRuleViolationException {

    public MissingReviewerException() {
        super();
    }
}

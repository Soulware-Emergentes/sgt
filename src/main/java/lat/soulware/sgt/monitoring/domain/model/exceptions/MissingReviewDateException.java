package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet is reviewed without a review date. */
public final class MissingReviewDateException extends BusinessRuleViolationException {

    public MissingReviewDateException() {
        super();
    }
}

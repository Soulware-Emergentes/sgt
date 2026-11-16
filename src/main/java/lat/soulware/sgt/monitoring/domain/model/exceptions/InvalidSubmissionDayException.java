package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a submission day to filter by is not a date written like {@code 2026-10-01}. */
public final class InvalidSubmissionDayException extends BusinessRuleViolationException {

    /**
     * @param day the day as given
     */
    public InvalidSubmissionDayException(String day) {
        super(day);
    }
}

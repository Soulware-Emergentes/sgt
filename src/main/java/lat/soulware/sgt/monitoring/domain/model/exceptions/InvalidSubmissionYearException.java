package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.monitoring.domain.model.valueobjects.SubmissionYear} is not a four-digit year. */
public final class InvalidSubmissionYearException extends BusinessRuleViolationException {

    /**
     * @param rejected the year as given
     */
    public InvalidSubmissionYearException(String rejected) {
        super(rejected);
    }
}

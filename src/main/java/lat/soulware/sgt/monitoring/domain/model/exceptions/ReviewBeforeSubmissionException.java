package lat.soulware.sgt.monitoring.domain.model.exceptions;

import java.time.LocalDateTime;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet's review date precedes its submission date. */
public final class ReviewBeforeSubmissionException extends BusinessRuleViolationException {

    public ReviewBeforeSubmissionException(LocalDateTime reviewDate, LocalDateTime submissionDate) {
        super(reviewDate, submissionDate);
    }
}

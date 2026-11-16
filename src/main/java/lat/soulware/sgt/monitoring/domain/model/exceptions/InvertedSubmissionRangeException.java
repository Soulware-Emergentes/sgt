package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when field sheets are filtered by a submission range that ends before it starts. */
public final class InvertedSubmissionRangeException extends BusinessRuleViolationException {

    /**
     * @param from the first day of the range
     * @param to   the last day of the range
     */
    public InvertedSubmissionRangeException(String from, String to) {
        super(from, to);
    }
}

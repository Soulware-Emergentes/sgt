package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet is submitted without the date its evaluation took place. */
public final class MissingEvaluationDateException extends BusinessRuleViolationException {

    public MissingEvaluationDateException() {
        super();
    }
}

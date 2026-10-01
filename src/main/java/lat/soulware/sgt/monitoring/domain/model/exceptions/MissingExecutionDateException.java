package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when an {@link lat.soulware.sgt.monitoring.domain.model.valueobjects.ExecutionDate} is null. */
public final class MissingExecutionDateException extends BusinessRuleViolationException {

    public MissingExecutionDateException() {
        super();
    }
}

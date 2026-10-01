package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a poi task is read by a code no task carries. */
public final class UnknownTaskException extends BusinessRuleViolationException {

    public UnknownTaskException(String taskCode) {
        super(taskCode);
    }
}

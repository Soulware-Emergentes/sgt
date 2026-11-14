package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode} does not have a poi task code's shape. */
public final class InvalidTaskCodeException extends BusinessRuleViolationException {

    /**
     * @param rejected the code as given
     */
    public InvalidTaskCodeException(String rejected) {
        super(rejected);
    }
}

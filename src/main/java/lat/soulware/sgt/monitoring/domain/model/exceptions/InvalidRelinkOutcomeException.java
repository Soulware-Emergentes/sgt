package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a relink outcome names a swapped field sheet without a swap, or a swap without one. */
public final class InvalidRelinkOutcomeException extends BusinessRuleViolationException {

    public InvalidRelinkOutcomeException() {
        super();
    }
}

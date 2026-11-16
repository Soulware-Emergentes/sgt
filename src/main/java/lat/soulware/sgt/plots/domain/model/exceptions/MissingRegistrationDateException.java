package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.plots.domain.model.aggregates.Plot} carries no registration date. */
public final class MissingRegistrationDateException extends BusinessRuleViolationException {

    public MissingRegistrationDateException() {
        super();
    }
}

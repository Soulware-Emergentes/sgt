package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.plots.domain.model.aggregates.Plot} names no beneficiary it is registered to. */
public final class MissingPlotOwnerException extends BusinessRuleViolationException {

    public MissingPlotOwnerException() {
        super();
    }
}

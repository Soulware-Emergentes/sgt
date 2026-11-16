package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.plots.domain.model.aggregates.Plot} carries no boundary. */
public final class MissingPlotBoundaryException extends BusinessRuleViolationException {

    public MissingPlotBoundaryException() {
        super();
    }
}

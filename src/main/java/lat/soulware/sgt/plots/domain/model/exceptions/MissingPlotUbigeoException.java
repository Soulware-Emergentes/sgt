package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.plots.domain.model.aggregates.Plot} names no district it lies in. */
public final class MissingPlotUbigeoException extends BusinessRuleViolationException {

    public MissingPlotUbigeoException() {
        super();
    }
}

package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary} declares no UTM zone. */
public final class MissingUtmZoneException extends BusinessRuleViolationException {

    public MissingUtmZoneException() {
        super();
    }
}

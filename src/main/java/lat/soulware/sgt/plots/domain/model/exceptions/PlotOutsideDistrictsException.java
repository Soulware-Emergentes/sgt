package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a plot's boundary lies in no district of Peru, so it has no ubigeo to be filed under. */
public final class PlotOutsideDistrictsException extends BusinessRuleViolationException {

    public PlotOutsideDistrictsException() {
        super();
    }
}

package lat.soulware.sgt.plots.domain.model.exceptions;

import java.util.Set;

import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a plot's boundary overlaps registered plots by more than the tolerance allows. */
public final class OverlappingPlotException extends BusinessRuleViolationException {

    /**
     * @param overlapped the registered plots the boundary overlaps, never empty
     */
    public OverlappingPlotException(Set<PlotId> overlapped) {
        super(overlapped);
    }
}

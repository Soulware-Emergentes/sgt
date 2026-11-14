package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary} has fewer than three distinct vertices. */
public final class TooFewBoundaryVerticesException extends BusinessRuleViolationException {

    /**
     * @param vertexCount how many vertices the boundary was given, once its ring was opened
     */
    public TooFewBoundaryVerticesException(int vertexCount) {
        super(vertexCount);
    }
}

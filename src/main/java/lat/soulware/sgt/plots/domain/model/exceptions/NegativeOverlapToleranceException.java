package lat.soulware.sgt.plots.domain.model.exceptions;

import java.math.BigDecimal;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when an {@link lat.soulware.sgt.plots.domain.model.valueobjects.OverlapTolerance} is missing or negative. */
public final class NegativeOverlapToleranceException extends BusinessRuleViolationException {

    /**
     * @param squareMeters the rejected area
     */
    public NegativeOverlapToleranceException(BigDecimal squareMeters) {
        super(squareMeters);
    }
}

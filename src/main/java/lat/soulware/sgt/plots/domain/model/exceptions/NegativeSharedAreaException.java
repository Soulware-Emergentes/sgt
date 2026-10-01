package lat.soulware.sgt.plots.domain.model.exceptions;

import java.math.BigDecimal;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.plots.domain.model.valueobjects.SharedArea} is missing or negative. */
public final class NegativeSharedAreaException extends BusinessRuleViolationException {

    /**
     * @param squareMeters the rejected area
     */
    public NegativeSharedAreaException(BigDecimal squareMeters) {
        super(squareMeters);
    }
}

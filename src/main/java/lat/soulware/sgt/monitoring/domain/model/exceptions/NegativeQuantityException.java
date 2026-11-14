package lat.soulware.sgt.monitoring.domain.model.exceptions;

import java.math.BigDecimal;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.monitoring.domain.model.valueobjects.Quantity} is negative. */
public final class NegativeQuantityException extends BusinessRuleViolationException {

    /**
     * @param value the rejected, negative amount
     */
    public NegativeQuantityException(BigDecimal value) {
        super(value);
    }
}

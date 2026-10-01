package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.monitoring.domain.model.valueobjects.Quantity} carries no unit. */
public final class MissingMeasurementUnitException extends BusinessRuleViolationException {

    public MissingMeasurementUnitException() {
        super();
    }
}

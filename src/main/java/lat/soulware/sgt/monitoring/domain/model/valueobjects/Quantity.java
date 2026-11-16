package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import java.math.BigDecimal;

import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingMeasurementUnitException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.NegativeQuantityException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * An amount of an agricultural input, and the unit it is expressed in.
 *
 * @param value the amount, never null and never negative
 * @param unit  the unit {@code value} is expressed in, never null
 */
public record Quantity(BigDecimal value, MeasurementUnit unit) implements ValueObject {

    public Quantity {
        if (value == null || value.signum() < 0) {
            throw new NegativeQuantityException(value);
        }
        if (unit == null) {
            throw new MissingMeasurementUnitException();
        }
    }
}

package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import java.time.LocalDateTime;

import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingSecurementDateException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * When a field sheet's securement was recorded.
 *
 * @param value the moment, never null
 */
public record SecurementDate(LocalDateTime value) implements ValueObject {

    public SecurementDate {
        if (value == null) {
            throw new MissingSecurementDateException();
        }
    }
}

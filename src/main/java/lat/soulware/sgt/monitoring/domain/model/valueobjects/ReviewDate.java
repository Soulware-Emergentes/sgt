package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import java.time.LocalDateTime;

import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingReviewDateException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * When a general management assistant reviewed a field sheet.
 *
 * @param value the moment, never null
 */
public record ReviewDate(LocalDateTime value) implements ValueObject {

    public ReviewDate {
        if (value == null) {
            throw new MissingReviewDateException();
        }
    }
}

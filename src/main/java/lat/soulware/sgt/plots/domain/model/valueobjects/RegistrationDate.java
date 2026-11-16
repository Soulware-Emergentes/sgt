package lat.soulware.sgt.plots.domain.model.valueobjects;

import java.time.LocalDateTime;

import lat.soulware.sgt.plots.domain.model.exceptions.MissingRegistrationDateException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * When a plot was registered.
 *
 * @param value the moment, never null
 */
public record RegistrationDate(LocalDateTime value) implements ValueObject {

    public RegistrationDate {
        if (value == null) {
            throw new MissingRegistrationDateException();
        }
    }
}

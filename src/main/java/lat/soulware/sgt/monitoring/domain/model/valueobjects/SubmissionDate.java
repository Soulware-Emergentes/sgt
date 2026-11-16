package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import java.time.LocalDateTime;

import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingSubmissionDateException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * When a field agent submitted a field sheet.
 *
 * @param value the moment, never null
 */
public record SubmissionDate(LocalDateTime value) implements ValueObject {

    public SubmissionDate {
        if (value == null) {
            throw new MissingSubmissionDateException();
        }
    }
}

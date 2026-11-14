package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import java.time.LocalDateTime;

import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingEvaluationDateException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * When a field sheet's evaluation took place.
 *
 * @param value the moment, never null
 */
public record EvaluationDate(LocalDateTime value) implements ValueObject {

    public EvaluationDate {
        if (value == null) {
            throw new MissingEvaluationDateException();
        }
    }
}

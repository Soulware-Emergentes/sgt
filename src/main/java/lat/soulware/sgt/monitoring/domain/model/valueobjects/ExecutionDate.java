package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import java.time.LocalDate;

import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingExecutionDateException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The day the work a delivery reports to poi was carried out: for a field sheet, the day of its
 * evaluation.
 *
 * @param value the day, never null
 */
public record ExecutionDate(LocalDate value) implements ValueObject {

    public ExecutionDate {
        if (value == null) {
            throw new MissingExecutionDateException();
        }
    }
}

package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import java.util.regex.Pattern;

import lat.soulware.sgt.monitoring.domain.model.exceptions.InvalidTaskCodeException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The poi task a field sheet is submitted for, by the code poi gives it: plan years, objective,
 * activity, and task number, written {@code 2025-2030-01-03-0032}. Only the shape is checked here;
 * whether the task exists and still takes a delivery is poi's to say.
 *
 * @param value the code as written, never null
 */
public record TaskCode(String value) implements ValueObject {

    private static final Pattern FORMAT = Pattern.compile("^\\d{4}-\\d{4}-\\d{2}-\\d{2}-\\d{4}$");

    public TaskCode {
        if (value == null || !FORMAT.matcher(value).matches()) {
            throw new InvalidTaskCodeException(value);
        }
    }
}

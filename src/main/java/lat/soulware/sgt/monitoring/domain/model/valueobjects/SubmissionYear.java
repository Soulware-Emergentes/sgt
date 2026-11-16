package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import java.time.LocalDateTime;

import lat.soulware.sgt.monitoring.domain.model.exceptions.InvalidSubmissionYearException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The year a field sheet was submitted in, which its code is numbered within.
 *
 * @param value the year, four digits
 */
public record SubmissionYear(int value) implements ValueObject {

    public SubmissionYear {
        boolean isFourDigits = value >= 1000 && value <= 9999;
        if (!isFourDigits) {
            throw new InvalidSubmissionYearException(Integer.toString(value));
        }
    }

    /**
     * @param submissionDate when a field sheet was submitted
     * @return the year it falls in
     */
    public static SubmissionYear of(LocalDateTime submissionDate) {
        return new SubmissionYear(submissionDate.getYear());
    }
}

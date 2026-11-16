package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNumbersExhaustedException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.InvalidFieldSheetCodeException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The business identity of a field sheet: its number among the field sheets submitted in its year,
 * and that year, written {@code FI-0032-2026}. Numbering starts again at 1 every year and stops at
 * 9999.
 *
 * @param number its number in its year, from 1 to 9999
 * @param year   the year it was submitted in, never null
 */
public record FieldSheetCode(int number, SubmissionYear year) implements ValueObject {

    private static final int LAST_NUMBER = 9999;
    private static final Pattern FORMAT = Pattern.compile("^FI-(\\d{4})-(\\d{4})$");

    public FieldSheetCode {
        boolean isInRange = number >= 1 && number <= LAST_NUMBER;
        if (year == null || !isInRange) {
            String written = year == null ? "?" : Integer.toString(year.value());
            throw new InvalidFieldSheetCodeException("FI-%04d-%s".formatted(number, written));
        }
    }

    /**
     * @param year a year
     * @return the code of the first field sheet submitted in it
     */
    public static FieldSheetCode first(SubmissionYear year) {
        return new FieldSheetCode(1, year);
    }

    /**
     * @param text a code as written, such as {@code FI-0032-2026}
     * @return the code it spells
     * @throws InvalidFieldSheetCodeException if {@code text} is not one
     */
    public static FieldSheetCode parse(String text) {
        Matcher matcher = text == null ? null : FORMAT.matcher(text);
        if (matcher == null || !matcher.matches()) {
            throw new InvalidFieldSheetCodeException(text);
        }
        return new FieldSheetCode(Integer.parseInt(matcher.group(1)), new SubmissionYear(Integer.parseInt(matcher.group(2))));
    }

    /**
     * @return the code numbered right after this one in the same year
     * @throws FieldSheetNumbersExhaustedException if this one is already the year's last number
     */
    public FieldSheetCode next() {
        if (this.number == LAST_NUMBER) {
            throw new FieldSheetNumbersExhaustedException(this.year.value());
        }
        return new FieldSheetCode(this.number + 1, this.year);
    }

    /** @return the code as written, such as {@code FI-0032-2026} */
    public String value() {
        return "FI-%04d-%d".formatted(this.number, this.year.value());
    }
}

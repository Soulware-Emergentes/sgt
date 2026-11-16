package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNumbersExhaustedException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.InvalidFieldSheetCodeException;

/** Covers how a field sheet code is written, read back, and numbered within its year. */
class FieldSheetCodeTest {

    private static final SubmissionYear YEAR = new SubmissionYear(2026);

    @Test
    void aCodeIsWrittenWithItsNumberPaddedAndItsYear() {
        assertEquals("FI-0032-2026", new FieldSheetCode(32, YEAR).value());
    }

    @Test
    void aWrittenCodeReadsBackAsTheSameCode() {
        assertEquals(new FieldSheetCode(32, YEAR), FieldSheetCode.parse("FI-0032-2026"));
    }

    @Test
    void theNextCodeKeepsTheYear() {
        assertEquals(new FieldSheetCode(33, YEAR), new FieldSheetCode(32, YEAR).next());
    }

    @Test
    void aYearStopsAtItsLastNumber() {
        FieldSheetCode last = new FieldSheetCode(9999, YEAR);

        assertThrows(FieldSheetNumbersExhaustedException.class, last::next);
    }

    @ParameterizedTest
    @ValueSource(strings = {"FI-032-2026", "FI-00032-2026", "FI-0000-2026", "FX-0032-2026", "FI-0032-26", ""})
    void anythingElseIsRefused(String text) {
        assertThrows(InvalidFieldSheetCodeException.class, () -> FieldSheetCode.parse(text));
    }

    @Test
    void aCodeNeedsAYear() {
        assertThrows(InvalidFieldSheetCodeException.class, () -> new FieldSheetCode(1, null));
    }
}

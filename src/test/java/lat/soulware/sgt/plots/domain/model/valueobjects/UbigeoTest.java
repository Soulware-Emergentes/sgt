package lat.soulware.sgt.plots.domain.model.valueobjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import lat.soulware.sgt.plots.domain.model.exceptions.InvalidUbigeoException;
import lat.soulware.sgt.plots.domain.model.exceptions.MissingUbigeoException;

/** Covers what a ubigeo accepts: six digits, nothing shorter, longer, or other than digits. */
class UbigeoTest {

    @Test
    void sixDigitsAreADistrict() {
        assertEquals("150101", new Ubigeo("150101").value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"15010", "1501011", "15010A", " 150101", ""})
    void anythingButSixDigitsIsRefused(String code) {
        assertThrows(InvalidUbigeoException.class, () -> new Ubigeo(code));
    }

    @Test
    void aUbigeoNeedsACode() {
        assertThrows(MissingUbigeoException.class, () -> new Ubigeo(null));
    }
}

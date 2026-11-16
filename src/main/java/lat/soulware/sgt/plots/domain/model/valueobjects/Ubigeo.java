package lat.soulware.sgt.plots.domain.model.valueobjects;

import java.util.regex.Pattern;

import lat.soulware.sgt.plots.domain.model.exceptions.InvalidUbigeoException;
import lat.soulware.sgt.plots.domain.model.exceptions.MissingUbigeoException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The district a plot lies in, by its INEI code: two digits of department, two of province and two
 * of district, so {@code 150101} is in province {@code 1501}, in department {@code 15}.
 *
 * @param value the six-digit code
 */
public record Ubigeo(String value) implements ValueObject {

    private static final Pattern SIX_DIGITS = Pattern.compile("^\\d{6}$");

    public Ubigeo {
        if (value == null) {
            throw new MissingUbigeoException();
        }
        if (!SIX_DIGITS.matcher(value).matches()) {
            throw new InvalidUbigeoException(value);
        }
    }
}

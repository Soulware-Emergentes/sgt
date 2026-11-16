package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.plots.domain.model.valueobjects.Ubigeo} is built from anything but six digits. */
public final class InvalidUbigeoException extends BusinessRuleViolationException {

    /**
     * @param value the code given
     */
    public InvalidUbigeoException(String value) {
        super(value);
    }
}

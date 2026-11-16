package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.plots.domain.model.valueobjects.Ubigeo} is built from no code. */
public final class MissingUbigeoException extends BusinessRuleViolationException {

    public MissingUbigeoException() {
        super();
    }
}

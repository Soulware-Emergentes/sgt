package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.monitoring.domain.model.valueobjects.SupplyName} is blank. */
public final class BlankSupplyNameException extends BusinessRuleViolationException {

    public BlankSupplyNameException() {
        super();
    }
}

package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.monitoring.domain.model.valueobjects.SecurementDate} is null. */
public final class MissingSecurementDateException extends BusinessRuleViolationException {

    public MissingSecurementDateException() {
        super();
    }
}

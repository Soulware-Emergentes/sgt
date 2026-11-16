package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when field sheets are filtered by a status no field sheet can stand in. */
public final class UnknownFieldSheetStatusException extends BusinessRuleViolationException {

    /**
     * @param status the status asked for
     */
    public UnknownFieldSheetStatusException(String status) {
        super(status);
    }
}

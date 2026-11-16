package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetCode} is not one. */
public final class InvalidFieldSheetCodeException extends BusinessRuleViolationException {

    /**
     * @param rejected the code as given
     */
    public InvalidFieldSheetCodeException(String rejected) {
        super(rejected);
    }
}

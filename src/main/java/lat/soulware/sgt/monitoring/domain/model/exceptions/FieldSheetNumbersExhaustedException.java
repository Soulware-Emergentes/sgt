package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet is submitted in a year that already numbered its 9999th. */
public final class FieldSheetNumbersExhaustedException extends BusinessRuleViolationException {

    /**
     * @param year the year with no number left
     */
    public FieldSheetNumbersExhaustedException(int year) {
        super(Integer.toString(year));
    }
}

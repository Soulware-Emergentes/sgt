package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetStatus;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet whose delivery poi has not accepted is reviewed. */
public final class FieldSheetNotDeliveredException extends BusinessRuleViolationException {

    /**
     * @param fieldSheetId the field sheet
     * @param status       where it stands
     */
    public FieldSheetNotDeliveredException(FieldSheetId fieldSheetId, FieldSheetStatus status) {
        super(fieldSheetId, status);
    }
}

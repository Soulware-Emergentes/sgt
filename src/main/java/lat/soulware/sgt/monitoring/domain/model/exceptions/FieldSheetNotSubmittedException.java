package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetStatus;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when poi's answer to a delivery is recorded on a field sheet that already has a different one. */
public final class FieldSheetNotSubmittedException extends BusinessRuleViolationException {

    /**
     * @param fieldSheetId the field sheet
     * @param status       where it stands
     */
    public FieldSheetNotSubmittedException(FieldSheetId fieldSheetId, FieldSheetStatus status) {
        super(fieldSheetId, status);
    }
}

package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetStatus;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet that was already approved or rejected is reviewed again. */
public final class FieldSheetAlreadyReviewedException extends BusinessRuleViolationException {

    public FieldSheetAlreadyReviewedException(FieldSheetId id, FieldSheetStatus status) {
        super(id.value(), status);
    }
}

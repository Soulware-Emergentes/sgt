package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/**
 * Thrown when someone reviews a field sheet they submitted themselves. One person may act as a field
 * agent and as a general management assistant, but never on both sides of the same field sheet.
 */
public final class ReviewerIsSubmitterException extends BusinessRuleViolationException {

    public ReviewerIsSubmitterException(FieldSheetId fieldSheetId) {
        super(fieldSheetId.value());
    }
}

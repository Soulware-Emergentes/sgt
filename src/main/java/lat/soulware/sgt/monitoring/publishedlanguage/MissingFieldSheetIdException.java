package lat.soulware.sgt.monitoring.publishedlanguage;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId} is null. */
public final class MissingFieldSheetIdException extends BusinessRuleViolationException {

    public MissingFieldSheetIdException() {
        super();
    }
}

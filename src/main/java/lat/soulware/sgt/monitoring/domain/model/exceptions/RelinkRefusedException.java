package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when poi refuses to relink a field sheet's delivery to the task it names. */
public final class RelinkRefusedException extends BusinessRuleViolationException {

    public RelinkRefusedException(FieldSheetId fieldSheetId, TaskCode taskCode) {
        super(fieldSheetId.value(), taskCode.value());
    }
}

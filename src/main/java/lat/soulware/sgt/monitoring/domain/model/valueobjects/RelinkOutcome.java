package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import lat.soulware.sgt.monitoring.domain.model.exceptions.InvalidRelinkOutcomeException;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * poi's answer to a relink.
 *
 * @param result      what poi did
 * @param swappedWith the field sheet whose delivery took the old task, present only when swapped
 */
public record RelinkOutcome(RelinkResult result, FieldSheetId swappedWith) implements ValueObject {

    public RelinkOutcome {
        boolean isSwap = result == RelinkResult.SWAPPED;
        boolean namesAFieldSheet = swappedWith != null;
        if (result == null || isSwap != namesAFieldSheet) {
            throw new InvalidRelinkOutcomeException();
        }
    }
}

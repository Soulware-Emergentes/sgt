package lat.soulware.sgt.people.publishedlanguage;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.people.publishedlanguage.StaffId} is null. */
public final class MissingStaffIdException extends BusinessRuleViolationException {

    public MissingStaffIdException() {
        super();
    }
}

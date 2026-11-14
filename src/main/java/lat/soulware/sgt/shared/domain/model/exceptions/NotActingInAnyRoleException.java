package lat.soulware.sgt.shared.domain.model.exceptions;

/**
 * Thrown when a caller's token names no sgt role to act as. A person eligible for several roles picks
 * one before acting.
 */
public final class NotActingInAnyRoleException extends ForbiddenException {

    public NotActingInAnyRoleException() {
        super();
    }
}

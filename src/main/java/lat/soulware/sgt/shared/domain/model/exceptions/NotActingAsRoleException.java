package lat.soulware.sgt.shared.domain.model.exceptions;

/**
 * Thrown when a caller asks for what only one role may do while acting as another.
 */
public final class NotActingAsRoleException extends ForbiddenException {

    /**
     * @param role the value of the role the action needs, as the token names it
     */
    public NotActingAsRoleException(String role) {
        super(role);
    }
}

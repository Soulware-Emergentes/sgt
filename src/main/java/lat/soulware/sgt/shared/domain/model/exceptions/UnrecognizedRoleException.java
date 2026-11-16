package lat.soulware.sgt.shared.domain.model.exceptions;

/**
 * Thrown when a caller's token names a role sgt does not define.
 */
public final class UnrecognizedRoleException extends ForbiddenException {

    /**
     * @param role the value the token names
     */
    public UnrecognizedRoleException(String role) {
        super(role);
    }
}

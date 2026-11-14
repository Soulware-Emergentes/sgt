package lat.soulware.sgt.shared.interfaces.rest.security;

import java.util.Set;
import java.util.UUID;

import lat.soulware.sgt.shared.domain.model.exceptions.NotActingAsRoleException;

/**
 * Who is making a request, as the token staff issued says, in types a module can read without
 * knowing about tokens. A controller takes one as a parameter, and its mapper turns it into the actor
 * the module's commands expect, refusing a caller not acting in the role the operation needs.
 *
 * @param subject the objectGUID of the person's directory account
 * @param roles   the roles the person is acting as in sgt, usually one
 */
public record Caller(UUID subject, Set<Role> roles) {

    public Caller {
        roles = Set.copyOf(roles);
    }

    /**
     * @param role a role sgt defines
     * @return whether the caller acts as it, which an admin always does
     */
    public boolean actsAs(Role role) {
        return this.roles.contains(role) || this.roles.contains(Role.ADMIN);
    }

    /**
     * The caller's identity as the actor an operation needs, for a mapper to wrap in its module's
     * actor type.
     *
     * @param role the role the operation needs
     * @return the subject, when the caller acts as that role
     * @throws NotActingAsRoleException if the caller does not act as it
     */
    public UUID actingAs(Role role) {
        if (!this.actsAs(role)) {
            throw new NotActingAsRoleException(role.claimValue());
        }
        return this.subject;
    }
}

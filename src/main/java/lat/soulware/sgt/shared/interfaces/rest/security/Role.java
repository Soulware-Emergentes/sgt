package lat.soulware.sgt.shared.interfaces.rest.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import lat.soulware.sgt.shared.domain.model.exceptions.NotActingInAnyRoleException;
import lat.soulware.sgt.shared.domain.model.exceptions.UnrecognizedRoleException;

/**
 * The roles sgt defines, each with the value staff writes for it in the token's roles claim.
 */
public enum Role {
    FIELD_AGENT("field-agent"),
    GENERAL_MANAGEMENT_ASSISTANT("general-management-assistant"),
    ADMIN("admin");

    private final String claimValue;

    Role(String claimValue) {
        this.claimValue = claimValue;
    }

    /** @return the value staff writes for this role in the token */
    public String claimValue() {
        return this.claimValue;
    }

    /**
     * Reads the roles claim of a token.
     *
     * @param claimValues the values the claim holds, null when the token carries none
     * @return the roles they name
     * @throws NotActingInAnyRoleException if the claim names no role
     * @throws UnrecognizedRoleException   if a value names no role sgt defines
     */
    public static Set<Role> fromClaim(Collection<String> claimValues) {
        boolean namesNoRole = Objects.isNull(claimValues) || claimValues.isEmpty();
        if (namesNoRole) {
            throw new NotActingInAnyRoleException();
        }

        Set<Role> roles = EnumSet.noneOf(Role.class);
        claimValues.forEach(value -> roles.add(fromClaimValue(value)));
        return Set.copyOf(roles);
    }

    private static Role fromClaimValue(String value) {
        return Arrays.stream(values())
            .filter(role -> role.claimValue.equals(value))
            .findFirst()
            .orElseThrow(() -> new UnrecognizedRoleException(value));
    }
}

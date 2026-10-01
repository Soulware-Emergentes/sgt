package lat.soulware.sgt.people.publishedlanguage;

import java.util.Objects;
import java.util.UUID;

import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * Identity of a member of the organization's staff: the {@code objectGUID} of their directory
 * account, which survives renames where the username does not.
 *
 * <p>One subtype per kind of staff, so a signature asking for one kind cannot be handed another.
 * Two identities are equal only when both their kind and their value match.
 */
public abstract sealed class StaffId implements ValueObject permits FieldAgentId, GeneralManagementAssistantId {

    private final UUID value;

    /**
     * @param value the directory account's GUID, never null
     */
    protected StaffId(UUID value) {
        if (value == null) {
            throw new MissingStaffIdException();
        }
        this.value = value;
    }

    /** @return the directory account's GUID, never null */
    public UUID value() {
        return this.value;
    }

    @Override
    public boolean equals(Object other) {
        return other != null
            && other.getClass() == this.getClass()
            && ((StaffId) other).value.equals(this.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.value);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[value=" + this.value + "]";
    }
}

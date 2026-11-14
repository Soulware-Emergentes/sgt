package lat.soulware.sgt.people.publishedlanguage;

import java.util.UUID;

import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * Identity of a field agent, the staff member who visits a plot and conducts its evaluation: the
 * {@code objectGUID} of their directory account, which survives renames where the username does not.
 * Never equal to a {@link GeneralManagementAssistantId}, even for the same account.
 *
 * @param value the field agent's directory account GUID
 */
public record FieldAgentId(UUID value) implements ValueObject {

    public FieldAgentId {
        if (value == null) {
            throw new MissingStaffIdException();
        }
    }
}

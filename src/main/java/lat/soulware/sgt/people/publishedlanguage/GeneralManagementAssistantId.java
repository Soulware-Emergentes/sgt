package lat.soulware.sgt.people.publishedlanguage;

import java.util.UUID;

import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * Identity of a general management assistant, the staff member who reviews the evaluations field
 * agents submit: the {@code objectGUID} of their directory account, which survives renames where the
 * username does not. Never equal to a {@link FieldAgentId}, even for the same account.
 *
 * @param value the general management assistant's directory account GUID
 */
public record GeneralManagementAssistantId(UUID value) implements ValueObject {

    public GeneralManagementAssistantId {
        if (value == null) {
            throw new MissingStaffIdException();
        }
    }
}

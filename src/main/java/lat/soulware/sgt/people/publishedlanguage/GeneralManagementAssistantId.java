package lat.soulware.sgt.people.publishedlanguage;

import java.util.UUID;

/**
 * Identity of a general management assistant, the staff member who reviews the evaluations field
 * agents submit.
 */
public final class GeneralManagementAssistantId extends StaffId {

    /**
     * @param value the general management assistant's directory account GUID, never null
     */
    public GeneralManagementAssistantId(UUID value) {
        super(value);
    }
}

package lat.soulware.sgt.people.publishedlanguage;

import java.util.UUID;

/** Identity of a field agent, the staff member who visits a plot and conducts its evaluation. */
public final class FieldAgentId extends StaffId {

    /**
     * @param value the field agent's directory account GUID, never null
     */
    public FieldAgentId(UUID value) {
        super(value);
    }
}

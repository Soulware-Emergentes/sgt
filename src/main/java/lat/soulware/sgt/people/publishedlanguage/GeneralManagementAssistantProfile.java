package lat.soulware.sgt.people.publishedlanguage;

import java.util.Objects;

import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * A general management assistant's personal data, as the directory holds it.
 *
 * @param id          the general management assistant's identity
 * @param username    the directory username, which may change over time
 * @param displayName the name to show for the general management assistant
 */
public record GeneralManagementAssistantProfile(GeneralManagementAssistantId id, String username, String displayName) implements ValueObject {

    public GeneralManagementAssistantProfile {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(displayName, "displayName");
    }
}

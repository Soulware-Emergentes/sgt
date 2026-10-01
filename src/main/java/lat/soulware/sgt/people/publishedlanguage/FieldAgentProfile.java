package lat.soulware.sgt.people.publishedlanguage;

/**
 * A field agent's personal data, as the directory holds it.
 *
 * @param id          the field agent's identity
 * @param username    the directory username, which may change over time
 * @param displayName the name to show for the field agent
 */
public record FieldAgentProfile(FieldAgentId id, String username, String displayName) {
}

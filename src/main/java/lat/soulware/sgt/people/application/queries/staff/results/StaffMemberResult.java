package lat.soulware.sgt.people.application.queries.staff.results;

import java.util.UUID;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * One staff member, as the directory returned them.
 *
 * @param id          the directory account GUID
 * @param username    the directory username
 * @param displayName the name to show
 */
public record StaffMemberResult(UUID id, String username, String displayName) implements QueryResult {
}

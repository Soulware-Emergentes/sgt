package lat.soulware.sgt.people.application.queries.staff.results;

import java.util.List;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * The staff members a lookup found.
 *
 * @param staffMembers the staff members, empty when none were found
 */
public record StaffMembersResult(List<StaffMemberResult> staffMembers) implements QueryResult {

    public StaffMembersResult {
        staffMembers = List.copyOf(staffMembers);
    }
}

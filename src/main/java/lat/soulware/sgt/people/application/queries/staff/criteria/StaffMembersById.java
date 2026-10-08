package lat.soulware.sgt.people.application.queries.staff.criteria;

import java.util.List;
import java.util.UUID;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * The staff members to look up, by directory account GUID.
 *
 * @param ids the GUIDs, copied defensively
 */
public record StaffMembersById(List<UUID> ids) implements QueryCriteria {

    public StaffMembersById {
        ids = List.copyOf(ids);
    }
}

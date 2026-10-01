package lat.soulware.sgt.people.application.queries.staff.criteria;

import java.util.List;
import java.util.UUID;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * The field agents to look up, by directory account GUID.
 *
 * @param ids the GUIDs, copied defensively
 */
public record FieldAgentsById(List<UUID> ids) implements QueryCriteria {

    public FieldAgentsById {
        ids = List.copyOf(ids);
    }
}

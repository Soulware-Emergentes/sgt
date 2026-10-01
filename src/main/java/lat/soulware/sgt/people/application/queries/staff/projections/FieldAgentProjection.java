package lat.soulware.sgt.people.application.queries.staff.projections;

import java.util.List;
import java.util.UUID;

import lat.soulware.sgt.people.application.queries.staff.results.FieldAgentResult;
import lat.soulware.sgt.shared.application.queries.projections.Projection;

/** The field agents the directory holds. */
public interface FieldAgentProjection extends Projection {

    /**
     * @param ids the directory account GUIDs to look up
     * @return the field agents found, leaving out the GUIDs the directory does not know as one
     */
    List<FieldAgentResult> findByIds(List<UUID> ids);
}

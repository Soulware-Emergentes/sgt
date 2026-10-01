package lat.soulware.sgt.people.application.queries.staff;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.people.application.queries.staff.criteria.FieldAgentsById;
import lat.soulware.sgt.people.application.queries.staff.projections.FieldAgentProjection;
import lat.soulware.sgt.people.application.queries.staff.results.FieldAgentResult;
import lat.soulware.sgt.shared.application.queries.QueryService;

/** The read entry point for staff. */
@Transactional(readOnly = true)
@Service
public class StaffQueryService implements QueryService {

    private final FieldAgentProjection fieldAgentProjection;

    public StaffQueryService(FieldAgentProjection fieldAgentProjection) {
        this.fieldAgentProjection = fieldAgentProjection;
    }

    public List<FieldAgentResult> fieldAgentsById(FieldAgentsById criteria) {
        if (criteria.ids().isEmpty()) {
            return List.of();
        }
        return this.fieldAgentProjection.findByIds(criteria.ids().stream().distinct().toList());
    }
}

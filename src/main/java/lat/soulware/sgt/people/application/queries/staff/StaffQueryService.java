package lat.soulware.sgt.people.application.queries.staff;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.people.application.queries.staff.criteria.StaffMembersById;
import lat.soulware.sgt.people.application.queries.staff.projections.PersonProjection;
import lat.soulware.sgt.people.application.queries.staff.results.StaffMembersResult;
import lat.soulware.sgt.shared.application.queries.QueryService;

/** The read entry point for staff. */
@Transactional(readOnly = true)
@Service
public class StaffQueryService implements QueryService {

    private final PersonProjection personProjection;

    public StaffQueryService(PersonProjection personProjection) {
        this.personProjection = personProjection;
    }

    /**
     * Both roles resolve the same way: the directory knows people, not what they do in sgt. The two
     * methods keep the role sgt recorded each person in visible at the call site.
     */
    public StaffMembersResult fieldAgentsById(StaffMembersById criteria) {
        return this.byId(criteria);
    }

    public StaffMembersResult generalManagementAssistantsById(StaffMembersById criteria) {
        return this.byId(criteria);
    }

    private StaffMembersResult byId(StaffMembersById criteria) {
        if (criteria.ids().isEmpty()) {
            return new StaffMembersResult(List.of());
        }
        return new StaffMembersResult(this.personProjection.findByIds(criteria.ids().stream().distinct().toList()));
    }
}

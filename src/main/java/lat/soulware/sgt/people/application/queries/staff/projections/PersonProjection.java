package lat.soulware.sgt.people.application.queries.staff.projections;

import java.util.List;
import java.util.UUID;

import lat.soulware.sgt.people.application.queries.staff.results.StaffMemberResult;
import lat.soulware.sgt.shared.application.queries.projections.Projection;

/**
 * The people the directory holds, whatever their job. Someone acting as a general management
 * assistant in sgt may hold another job, so sgt resolves people by GUID alone and calls them by the
 * role it recorded them in.
 */
public interface PersonProjection extends Projection {

    /**
     * @param ids the directory account GUIDs to look up
     * @return the people found, leaving out the GUIDs the directory does not know
     */
    List<StaffMemberResult> findByIds(List<UUID> ids);
}

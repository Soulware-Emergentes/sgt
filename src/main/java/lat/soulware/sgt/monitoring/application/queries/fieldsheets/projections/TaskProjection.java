package lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections;

import java.util.List;

import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.PendingTasks;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.TaskByCode;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.PendingTaskResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.TaskResult;
import lat.soulware.sgt.shared.application.queries.projections.Projection;

/** poi's tasks, as field sheets are submitted for them and reviewed against them. */
public interface TaskProjection extends Projection {

    /**
     * @param criteria the task to read
     * @return the task as poi holds it
     */
    TaskResult findByCode(TaskByCode criteria);

    /**
     * @param criteria which pending tasks to read
     * @return the pending tasks, ordered by code
     */
    List<PendingTaskResult> findPending(PendingTasks criteria);
}

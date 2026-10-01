package lat.soulware.sgt.monitoring.application.queries.fieldsheets.results;

import java.util.List;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * The poi tasks waiting for a field sheet, ordered by code.
 *
 * @param tasks the tasks, empty when none are pending
 */
public record PendingTasksResult(List<PendingTaskResult> tasks) implements QueryResult {

    public PendingTasksResult {
        tasks = List.copyOf(tasks);
    }
}

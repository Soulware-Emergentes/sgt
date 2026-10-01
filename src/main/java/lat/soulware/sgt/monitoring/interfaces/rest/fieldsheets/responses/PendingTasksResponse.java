package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses;

import java.util.List;

/**
 * The poi tasks waiting for a field sheet.
 *
 * @param tasks the tasks, ordered by code
 */
public record PendingTasksResponse(List<PendingTaskResponse> tasks) {
}

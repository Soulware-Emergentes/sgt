package lat.soulware.sgt.monitoring.application.queries.fieldsheets.results;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * A poi task waiting for a field sheet.
 *
 * @param code                the task's code
 * @param description         what the task is
 * @param activityCode        the activity it is planned under
 * @param activityDescription what that activity is
 */
public record PendingTaskResult(String code, String description, String activityCode, String activityDescription) implements QueryResult {
}

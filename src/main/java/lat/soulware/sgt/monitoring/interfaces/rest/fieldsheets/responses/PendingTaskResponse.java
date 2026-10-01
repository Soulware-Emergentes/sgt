package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses;

/**
 * A poi task waiting for a field sheet.
 *
 * @param code                the task's code
 * @param description         what the task is
 * @param activityCode        the activity it is planned under
 * @param activityDescription what that activity is
 */
public record PendingTaskResponse(String code, String description, String activityCode, String activityDescription) {
}

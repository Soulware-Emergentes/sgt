package lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * The poi tasks a field sheet can be submitted for, optionally under one activity.
 *
 * @param activityCode the activity to narrow to, or null for every activity
 */
public record PendingTasks(String activityCode) implements QueryCriteria {
}

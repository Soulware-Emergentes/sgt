package lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * The field sheets standing at one step of their review.
 *
 * @param status the step, by name
 */
public record FieldSheetsInStatus(String status) implements QueryCriteria {
}

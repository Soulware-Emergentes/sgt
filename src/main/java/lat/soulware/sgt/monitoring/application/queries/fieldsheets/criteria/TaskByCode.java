package lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * One poi task, as a reviewer checks a field sheet's evidence against it.
 *
 * @param code the task's code, as written
 */
public record TaskByCode(String code) implements QueryCriteria {
}

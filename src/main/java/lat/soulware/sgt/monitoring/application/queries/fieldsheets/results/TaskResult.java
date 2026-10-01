package lat.soulware.sgt.monitoring.application.queries.fieldsheets.results;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * A poi task, as a reviewer checks a field sheet's evidence against it.
 *
 * @param code          the task's code
 * @param description   what the task is
 * @param status        where the task stands in poi, by name
 * @param beneficiaryId the beneficiary the task is planned to visit
 */
public record TaskResult(String code, String description, String status, String beneficiaryId) implements QueryResult {
}

package lat.soulware.sgt.monitoring.application.queries.fieldsheets.results;

import java.time.LocalDateTime;
import java.util.UUID;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * A field sheet in a list.
 *
 * @param id             the field sheet's identity
 * @param code           the field sheet's code, such as FI-0032-2026
 * @param taskCode       the poi task it is linked to
 * @param status         where it stands, by name
 * @param submittedBy    the field agent who submitted it
 * @param submissionDate when it was submitted
 */
public record FieldSheetSummaryResult(
    UUID id,
    String code,
    String taskCode,
    String status,
    UUID submittedBy,
    LocalDateTime submissionDate
) implements QueryResult {
}

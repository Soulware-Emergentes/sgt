package lat.soulware.sgt.monitoring.application.queries.fieldsheets.results;

import java.util.List;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * Field sheets, oldest submission first.
 *
 * @param fieldSheets the field sheets, empty when none match
 */
public record FieldSheetsResult(List<FieldSheetSummaryResult> fieldSheets) implements QueryResult {

    public FieldSheetsResult {
        fieldSheets = List.copyOf(fieldSheets);
    }
}

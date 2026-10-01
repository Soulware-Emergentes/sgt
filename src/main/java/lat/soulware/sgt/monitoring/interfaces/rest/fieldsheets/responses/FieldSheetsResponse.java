package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses;

import java.util.List;

/**
 * Field sheets, oldest submission first.
 *
 * @param fieldSheets the field sheets
 */
public record FieldSheetsResponse(List<FieldSheetSummaryResponse> fieldSheets) {
}

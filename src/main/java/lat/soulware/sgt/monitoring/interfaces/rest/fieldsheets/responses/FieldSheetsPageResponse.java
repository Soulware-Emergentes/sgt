package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses;

import java.util.List;

/**
 * One page of field sheets.
 *
 * @param content       the field sheets on the page, in the order asked for
 * @param totalPages    how many pages the whole selection fills
 * @param totalElements how many field sheets the whole selection holds
 * @param actualPage    the page, counted from zero
 * @param pageSize      how many field sheets a page holds
 */
public record FieldSheetsPageResponse(
    List<FieldSheetSummaryResponse> content,
    int totalPages,
    long totalElements,
    int actualPage,
    int pageSize
) {
}

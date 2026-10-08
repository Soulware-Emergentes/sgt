package lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * One page of field sheets, optionally narrowed. Every filter left null matches every field sheet.
 *
 * @param status        the status the field sheets stand in, by name
 * @param code          text the field sheet's code contains, ignoring case
 * @param taskCode      text the poi task code contains
 * @param submittedFrom the first day of submission to include, written {@code 2026-10-01}
 * @param submittedTo   the last day of submission to include, written {@code 2026-10-31}
 * @param sort          the column to order by: {@code code}, {@code taskCode}, {@code submittedAt} or
 *                      {@code status}
 * @param direction     {@code asc} or {@code desc}
 * @param page          the page, counted from zero
 * @param size          how many field sheets a page holds
 */
public record FieldSheetsPage(
    String status,
    String code,
    String taskCode,
    String submittedFrom,
    String submittedTo,
    String sort,
    String direction,
    int page,
    int size
) implements QueryCriteria {
}

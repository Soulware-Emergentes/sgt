package lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria;

import java.util.UUID;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * The scanned PDF of one field sheet, to download.
 *
 * @param fieldSheetId the field sheet's identity
 */
public record ScanOfFieldSheet(UUID fieldSheetId) implements QueryCriteria {
}

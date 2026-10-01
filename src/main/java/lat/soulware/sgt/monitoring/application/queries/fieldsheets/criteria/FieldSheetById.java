package lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria;

import java.util.UUID;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * One field sheet.
 *
 * @param id the field sheet's identity
 */
public record FieldSheetById(UUID id) implements QueryCriteria {
}

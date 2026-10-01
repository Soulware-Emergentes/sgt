package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses;

import java.util.UUID;

/**
 * A submitted field sheet.
 *
 * @param id the field sheet's identity
 */
public record FieldSheetCreatedResponse(UUID id) {
}

package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests;

import java.util.UUID;

/**
 * A rejection.
 *
 * @param reviewer the general management assistant rejecting it
 */
public record RejectFieldSheetRequest(UUID reviewer) {
}

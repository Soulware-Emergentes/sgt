package lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent;

import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Reject a field sheet's delivery in poi, once the field sheet is rejected.
 *
 * @param fieldSheetId the reviewed field sheet
 */
public record RejectFieldSheetDelivery(FieldSheetId fieldSheetId) implements CommandIntent {
}

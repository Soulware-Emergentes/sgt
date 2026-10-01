package lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent;

import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Confirm a field sheet's delivery in poi, once the field sheet is approved.
 *
 * @param fieldSheetId the approved field sheet
 */
public record ConfirmFieldSheetDelivery(FieldSheetId fieldSheetId) implements CommandIntent {
}

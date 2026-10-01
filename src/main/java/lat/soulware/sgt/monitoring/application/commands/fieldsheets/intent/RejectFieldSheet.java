package lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent;

import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantId;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Reject a submitted field sheet, turning its evaluation down.
 *
 * @param fieldSheetId the field sheet under review
 * @param reviewer     the general management assistant reviewing it
 */
public record RejectFieldSheet(FieldSheetId fieldSheetId, GeneralManagementAssistantId reviewer) implements CommandIntent {
}

package lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent;

import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Complete the poi task a field sheet was approved for.
 *
 * @param fieldSheetId the approved field sheet
 */
public record CompleteTask(FieldSheetId fieldSheetId) implements CommandIntent {
}

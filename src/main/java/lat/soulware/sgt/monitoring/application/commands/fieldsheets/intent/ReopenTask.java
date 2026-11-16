package lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent;

import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Reopen the poi task a field sheet was rejected for, so it is on the pending list again.
 *
 * @param fieldSheetId the rejected field sheet
 */
public record ReopenTask(FieldSheetId fieldSheetId) implements CommandIntent {
}

package lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent;

import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Deliver a submitted field sheet to poi, against the task it names, and record poi's answer.
 *
 * @param fieldSheetId the submitted field sheet
 */
public record DeliverFieldSheet(FieldSheetId fieldSheetId) implements CommandIntent {
}

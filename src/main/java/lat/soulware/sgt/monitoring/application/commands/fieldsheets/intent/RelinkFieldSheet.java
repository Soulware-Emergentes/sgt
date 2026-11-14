package lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Relink a field sheet under review to the task its evidence belongs to.
 *
 * @param fieldSheetId the field sheet under review
 * @param taskCode     the task its evidence belongs to
 */
public record RelinkFieldSheet(FieldSheetId fieldSheetId, TaskCode taskCode) implements CommandIntent {
}

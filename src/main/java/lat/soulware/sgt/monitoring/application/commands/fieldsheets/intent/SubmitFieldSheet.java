package lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentId;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Submit a field sheet for a poi task, as the field agent who carried it out.
 *
 * @param taskCode    the poi task the field sheet is submitted for
 * @param scanKey     where the field agent uploaded the scanned PDF
 * @param submittedBy the field agent submitting it
 */
public record SubmitFieldSheet(TaskCode taskCode, ScanKey scanKey, FieldAgentId submittedBy) implements CommandIntent {
}

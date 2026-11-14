package lat.soulware.sgt.monitoring.domain.model.events;

import java.time.Instant;

import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.model.events.DomainEvent;
import lat.soulware.sgt.shared.domain.model.events.EmittedBy;

/**
 * A field agent submitted a field sheet for a poi task, which now has to be delivered to poi.
 *
 * @param fieldSheetId the submitted field sheet
 * @param occurredOn   when the submission was recorded
 */
@EmittedBy("fieldsheets")
public record FieldSheetSubmitted(FieldSheetId fieldSheetId, Instant occurredOn) implements DomainEvent {
}

package lat.soulware.sgt.monitoring.domain.model.events;

import java.time.Instant;

import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.model.events.DomainEvent;
import lat.soulware.sgt.shared.domain.model.events.EmittedBy;

/**
 * A general management assistant turned a field sheet's evaluation down, so the task it was
 * submitted for needs a delivery again.
 *
 * @param fieldSheetId the rejected field sheet
 * @param occurredOn   when the rejection was recorded
 */
@EmittedBy("fieldsheets")
public record FieldSheetRejected(FieldSheetId fieldSheetId, Instant occurredOn) implements DomainEvent {
}

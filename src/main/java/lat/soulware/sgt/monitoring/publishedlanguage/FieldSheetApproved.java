package lat.soulware.sgt.monitoring.publishedlanguage;

import java.time.Instant;

import lat.soulware.sgt.shared.domain.model.events.DomainEvent;
import lat.soulware.sgt.shared.domain.model.events.EmittedBy;

/**
 * A general management assistant accepted a field sheet's evaluation as correct.
 *
 * @param fieldSheetId     the approved field sheet
 * @param occurredOn when the approval was recorded
 */
@EmittedBy("fieldsheets")
public record FieldSheetApproved(FieldSheetId fieldSheetId, Instant occurredOn) implements DomainEvent {
}

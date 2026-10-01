package lat.soulware.sgt.monitoring.publishedlanguage;

import java.util.UUID;

import lat.soulware.sgt.shared.domain.model.valueobjects.AggregateId;

/**
 * Identity of a {@link lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet}.
 *
 * @param value the underlying key, never null
 */
public record FieldSheetId(UUID value) implements AggregateId<UUID> {

    public FieldSheetId {
        if (value == null) {
            throw new MissingFieldSheetIdException();
        }
    }
}

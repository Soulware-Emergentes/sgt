package lat.soulware.sgt.plots.domain.model.valueobjects;

import java.util.UUID;

import lat.soulware.sgt.plots.domain.model.exceptions.MissingPlotIdException;
import lat.soulware.sgt.shared.domain.model.valueobjects.AggregateId;

/**
 * Identity of a {@link lat.soulware.sgt.plots.domain.model.aggregates.Plot}.
 *
 * @param value the underlying key, never null
 */
public record PlotId(UUID value) implements AggregateId<UUID> {

    public PlotId {
        if (value == null) {
            throw new MissingPlotIdException();
        }
    }
}

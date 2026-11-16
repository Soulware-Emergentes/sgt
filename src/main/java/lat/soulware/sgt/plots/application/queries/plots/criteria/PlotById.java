package lat.soulware.sgt.plots.application.queries.plots.criteria;

import java.util.UUID;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * One plot.
 *
 * @param id the plot's identity
 */
public record PlotById(UUID id) implements QueryCriteria {
}

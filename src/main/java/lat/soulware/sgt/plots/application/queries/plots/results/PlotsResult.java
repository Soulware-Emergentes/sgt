package lat.soulware.sgt.plots.application.queries.plots.results;

import java.util.List;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * Plots, ordered by ubigeo and then by when they were registered.
 *
 * @param plots the plots, empty when none match
 */
public record PlotsResult(List<PlotResult> plots) implements QueryResult {

    public PlotsResult {
        plots = List.copyOf(plots);
    }
}

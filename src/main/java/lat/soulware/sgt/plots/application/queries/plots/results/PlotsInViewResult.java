package lat.soulware.sgt.plots.application.queries.plots.results;

import java.util.List;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * The plots a map shows, up to as many as one answer carries.
 *
 * @param plots     the plots, empty when none touch the view
 * @param truncated whether more plots touch the view than were answered, which a map shows by asking
 *                  for a smaller view
 */
public record PlotsInViewResult(List<PlotFootprintResult> plots, boolean truncated) implements QueryResult {

    public PlotsInViewResult {
        plots = List.copyOf(plots);
    }
}

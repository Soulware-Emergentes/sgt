package lat.soulware.sgt.plots.application.queries.plots.results;

import java.util.List;
import java.util.UUID;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * A plot as a map draws it: its boundary on WGS84 rather than in the UTM zone it was surveyed in.
 *
 * @param id        the plot's identity
 * @param ubigeo    the district it lies in
 * @param footprint the boundary's vertices in order, the ring closed
 */
public record PlotFootprintResult(UUID id, String ubigeo, List<CoordinateResult> footprint) implements QueryResult {

    public PlotFootprintResult {
        footprint = List.copyOf(footprint);
    }
}

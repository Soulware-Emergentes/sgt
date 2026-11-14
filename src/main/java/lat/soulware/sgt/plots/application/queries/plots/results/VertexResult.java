package lat.soulware.sgt.plots.application.queries.plots.results;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * One vertex of a plot's surveyed boundary, in the coordinates of its UTM zone.
 *
 * @param easting  metres east
 * @param northing metres north
 */
public record VertexResult(double easting, double northing) implements QueryResult {
}

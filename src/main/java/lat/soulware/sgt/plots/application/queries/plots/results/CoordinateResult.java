package lat.soulware.sgt.plots.application.queries.plots.results;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * A point on WGS84, the way a map places it.
 *
 * @param longitude degrees east
 * @param latitude  degrees north
 */
public record CoordinateResult(double longitude, double latitude) implements QueryResult {
}

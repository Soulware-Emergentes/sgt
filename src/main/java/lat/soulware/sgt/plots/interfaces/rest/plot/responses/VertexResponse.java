package lat.soulware.sgt.plots.interfaces.rest.plot.responses;

/**
 * One vertex of a plot's boundary.
 *
 * @param easting  metres east
 * @param northing metres north
 */
public record VertexResponse(double easting, double northing) {
}

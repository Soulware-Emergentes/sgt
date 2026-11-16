package lat.soulware.sgt.plots.interfaces.rest.plot.requests;

/**
 * One vertex of a surveyed perimeter.
 *
 * @param easting  metres east
 * @param northing metres north
 */
public record VertexRequest(double easting, double northing) {
}

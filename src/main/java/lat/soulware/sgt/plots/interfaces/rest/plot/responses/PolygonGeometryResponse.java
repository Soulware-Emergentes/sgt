package lat.soulware.sgt.plots.interfaces.rest.plot.responses;

import java.util.List;

/**
 * A GeoJSON Polygon.
 *
 * @param type        always {@code Polygon}
 * @param coordinates its one ring, as positions of longitude and latitude, closed
 */
public record PolygonGeometryResponse(String type, List<List<List<Double>>> coordinates) {

    public static final String TYPE = "Polygon";
}

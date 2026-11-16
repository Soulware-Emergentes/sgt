package lat.soulware.sgt.plots.interfaces.rest.plot.responses;

import java.util.UUID;

/**
 * One plot as a GeoJSON Feature.
 *
 * @param type       always {@code Feature}
 * @param id         the plot's identity, which reads the whole plot from {@code /plots/{id}}
 * @param geometry   its footprint on WGS84
 * @param properties what a map may style or label it by
 */
public record PlotFeatureResponse(
    String type,
    UUID id,
    PolygonGeometryResponse geometry,
    PlotPropertiesResponse properties
) {

    public static final String TYPE = "Feature";
}

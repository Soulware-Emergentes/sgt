package lat.soulware.sgt.plots.interfaces.rest.plot.responses;

import java.util.List;

/**
 * The plots a map shows, as a GeoJSON FeatureCollection (RFC 7946) a map library draws as it is.
 *
 * @param type      always {@code FeatureCollection}
 * @param features  one feature per plot
 * @param truncated whether more plots touch the view than were answered; a foreign member the
 *                  GeoJSON format allows, so a map knows to ask for a smaller view
 */
public record PlotFeatureCollectionResponse(String type, List<PlotFeatureResponse> features, boolean truncated) {

    public static final String TYPE = "FeatureCollection";
}

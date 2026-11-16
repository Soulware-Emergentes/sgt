package lat.soulware.sgt.plots.application.queries.plots.criteria;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * The plots a map shows: those touching a bounding box of longitudes and latitudes on WGS84.
 *
 * @param west  the westernmost longitude, in degrees
 * @param south the southernmost latitude, in degrees
 * @param east  the easternmost longitude, in degrees
 * @param north the northernmost latitude, in degrees
 */
public record PlotsInView(double west, double south, double east, double north) implements QueryCriteria {
}

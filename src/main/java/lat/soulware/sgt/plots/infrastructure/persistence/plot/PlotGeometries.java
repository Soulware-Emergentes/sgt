package lat.soulware.sgt.plots.infrastructure.persistence.plot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.SurveyPoint;
import lat.soulware.sgt.plots.domain.model.valueobjects.UtmZone;
import lat.soulware.sgt.shared.infrastructure.exceptions.InfrastructureException;

/**
 * Converts between a {@link PlotBoundary} and the JTS polygon PostGIS stores. The polygon's SRID is
 * the zone's EPSG code, and its ring is closed, which JTS requires and the boundary leaves open.
 */
final class PlotGeometries {

    private PlotGeometries() {
    }

    static Polygon toPolygon(PlotBoundary boundary) {
        GeometryFactory factory = new GeometryFactory();
        List<SurveyPoint> vertices = boundary.vertices();
        Coordinate[] closedRing = Stream.concat(vertices.stream(), Stream.of(vertices.getFirst()))
            .map(vertex -> new Coordinate(vertex.easting(), vertex.northing()))
            .toArray(Coordinate[]::new);

        Polygon polygon = factory.createPolygon(closedRing);
        polygon.setSRID(boundary.zone().epsgCode());
        return polygon;
    }

    static PlotBoundary toBoundary(Polygon polygon) {
        List<SurveyPoint> vertices = Arrays.stream(polygon.getExteriorRing().getCoordinates())
            .map(coordinate -> new SurveyPoint(coordinate.getX(), coordinate.getY()))
            .toList();

        return new PlotBoundary(zoneOf(polygon.getSRID()), vertices);
    }

    private static UtmZone zoneOf(int srid) {
        return Arrays.stream(UtmZone.values())
            .filter(zone -> zone.epsgCode() == srid)
            .findFirst()
            .orElseThrow(() -> new InfrastructureException(
                "Stored plot has SRID %d, which is no UTM zone".formatted(srid),
                null
            ));
    }
}

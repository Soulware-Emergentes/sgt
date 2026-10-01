package lat.soulware.sgt.plots.infrastructure.geometry.plot;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;
import org.springframework.stereotype.Component;

import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.SharedArea;
import lat.soulware.sgt.plots.domain.model.valueobjects.SurveyPoint;
import lat.soulware.sgt.plots.domain.model.valueobjects.UtmZone;
import lat.soulware.sgt.plots.domain.services.PlotGeometry;

/**
 * Measures shared area with JTS, on the plane of the first boundary's UTM zone. A second boundary
 * surveyed in another zone is reprojected into the first's before the two are intersected, which is
 * accurate at plot scale: plots in different zones only meet along the line between them, where
 * both projections distort the same way.
 */
@Component
public class JtsPlotGeometry implements PlotGeometry {

    private static final GeometryFactory FACTORY = new GeometryFactory();
    private static final CRSFactory CRS_FACTORY = new CRSFactory();
    private static final CoordinateTransformFactory TRANSFORMS = new CoordinateTransformFactory();

    @Override
    public SharedArea sharedArea(PlotBoundary first, PlotBoundary second) {
        Polygon firstPolygon = toPolygon(first.vertices());
        Polygon secondPolygon = toPolygon(inZone(second, first.zone()));
        double squareMeters = firstPolygon.intersection(secondPolygon).getArea();

        return new SharedArea(BigDecimal.valueOf(squareMeters));
    }

    private static List<SurveyPoint> inZone(PlotBoundary boundary, UtmZone target) {
        if (boundary.zone() == target) {
            return boundary.vertices();
        }
        CoordinateTransform transform = TRANSFORMS.createTransform(crsOf(boundary.zone()), crsOf(target));

        return boundary.vertices().stream()
            .map(vertex -> reproject(vertex, transform))
            .toList();
    }

    private static SurveyPoint reproject(SurveyPoint vertex, CoordinateTransform transform) {
        ProjCoordinate reprojected = transform.transform(
            new ProjCoordinate(vertex.easting(), vertex.northing()),
            new ProjCoordinate()
        );
        return new SurveyPoint(reprojected.x, reprojected.y);
    }

    private static CoordinateReferenceSystem crsOf(UtmZone zone) {
        int zoneNumber = switch (zone) {
            case ZONE_17S -> 17;
            case ZONE_18S -> 18;
            case ZONE_19S -> 19;
        };
        String parameters = "+proj=utm +zone=%d +south +datum=WGS84 +units=m +no_defs".formatted(zoneNumber);

        return CRS_FACTORY.createFromParameters(zone.name(), parameters);
    }

    private static Polygon toPolygon(List<SurveyPoint> vertices) {
        Coordinate[] closedRing = Stream.concat(vertices.stream(), Stream.of(vertices.getFirst()))
            .map(vertex -> new Coordinate(vertex.easting(), vertex.northing()))
            .toArray(Coordinate[]::new);

        return FACTORY.createPolygon(closedRing);
    }
}

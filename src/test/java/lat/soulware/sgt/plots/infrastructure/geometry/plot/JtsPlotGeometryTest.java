package lat.soulware.sgt.plots.infrastructure.geometry.plot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.SurveyPoint;
import lat.soulware.sgt.plots.domain.model.valueobjects.UtmZone;

/**
 * Covers the areas JTS reports, in metres, for boundaries in one zone and across two.
 */
class JtsPlotGeometryTest {

    private final JtsPlotGeometry geometry = new JtsPlotGeometry();

    @Test
    void measuresTheAreaTwoBoundariesShare() {
        PlotBoundary first = square(UtmZone.ZONE_18S, 280_000, 8_665_000, 100);
        PlotBoundary halfOver = square(UtmZone.ZONE_18S, 280_050, 8_665_000, 100);

        assertEquals(5_000, this.squareMeters(first, halfOver), 1e-6);
    }

    @Test
    void measuresNothingForBoundariesSharingOnlyAnEdge() {
        PlotBoundary first = square(UtmZone.ZONE_18S, 280_000, 8_665_000, 100);
        PlotBoundary alongsideEast = square(UtmZone.ZONE_18S, 280_100, 8_665_000, 100);

        assertEquals(0, this.squareMeters(first, alongsideEast), 1e-6);
    }

    @Test
    void measuresBoundariesSurveyedInDifferentZones() {
        // Just west of the 78°W line between zones 17S and 18S, surveyed in 18S's grid.
        PlotBoundary in18S = square(UtmZone.ZONE_18S, 170_000, 8_890_000, 100);
        PlotBoundary sameGroundIn17S = reprojected(in18S, 18, UtmZone.ZONE_17S, 17);

        assertEquals(10_000, this.squareMeters(in18S, sameGroundIn17S), 1.0);
    }

    private double squareMeters(PlotBoundary first, PlotBoundary second) {
        return this.geometry.sharedArea(first, second).squareMeters().doubleValue();
    }

    private static PlotBoundary square(UtmZone zone, double easting, double northing, double side) {
        return new PlotBoundary(zone, List.of(
            new SurveyPoint(easting, northing),
            new SurveyPoint(easting + side, northing),
            new SurveyPoint(easting + side, northing + side),
            new SurveyPoint(easting, northing + side)
        ));
    }

    private static PlotBoundary reprojected(PlotBoundary boundary, int fromZone, UtmZone target, int toZone) {
        CRSFactory crs = new CRSFactory();
        String utm = "+proj=utm +zone=%d +south +datum=WGS84 +units=m +no_defs";
        CoordinateTransform transform = new CoordinateTransformFactory().createTransform(
            crs.createFromParameters("from", utm.formatted(fromZone)),
            crs.createFromParameters("to", utm.formatted(toZone))
        );
        List<SurveyPoint> vertices = boundary.vertices().stream()
            .map(vertex -> transform.transform(new ProjCoordinate(vertex.easting(), vertex.northing()), new ProjCoordinate()))
            .map(coordinate -> new SurveyPoint(coordinate.x, coordinate.y))
            .toList();

        return new PlotBoundary(target, vertices);
    }
}

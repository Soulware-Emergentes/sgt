package lat.soulware.sgt.plots.domain.model.valueobjects;

import java.util.HashSet;
import java.util.List;

import lat.soulware.sgt.plots.domain.model.exceptions.MissingUtmZoneException;
import lat.soulware.sgt.plots.domain.model.exceptions.TooFewBoundaryVerticesException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The surveyed perimeter of a plot: its vertices in order, and the UTM zone they were measured in.
 *
 * <p>The ring is held open. A survey that repeats the first vertex at the end to close the ring has
 * that repetition dropped, so the same perimeter compares equal however it was exported.
 *
 * @param zone     the zone every vertex is projected in, never null
 * @param vertices the perimeter in order, at least three distinct points
 */
public record PlotBoundary(UtmZone zone, List<SurveyPoint> vertices) implements ValueObject {

    private static final int MINIMUM_DISTINCT_VERTICES = 3;

    public PlotBoundary {
        if (zone == null) {
            throw new MissingUtmZoneException();
        }
        vertices = openRing(vertices);
        if (new HashSet<>(vertices).size() < MINIMUM_DISTINCT_VERTICES) {
            throw new TooFewBoundaryVerticesException(vertices.size());
        }
    }

    private static List<SurveyPoint> openRing(List<SurveyPoint> vertices) {
        if (vertices == null) {
            return List.of();
        }
        List<SurveyPoint> copied = List.copyOf(vertices);
        boolean isClosed = copied.size() > 1 && copied.getFirst().equals(copied.getLast());

        return isClosed
            ? copied.subList(0, copied.size() - 1)
            : copied;
    }
}

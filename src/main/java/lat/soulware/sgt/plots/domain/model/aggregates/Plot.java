package lat.soulware.sgt.plots.domain.model.aggregates;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lat.soulware.sgt.plots.domain.model.exceptions.MissingPlotBoundaryException;
import lat.soulware.sgt.plots.domain.model.exceptions.OverlappingPlotException;
import lat.soulware.sgt.plots.domain.model.valueobjects.OverlapTolerance;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.plots.domain.model.valueobjects.SharedArea;
import lat.soulware.sgt.plots.domain.services.PlotGeometry;
import lat.soulware.sgt.shared.domain.model.aggregates.AggregateRoot;
import lat.soulware.sgt.shared.domain.model.aggregates.PackagedAs;

/**
 * A surveyed plot of land. No two registered plots overlap by more than {@link #OVERLAP_TOLERANCE}.
 */
@PackagedAs("plot")
public class Plot extends AggregateRoot<PlotId> {

    /**
     * The shared area two plots may have before they count as overlapping. One square metre until
     * the program fixes the figure from the precision of the total stations its surveyors use.
     */
    static final OverlapTolerance OVERLAP_TOLERANCE = new OverlapTolerance(BigDecimal.ONE);

    private final PlotId id;
    private final PlotBoundary boundary;

    private Plot(PlotId id, PlotBoundary boundary) {
        if (boundary == null) {
            throw new MissingPlotBoundaryException();
        }
        this.id = id;
        this.boundary = boundary;
    }

    /**
     * Registers a plot, provided it shares more than {@link #OVERLAP_TOLERANCE} with none of the
     * plots around it. The caller loads the neighbours and saves the result in one transaction
     * isolated enough that no other registration lands in between.
     *
     * @param id         the plot's identity
     * @param boundary   the surveyed perimeter
     * @param neighbours the registered plots intersecting {@code boundary}
     * @param geometry   what measures the area two boundaries share
     * @return the registered plot
     * @throws OverlappingPlotException if the boundary overlaps any neighbour beyond the tolerance
     */
    public static Plot register(
        PlotId id,
        PlotBoundary boundary,
        List<Plot> neighbours,
        PlotGeometry geometry
    ) {
        Plot plot = new Plot(id, boundary);
        Set<PlotId> overlapped = neighbours.stream()
            .filter(neighbour -> plot.overlaps(neighbour, geometry))
            .map(p -> p.getId())
            .collect(Collectors.toSet());

        if (!overlapped.isEmpty()) {
            throw new OverlappingPlotException(overlapped);
        }
        return plot;
    }

    private boolean overlaps(Plot other, PlotGeometry geometry) {
        SharedArea shared = geometry.sharedArea(this.boundary, other.boundary);
        return OVERLAP_TOLERANCE.isExceededBy(shared);
    }

    /**
     * Rebuilds a plot already registered. Skips the overlap check, which held when it was registered.
     *
     * @param id       the plot's identity
     * @param boundary the surveyed perimeter
     * @return the plot as stored
     */
    public static Plot reconstitute(PlotId id, PlotBoundary boundary) {
        return new Plot(id, boundary);
    }

    @Override
    public PlotId getId() {
        return this.id;
    }

    public PlotBoundary getBoundary() {
        return this.boundary;
    }
}

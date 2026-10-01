package lat.soulware.sgt.plots.domain.services;

import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.SharedArea;
import lat.soulware.sgt.shared.domain.services.DomainService;

/**
 * Measures how plot boundaries relate on the ground. Plots may be non-convex, and clipping one
 * non-convex polygon against another robustly is a computational geometry problem the model leaves
 * to a library.
 */
public interface PlotGeometry extends DomainService {

    /**
     * @param first  one boundary, in any UTM zone
     * @param second the other boundary, in any UTM zone, not necessarily the same as {@code first}'s
     * @return the area both boundaries enclose
     */
    SharedArea sharedArea(PlotBoundary first, PlotBoundary second);
}

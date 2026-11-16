package lat.soulware.sgt.plots.application.queries.plots.projections;

import java.util.List;
import java.util.Optional;

import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotById;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotOfOwner;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotsInRegion;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotsInView;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotFootprintResult;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotResult;
import lat.soulware.sgt.shared.application.queries.projections.Projection;

/** The plots the read store holds. */
public interface PlotProjection extends Projection {

    /**
     * @param criteria the plot to read
     * @return the plot, empty when none carries that identity
     */
    Optional<PlotResult> findById(PlotById criteria);

    /**
     * @param criteria the region to list, already checked to be a level of a ubigeo
     * @return the plots whose ubigeo starts with it, ordered by ubigeo and then registration
     */
    List<PlotResult> findInRegion(PlotsInRegion criteria);

    /**
     * @param criteria the beneficiary whose plot to read
     * @return the plot registered to them, empty when there is none
     */
    Optional<PlotResult> findOfOwner(PlotOfOwner criteria);

    /**
     * @param criteria the view, already checked to be a bounding box
     * @param limit    the most plots to answer
     * @return the plots whose footprint touches the view, at most {@code limit} of them
     */
    List<PlotFootprintResult> findInView(PlotsInView criteria, int limit);
}

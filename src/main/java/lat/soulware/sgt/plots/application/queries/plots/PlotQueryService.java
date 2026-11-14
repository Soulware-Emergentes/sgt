package lat.soulware.sgt.plots.application.queries.plots;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotById;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotOfOwner;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotsInRegion;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotsInView;
import lat.soulware.sgt.plots.application.queries.plots.projections.PlotProjection;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotFootprintResult;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotResult;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotsInViewResult;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotsResult;
import lat.soulware.sgt.plots.domain.model.exceptions.BeneficiaryHoldsNoPlotException;
import lat.soulware.sgt.plots.domain.model.exceptions.InvalidBoundingBoxException;
import lat.soulware.sgt.plots.domain.model.exceptions.InvalidRegionException;
import lat.soulware.sgt.plots.domain.model.exceptions.PlotNotFoundException;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.shared.application.queries.QueryService;

/** Answers the reads about plots. */
@Service
@Transactional(readOnly = true)
public class PlotQueryService implements QueryService {

    /** A department, a province or a district: two, four or six digits of a ubigeo. */
    private static final Pattern REGION = Pattern.compile("^\\d{2}(\\d{2}){0,2}$");

    /** The most plots one map view answers with; a view holding more is answered truncated. */
    static final int MAX_PLOTS_IN_VIEW = 1000;

    private final PlotProjection plotProjection;

    public PlotQueryService(PlotProjection plotProjection) {
        this.plotProjection = plotProjection;
    }

    public PlotResult byId(PlotById criteria) {
        return this.plotProjection.findById(criteria)
            .orElseThrow(() -> new PlotNotFoundException(List.of(new PlotId(criteria.id()))));
    }

    public PlotsResult inRegion(PlotsInRegion criteria) {
        boolean isAUbigeoLevel = criteria.region() != null && REGION.matcher(criteria.region()).matches();
        if (!isAUbigeoLevel) {
            throw new InvalidRegionException(criteria.region());
        }

        return new PlotsResult(this.plotProjection.findInRegion(criteria));
    }

    public PlotResult ofOwner(PlotOfOwner criteria) {
        return this.plotProjection.findOfOwner(criteria)
            .orElseThrow(() -> new BeneficiaryHoldsNoPlotException(criteria.documentType(), criteria.documentNumber()));
    }

    public PlotsInViewResult inView(PlotsInView criteria) {
        boolean isABoundingBox = isLongitude(criteria.west()) && isLongitude(criteria.east())
            && isLatitude(criteria.south()) && isLatitude(criteria.north())
            && criteria.west() < criteria.east() && criteria.south() < criteria.north();
        if (!isABoundingBox) {
            throw new InvalidBoundingBoxException("%s,%s,%s,%s".formatted(
                criteria.west(), criteria.south(), criteria.east(), criteria.north()
            ));
        }

        // One more than the most answered tells whether the view holds more.
        List<PlotFootprintResult> plots = this.plotProjection.findInView(criteria, MAX_PLOTS_IN_VIEW + 1);
        boolean truncated = plots.size() > MAX_PLOTS_IN_VIEW;

        return new PlotsInViewResult(truncated ? plots.subList(0, MAX_PLOTS_IN_VIEW) : plots, truncated);
    }

    private static boolean isLongitude(double degrees) {
        return degrees >= -180 && degrees <= 180;
    }

    private static boolean isLatitude(double degrees) {
        return degrees >= -90 && degrees <= 90;
    }
}

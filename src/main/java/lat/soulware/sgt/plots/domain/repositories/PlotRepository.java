package lat.soulware.sgt.plots.domain.repositories;

import java.util.List;
import java.util.Optional;

import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.plots.domain.model.aggregates.Plot;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.shared.domain.repositories.DomainRepository;

/** Stores {@link Plot} aggregates. */
public interface PlotRepository extends DomainRepository<Plot, PlotId> {

    /**
     * Loads every stored plot touching or overlapping {@code boundary}. Any plot sharing area with
     * the boundary is among them, which is what makes this the complete set of plots a new
     * boundary could conflict with.
     *
     * @param boundary the boundary to look around, in any UTM zone
     * @return the plots intersecting it, empty when none do
     */
    List<Plot> findIntersecting(PlotBoundary boundary);

    /**
     * @param owner the legal document of a beneficiary
     * @return the plot registered to them, empty when they hold none
     */
    Optional<Plot> findOwnedBy(BeneficiaryLegalDocument owner);
}

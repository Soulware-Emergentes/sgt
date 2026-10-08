package lat.soulware.sgt.plots.domain.model.aggregates;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.plots.domain.model.exceptions.BeneficiaryAlreadyHoldsPlotException;
import lat.soulware.sgt.plots.domain.model.exceptions.MissingPlotBoundaryException;
import lat.soulware.sgt.plots.domain.model.exceptions.MissingPlotOwnerException;
import lat.soulware.sgt.plots.domain.model.exceptions.MissingPlotUbigeoException;
import lat.soulware.sgt.plots.domain.model.exceptions.MissingRegistrationDateException;
import lat.soulware.sgt.plots.domain.model.exceptions.OverlappingPlotException;
import lat.soulware.sgt.plots.domain.model.valueobjects.OverlapTolerance;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.plots.domain.model.valueobjects.SharedArea;
import lat.soulware.sgt.plots.domain.model.valueobjects.Ubigeo;
import lat.soulware.sgt.plots.domain.services.PlotGeometry;
import lat.soulware.sgt.shared.domain.model.aggregates.AggregateRoot;
import lat.soulware.sgt.shared.domain.model.aggregates.PackagedAs;

/**
 * A surveyed plot of land, registered to the beneficiary whose legal document it names. A
 * beneficiary holds a single plot, and no two registered plots overlap by more than
 * {@link #OVERLAP_TOLERANCE}.
 */
@PackagedAs("plot")
public class Plot extends AggregateRoot<PlotId> {

    /**
     * The shared area two plots may have before they count as overlapping. One square metre until
     * the program fixes the figure from the precision of the total stations its surveyors use.
     */
    static final OverlapTolerance OVERLAP_TOLERANCE = new OverlapTolerance(BigDecimal.ONE);

    private final PlotId id;
    private final BeneficiaryLegalDocument owner;
    private final PlotBoundary boundary;
    private final Ubigeo ubigeo;
    private final LocalDateTime registrationDate;

    private Plot(
        PlotId id,
        BeneficiaryLegalDocument owner,
        PlotBoundary boundary,
        Ubigeo ubigeo,
        LocalDateTime registrationDate
    ) {
        if (owner == null) {
            throw new MissingPlotOwnerException();
        }
        if (boundary == null) {
            throw new MissingPlotBoundaryException();
        }
        if (ubigeo == null) {
            throw new MissingPlotUbigeoException();
        }
        if (registrationDate == null) {
            throw new MissingRegistrationDateException();
        }
        this.id = id;
        this.owner = owner;
        this.boundary = boundary;
        this.ubigeo = ubigeo;
        this.registrationDate = registrationDate;
    }

    /**
     * Registers a plot, provided its owner holds none yet and it shares more than
     * {@link #OVERLAP_TOLERANCE} with none of the plots around it. The caller loads the owner's plot
     * and the neighbours, and saves the result in one transaction isolated enough that no other
     * registration lands in between.
     *
     * @param id               the plot's identity
     * @param owner            the legal document of the beneficiary it is registered to
     * @param boundary         the surveyed perimeter
     * @param ubigeo           the district it lies in
     * @param registrationDate when it is registered
     * @param ownersPlot       the plot already registered to {@code owner}, empty when there is none
     * @param neighbours       the registered plots intersecting {@code boundary}
     * @param geometry         what measures the area two boundaries share
     * @return the registered plot
     * @throws BeneficiaryAlreadyHoldsPlotException if the owner already holds a plot
     * @throws OverlappingPlotException if the boundary overlaps any neighbour beyond the tolerance
     */
    public static Plot register(
        PlotId id,
        BeneficiaryLegalDocument owner,
        PlotBoundary boundary,
        Ubigeo ubigeo,
        LocalDateTime registrationDate,
        Optional<Plot> ownersPlot,
        List<Plot> neighbours,
        PlotGeometry geometry
    ) {
        Plot plot = new Plot(id, owner, boundary, ubigeo, registrationDate);
        ownersPlot.ifPresent(held -> {
            throw new BeneficiaryAlreadyHoldsPlotException(owner, held.getId());
        });

        Set<PlotId> overlapping = neighbours.stream()
            .filter(neighbour -> plot.overlaps(neighbour, geometry))
            .map(p -> p.getId())
            .collect(Collectors.toSet());

        if (!overlapping.isEmpty()) {
            throw new OverlappingPlotException(overlapping);
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
     * @param id               the plot's identity
     * @param owner            the legal document of the beneficiary it is registered to
     * @param boundary         the surveyed perimeter
     * @param ubigeo           the district it lies in
     * @param registrationDate when it was registered
     * @return the plot as stored
     */
    public static Plot reconstitute(
        PlotId id,
        BeneficiaryLegalDocument owner,
        PlotBoundary boundary,
        Ubigeo ubigeo,
        LocalDateTime registrationDate
    ) {
        return new Plot(id, owner, boundary, ubigeo, registrationDate);
    }

    @Override
    public PlotId getId() {
        return this.id;
    }

    public BeneficiaryLegalDocument getOwner() {
        return this.owner;
    }

    public PlotBoundary getBoundary() {
        return this.boundary;
    }

    public Ubigeo getUbigeo() {
        return this.ubigeo;
    }

    public LocalDateTime getRegistrationDate() {
        return this.registrationDate;
    }
}

package lat.soulware.sgt.plots.infrastructure.persistence.plot;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.locationtech.jts.geom.Polygon;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.plots.domain.model.aggregates.Plot;
import lat.soulware.sgt.plots.domain.model.exceptions.PlotNotFoundException;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.plots.domain.model.valueobjects.Ubigeo;
import lat.soulware.sgt.plots.domain.repositories.PlotRepository;
import lat.soulware.sgt.plots.infrastructure.persistence.plot.jpa.PlotEntity;
import lat.soulware.sgt.plots.infrastructure.persistence.plot.jpa.PlotJpaRepository;
import lat.soulware.sgt.shared.domain.model.exceptions.EntityNotFoundException;
import lat.soulware.sgt.shared.infrastructure.persistence.adapter.JpaRepositoryToDomainRepositoryAdapter;

/** Stores plots in the {@code plots} table. */
@Repository
public class JpaPlotRepositoryAdapter extends JpaRepositoryToDomainRepositoryAdapter<Plot, PlotId, PlotEntity, UUID>
    implements PlotRepository {

    private final PlotJpaRepository delegate;
    private final ApplicationEventPublisher publisher;

    public JpaPlotRepositoryAdapter(PlotJpaRepository delegate, ApplicationEventPublisher publisher) {
        this.delegate = delegate;
        this.publisher = publisher;
    }

    @Override
    public List<Plot> findIntersecting(PlotBoundary boundary) {
        Polygon polygon = PlotGeometries.toPolygon(boundary);

        return this.delegate.findIntersecting(polygon.toText(), polygon.getSRID()).stream()
            .map(this::toAggregate)
            .toList();
    }

    @Override
    public Optional<Plot> findOwnedBy(BeneficiaryLegalDocument owner) {
        return this.delegate.findByOwnerDocumentTypeAndOwnerDocumentNumber(owner.type().name(), owner.number())
            .map(this::toAggregate);
    }

    @Override
    protected PlotJpaRepository delegate() {
        return this.delegate;
    }

    @Override
    protected ApplicationEventPublisher publisher() {
        return this.publisher;
    }

    @Override
    protected PlotEntity toEntity(Plot aggregate) {
        BeneficiaryLegalDocument owner = aggregate.getOwner();

        return new PlotEntity(
            aggregate.getId().value(),
            owner.type().name(),
            owner.number(),
            PlotGeometries.toPolygon(aggregate.getBoundary()),
            aggregate.getUbigeo().value(),
            aggregate.getRegistrationDate()
        );
    }

    @Override
    protected Plot toAggregate(PlotEntity entity) {
        BeneficiaryLegalDocument owner = new BeneficiaryLegalDocument(
            LegalDocumentType.valueOf(entity.getOwnerDocumentType()),
            entity.getOwnerDocumentNumber()
        );

        return Plot.reconstitute(
            new PlotId(entity.getId()),
            owner,
            PlotGeometries.toBoundary(entity.getBoundary()),
            new Ubigeo(entity.getUbigeo()),
            entity.getRegistrationDate()
        );
    }

    @Override
    protected UUID keyOf(PlotEntity entity) {
        return entity.getId();
    }

    @Override
    protected EntityNotFoundException notFound(Collection<PlotId> missing) {
        return new PlotNotFoundException(missing);
    }
}

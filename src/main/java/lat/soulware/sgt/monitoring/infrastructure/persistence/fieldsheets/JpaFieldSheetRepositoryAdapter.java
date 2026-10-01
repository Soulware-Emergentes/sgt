package lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNotFoundException;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetScan;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetStatus;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.MeasurementUnit;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.Quantity;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.Supply;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SupplyName;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets.jpa.FieldSheetEntity;
import lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets.jpa.FieldSheetJpaRepository;
import lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets.jpa.SupplyRow;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentId;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantId;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.shared.domain.model.exceptions.EntityNotFoundException;
import lat.soulware.sgt.shared.infrastructure.persistence.adapter.JpaRepositoryToDomainRepositoryAdapter;

/** Stores field sheets in {@code field_sheets}, their supplies in {@code field_sheet_supplies}. */
@Repository
public class JpaFieldSheetRepositoryAdapter
    extends JpaRepositoryToDomainRepositoryAdapter<FieldSheet, FieldSheetId, FieldSheetEntity, UUID>
    implements FieldSheetRepository {

    private final FieldSheetJpaRepository delegate;
    private final ApplicationEventPublisher publisher;

    public JpaFieldSheetRepositoryAdapter(FieldSheetJpaRepository delegate, ApplicationEventPublisher publisher) {
        this.delegate = delegate;
        this.publisher = publisher;
    }

    @Override
    protected FieldSheetJpaRepository delegate() {
        return this.delegate;
    }

    @Override
    protected ApplicationEventPublisher publisher() {
        return this.publisher;
    }

    @Override
    protected FieldSheetEntity toEntity(FieldSheet aggregate) {
        boolean isReviewed = aggregate.getReviewedBy() != null;
        UUID reviewedBy = isReviewed
            ? aggregate.getReviewedBy().value()
            : null;
        BeneficiaryLegalDocument beneficiary = aggregate.getBeneficiaryLegalDocument();
        boolean namesABeneficiary = beneficiary != null;
        String beneficiaryDocumentType = namesABeneficiary
            ? beneficiary.type().name()
            : null;
        String beneficiaryDocumentNumber = namesABeneficiary
            ? beneficiary.number()
            : null;
        List<SupplyRow> supplies = aggregate.getUsedSupplies().stream()
            .map(supply -> new SupplyRow(
                supply.name().value(),
                supply.quantity().value(),
                supply.quantity().unit().name()
            ))
            .toList();

        return new FieldSheetEntity(
            aggregate.getId().value(),
            aggregate.getTaskCode().value(),
            beneficiaryDocumentType,
            beneficiaryDocumentNumber,
            aggregate.getEvaluationDate(),
            aggregate.getSecurementDate(),
            aggregate.getScan().key().value(),
            aggregate.getScan().sizeInBytes(),
            aggregate.getSubmittedBy().value(),
            aggregate.getSubmissionDate(),
            aggregate.getStatus().name(),
            reviewedBy,
            aggregate.getReviewDate(),
            supplies
        );
    }

    @Override
    protected FieldSheet toAggregate(FieldSheetEntity entity) {
        boolean isReviewed = entity.getReviewedBy() != null;
        GeneralManagementAssistantId reviewedBy = isReviewed
            ? new GeneralManagementAssistantId(entity.getReviewedBy())
            : null;
        boolean namesABeneficiary = entity.getBeneficiaryDocumentType() != null;
        BeneficiaryLegalDocument beneficiary = namesABeneficiary
            ? new BeneficiaryLegalDocument(
                LegalDocumentType.valueOf(entity.getBeneficiaryDocumentType()),
                entity.getBeneficiaryDocumentNumber()
            )
            : null;
        List<Supply> supplies = entity.getSupplies().stream()
            .map(row -> new Supply(
                new SupplyName(row.getName()),
                new Quantity(row.getAmount(), MeasurementUnit.valueOf(row.getUnit()))
            ))
            .toList();

        return FieldSheet.reconstitute(
            new FieldSheetId(entity.getId()),
            new TaskCode(entity.getTaskCode()),
            FieldSheetStatus.valueOf(entity.getStatus()),
            beneficiary,
            supplies,
            entity.getEvaluationDate(),
            entity.getSecurementDate(),
            new FieldSheetScan(new ScanKey(entity.getScanKey()), entity.getScanSize()),
            new FieldAgentId(entity.getSubmittedBy()),
            entity.getSubmissionDate(),
            reviewedBy,
            entity.getReviewDate()
        );
    }

    @Override
    protected UUID keyOf(FieldSheetEntity entity) {
        return entity.getId();
    }

    @Override
    protected EntityNotFoundException notFound(Collection<FieldSheetId> missing) {
        return new FieldSheetNotFoundException(missing);
    }
}

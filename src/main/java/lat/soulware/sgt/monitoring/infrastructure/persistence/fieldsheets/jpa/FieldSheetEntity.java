package lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

/** The row storing a field sheet, with its supplies in order. */
@Entity
@Table(name = "field_sheets")
public class FieldSheetEntity {

    @Id
    private UUID id;

    @Column(name = "task_code", nullable = false)
    private String taskCode;

    @Column(name = "beneficiary_document_type")
    private String beneficiaryDocumentType;

    @Column(name = "beneficiary_document_number")
    private String beneficiaryDocumentNumber;

    @Column(name = "evaluation_date")
    private LocalDateTime evaluationDate;

    @Column(name = "securement_date")
    private LocalDateTime securementDate;

    @Column(name = "scan_key", nullable = false)
    private String scanKey;

    @Column(name = "scan_size", nullable = false)
    private long scanSize;

    @Column(name = "submitted_by", nullable = false)
    private UUID submittedBy;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @Column(nullable = false)
    private String status;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "field_sheet_supplies", joinColumns = @JoinColumn(name = "field_sheet_id"))
    @OrderColumn(name = "position")
    private List<SupplyRow> supplies = new ArrayList<>();

    /** For JPA. */
    protected FieldSheetEntity() {
    }

    public FieldSheetEntity(
        UUID id,
        String taskCode,
        String beneficiaryDocumentType,
        String beneficiaryDocumentNumber,
        LocalDateTime evaluationDate,
        LocalDateTime securementDate,
        String scanKey,
        long scanSize,
        UUID submittedBy,
        LocalDateTime submissionDate,
        String status,
        UUID reviewedBy,
        LocalDateTime reviewDate,
        List<SupplyRow> supplies
    ) {
        this.id = id;
        this.taskCode = taskCode;
        this.beneficiaryDocumentType = beneficiaryDocumentType;
        this.beneficiaryDocumentNumber = beneficiaryDocumentNumber;
        this.evaluationDate = evaluationDate;
        this.securementDate = securementDate;
        this.scanKey = scanKey;
        this.scanSize = scanSize;
        this.submittedBy = submittedBy;
        this.submissionDate = submissionDate;
        this.status = status;
        this.reviewedBy = reviewedBy;
        this.reviewDate = reviewDate;
        this.supplies = new ArrayList<>(supplies);
    }

    public UUID getId() {
        return this.id;
    }

    public String getTaskCode() {
        return this.taskCode;
    }

    public String getBeneficiaryDocumentType() {
        return this.beneficiaryDocumentType;
    }

    public String getBeneficiaryDocumentNumber() {
        return this.beneficiaryDocumentNumber;
    }

    public LocalDateTime getEvaluationDate() {
        return this.evaluationDate;
    }

    public LocalDateTime getSecurementDate() {
        return this.securementDate;
    }

    public String getScanKey() {
        return this.scanKey;
    }

    public long getScanSize() {
        return this.scanSize;
    }

    public UUID getSubmittedBy() {
        return this.submittedBy;
    }

    public LocalDateTime getSubmissionDate() {
        return this.submissionDate;
    }

    public String getStatus() {
        return this.status;
    }

    public UUID getReviewedBy() {
        return this.reviewedBy;
    }

    public LocalDateTime getReviewDate() {
        return this.reviewDate;
    }

    public List<SupplyRow> getSupplies() {
        return this.supplies;
    }
}

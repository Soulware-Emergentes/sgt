package lat.soulware.sgt.monitoring.domain.model.aggregates;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import lat.soulware.sgt.monitoring.domain.model.events.FieldSheetRejected;
import lat.soulware.sgt.monitoring.domain.model.events.FieldSheetSubmitted;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetAlreadyReviewedException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNotDeliveredException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNotSubmittedException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNumbersExhaustedException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingBeneficiaryException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingEvaluationDateException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingFieldSheetCodeException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingReviewDateException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingReviewerException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingScanException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingSubmissionDateException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingSubmitterException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingTaskCodeException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.ReviewBeforeSubmissionException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.ReviewerIsSubmitterException;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.EvaluationDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetCode;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetScan;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetStatus;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ReviewDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SecurementDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SubmissionDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SubmissionYear;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.Supply;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetApproved;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentId;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantId;
import lat.soulware.sgt.shared.domain.model.aggregates.AggregateRoot;
import lat.soulware.sgt.shared.domain.model.aggregates.PackagedAs;

/**
 * A field agent's evaluation of the agricultural inputs a beneficiary has used, and how much of
 * each, and the review a general management assistant gives it.
 *
 * <p>A field sheet is submitted as a scanned PDF for one poi task, the work it delivers. The PDF is
 * the evidence and never changes. What it says (the beneficiary, the supplies and the dates) is read
 * by the reviewer and stored only on approval. Until then the reviewer may relink the field sheet to
 * the task its evidence belongs to.
 *
 * <p>It records each step of its
 * lifecycle itself: who submitted it and when, how poi answered its delivery, and once reviewed,
 * who reviewed it, when, and with what outcome. poi's answer moves it from
 * {@link FieldSheetStatus#SUBMITTED} to {@link FieldSheetStatus#DELIVERED} or
 * {@link FieldSheetStatus#VOIDED}. Only {@link #approve} and {@link #reject} move it on from there,
 * and only from {@link FieldSheetStatus#DELIVERED}.
 */
@PackagedAs("fieldsheets")
public class FieldSheet extends AggregateRoot<FieldSheetId> {
    private final FieldSheetId id;
    private final FieldSheetCode code;
    private TaskCode taskCode;
    private FieldSheetStatus status;

    private BeneficiaryLegalDocument beneficiaryLegalDocument;
    private List<Supply> usedSupplies;
    private EvaluationDate evaluationDate;
    private SecurementDate securementDate;

    private final FieldSheetScan scan;
    private final FieldAgentId submittedBy;
    private final SubmissionDate submissionDate;

    private GeneralManagementAssistantId reviewedBy;
    private ReviewDate reviewDate;

    private FieldSheet(
        FieldSheetId id,
        FieldSheetCode code,
        TaskCode taskCode,
        FieldSheetStatus status,
        BeneficiaryLegalDocument beneficiaryLegalDocument,
        List<Supply> usedSupplies,
        EvaluationDate evaluationDate,
        SecurementDate securementDate,
        FieldSheetScan scan,
        FieldAgentId submittedBy,
        SubmissionDate submissionDate,
        GeneralManagementAssistantId reviewedBy,
        ReviewDate reviewDate
    ) {
        if (code == null) {
            throw new MissingFieldSheetCodeException();
        }
        if (taskCode == null) {
            throw new MissingTaskCodeException();
        }
        if (scan == null) {
            throw new MissingScanException();
        }
        if (status == FieldSheetStatus.APPROVED) {
            requireConfirmedData(beneficiaryLegalDocument, evaluationDate);
        }
        if (submittedBy == null) {
            throw new MissingSubmitterException();
        }
        if (submissionDate == null) {
            throw new MissingSubmissionDateException();
        }
        this.id = id;
        this.code = code;
        this.taskCode = taskCode;
        this.status = status;
        this.beneficiaryLegalDocument = beneficiaryLegalDocument;
        this.usedSupplies = List.copyOf(usedSupplies);
        this.evaluationDate = evaluationDate;
        this.securementDate = securementDate;
        this.scan = scan;
        this.submittedBy = submittedBy;
        this.submissionDate = submissionDate;
        this.reviewedBy = reviewedBy;
        this.reviewDate = reviewDate;
    }

    /**
     * @param id             the field sheet's identity
     * @param taskCode       the poi task the field sheet is submitted for
     * @param scan           the scanned PDF, already uploaded
     * @param submittedBy    the field agent submitting the field sheet
     * @param submissionDate when the field agent submitted the field sheet
     * @param lastOfYear     the field sheet numbered last in the submission's year, empty when this
     *                       is the year's first
     * @return the submitted field sheet, awaiting review, numbered right after the year's last and
     *         having announced its submission so its delivery reaches poi
     * @throws FieldSheetNumbersExhaustedException if the year already numbered its 9999th
     */
    public static FieldSheet submit(
        FieldSheetId id,
        TaskCode taskCode,
        FieldSheetScan scan,
        FieldAgentId submittedBy,
        SubmissionDate submissionDate,
        Optional<FieldSheet> lastOfYear
    ) {
        if (submissionDate == null) {
            throw new MissingSubmissionDateException();
        }
        FieldSheetCode code = lastOfYear.map(last -> last.getCode().next())
            .orElse(FieldSheetCode.first(SubmissionYear.of(submissionDate.value())));

        FieldSheet fieldSheet = new FieldSheet(
            id,
            code,
            taskCode,
            FieldSheetStatus.SUBMITTED,
            null,
            List.of(),
            null,
            null,
            scan,
            submittedBy,
            submissionDate,
            null,
            null
        );
        fieldSheet.registerEvent(new FieldSheetSubmitted(id, Instant.now()));
        return fieldSheet;
    }

    /**
     * Rebuilds a field sheet already stored, in whatever step of its lifecycle it reached.
     *
     * @param id                       the field sheet's identity
     * @param taskCode                 the poi task the field sheet is submitted for
     * @param status                   where the field sheet stands in its review
     * @param beneficiaryLegalDocument the legal document of the evaluated beneficiary, null until approved
     * @param usedSupplies             the inputs the beneficiary used, empty until approved
     * @param evaluationDate           when the field agent conducted the evaluation, null until approved
     * @param securementDate           the field sheet's securement date, null until approved
     * @param scan                     the scanned PDF
     * @param submittedBy              the field agent who submitted the field sheet
     * @param submissionDate           when the field sheet was submitted
     * @param reviewedBy               who reviewed the field sheet, null until it is reviewed
     * @param reviewDate               when the field sheet was reviewed, null until it is reviewed
     * @return the field sheet as stored
     */
    public static FieldSheet reconstitute(
        FieldSheetId id,
        FieldSheetCode code,
        TaskCode taskCode,
        FieldSheetStatus status,
        BeneficiaryLegalDocument beneficiaryLegalDocument,
        List<Supply> usedSupplies,
        EvaluationDate evaluationDate,
        SecurementDate securementDate,
        FieldSheetScan scan,
        FieldAgentId submittedBy,
        SubmissionDate submissionDate,
        GeneralManagementAssistantId reviewedBy,
        ReviewDate reviewDate
    ) {
        return new FieldSheet(
            id,
            code,
            taskCode,
            status,
            beneficiaryLegalDocument,
            usedSupplies,
            evaluationDate,
            securementDate,
            scan,
            submittedBy,
            submissionDate,
            reviewedBy,
            reviewDate
        );
    }

    /**
     * Accepts the evaluation as correct, storing what the reviewer confirmed the PDF says.
     *
     * @param reviewer                 the general management assistant reviewing the field sheet
     * @param reviewDate               when the review took place, not before the submission
     * @param beneficiaryLegalDocument the legal document of the evaluated beneficiary
     * @param usedSupplies             the inputs the beneficiary used
     * @param evaluationDate           when the field agent conducted the evaluation
     * @param securementDate           the field sheet's securement date, if it states one
     * @throws MissingBeneficiaryException        if no beneficiary is given
     * @throws MissingEvaluationDateException     if no evaluation date is given
     * @throws FieldSheetAlreadyReviewedException if the field sheet was already approved or rejected
     * @throws FieldSheetNotDeliveredException    if poi has not accepted the field sheet's delivery
     * @throws ReviewBeforeSubmissionException    if {@code reviewDate} precedes the submission
     * @throws ReviewerIsSubmitterException      if the reviewer is the field agent who submitted it
     */
    public void approve(
        GeneralManagementAssistantId reviewer,
        ReviewDate reviewDate,
        BeneficiaryLegalDocument beneficiaryLegalDocument,
        List<Supply> usedSupplies,
        EvaluationDate evaluationDate,
        SecurementDate securementDate
    ) {
        requireConfirmedData(beneficiaryLegalDocument, evaluationDate);
        this.review(reviewer, reviewDate, FieldSheetStatus.APPROVED);
        this.beneficiaryLegalDocument = beneficiaryLegalDocument;
        this.usedSupplies = List.copyOf(usedSupplies);
        this.evaluationDate = evaluationDate;
        this.securementDate = securementDate;
        this.registerEvent(new FieldSheetApproved(this.id, Instant.now()));
    }

    /**
     * Turns the evaluation down, which hands the task back to poi for another delivery. The field
     * sheet stays rejected; a corrected evaluation is a new field sheet.
     *
     * @param reviewer   the general management assistant reviewing the field sheet
     * @param reviewDate when the review took place, not before the submission
     * @throws FieldSheetAlreadyReviewedException if the field sheet was already approved or rejected
     * @throws FieldSheetNotDeliveredException    if poi has not accepted the field sheet's delivery
     * @throws ReviewBeforeSubmissionException    if {@code reviewDate} precedes the submission
     * @throws ReviewerIsSubmitterException      if the reviewer is the field agent who submitted it
     */
    public void reject(GeneralManagementAssistantId reviewer, ReviewDate reviewDate) {
        this.review(reviewer, reviewDate, FieldSheetStatus.REJECTED);
        this.registerEvent(new FieldSheetRejected(this.id, Instant.now()));
    }

    /**
     * Records that poi accepted the delivery, which opens the field sheet to review. A field sheet
     * already delivered is left as it is, since the acceptance may be recorded again.
     *
     * @throws FieldSheetNotSubmittedException if poi's answer was already recorded otherwise
     */
    public void recordDeliveryAccepted() {
        this.recordDeliveryOutcome(FieldSheetStatus.DELIVERED);
    }

    /**
     * Records that poi refused the delivery, because the task it names does not exist or already has
     * one, which voids the submission. A voided field sheet is never reviewed. One already voided is
     * left as it is, since the refusal may be recorded again.
     *
     * @throws FieldSheetNotSubmittedException if poi's answer was already recorded otherwise
     */
    public void recordDeliveryRefused() {
        this.recordDeliveryOutcome(FieldSheetStatus.VOIDED);
    }

    private void recordDeliveryOutcome(FieldSheetStatus outcome) {
        if (this.status == outcome) {
            return;
        }
        if (this.status != FieldSheetStatus.SUBMITTED) {
            throw new FieldSheetNotSubmittedException(this.id, this.status);
        }
        this.status = outcome;
    }

    /**
     * Relinks the field sheet to the task its evidence belongs to, once poi moved the delivery there.
     * Relinking to the task it already names changes nothing.
     *
     * @param taskCode the task the field sheet's evidence belongs to
     * @throws FieldSheetAlreadyReviewedException if the field sheet was already approved or rejected
     * @throws FieldSheetNotDeliveredException    if poi has not accepted the field sheet's delivery
     */
    public void relinkTo(TaskCode taskCode) {
        if (taskCode == null) {
            throw new MissingTaskCodeException();
        }
        this.requireDelivered();
        this.taskCode = taskCode;
    }

    private static void requireConfirmedData(
        BeneficiaryLegalDocument beneficiaryLegalDocument,
        EvaluationDate evaluationDate
    ) {
        if (beneficiaryLegalDocument == null) {
            throw new MissingBeneficiaryException();
        }
        if (evaluationDate == null) {
            throw new MissingEvaluationDateException();
        }
    }

    private void requireDelivered() {
        boolean isReviewed = this.status == FieldSheetStatus.APPROVED
            || this.status == FieldSheetStatus.REJECTED;
        if (isReviewed) {
            throw new FieldSheetAlreadyReviewedException(this.id, this.status);
        }
        if (this.status != FieldSheetStatus.DELIVERED) {
            throw new FieldSheetNotDeliveredException(this.id, this.status);
        }
    }

    private void review(GeneralManagementAssistantId reviewer, ReviewDate reviewDate, FieldSheetStatus outcome) {
        this.requireDelivered();
        if (reviewer == null) {
            throw new MissingReviewerException();
        }
        if (reviewDate == null) {
            throw new MissingReviewDateException();
        }
        if (reviewDate.value().isBefore(this.submissionDate.value())) {
            throw new ReviewBeforeSubmissionException(reviewDate.value(), this.submissionDate.value());
        }
        boolean reviewsOwnSubmission = reviewer.value().equals(this.submittedBy.value());
        if (reviewsOwnSubmission) {
            throw new ReviewerIsSubmitterException(this.id);
        }
        this.status = outcome;
        this.reviewedBy = reviewer;
        this.reviewDate = reviewDate;
    }

    @Override
    public FieldSheetId getId() {
        return this.id;
    }

    public FieldSheetCode getCode() {
        return this.code;
    }

    public TaskCode getTaskCode() {
        return this.taskCode;
    }

    /** @return the evaluated beneficiary's legal document, null until approved */
    public BeneficiaryLegalDocument getBeneficiaryLegalDocument() {
        return this.beneficiaryLegalDocument;
    }

    /** @return the inputs the beneficiary used, unmodifiable, empty until approved */
    public List<Supply> getUsedSupplies() {
        return this.usedSupplies;
    }

    /** @return when the evaluation took place, null until approved */
    public EvaluationDate getEvaluationDate() {
        return this.evaluationDate;
    }

    /** @return the securement date, null until approved or when the PDF states none */
    public SecurementDate getSecurementDate() {
        return this.securementDate;
    }

    public FieldSheetScan getScan() {
        return this.scan;
    }

    public FieldAgentId getSubmittedBy() {
        return this.submittedBy;
    }

    public SubmissionDate getSubmissionDate() {
        return this.submissionDate;
    }

    public FieldSheetStatus getStatus() {
        return this.status;
    }

    /** @return who reviewed the field sheet, null until it is reviewed */
    public GeneralManagementAssistantId getReviewedBy() {
        return this.reviewedBy;
    }

    /** @return when the field sheet was reviewed, null until it is reviewed */
    public ReviewDate getReviewDate() {
        return this.reviewDate;
    }
}

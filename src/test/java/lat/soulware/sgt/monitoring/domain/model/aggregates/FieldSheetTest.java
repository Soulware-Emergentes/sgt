package lat.soulware.sgt.monitoring.domain.model.aggregates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import lat.soulware.sgt.monitoring.domain.model.events.FieldSheetRejected;
import lat.soulware.sgt.monitoring.domain.model.events.FieldSheetSubmitted;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetAlreadyReviewedException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNotDeliveredException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNumbersExhaustedException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.ReviewerIsSubmitterException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNotSubmittedException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.InvalidTaskCodeException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingBeneficiaryException;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetCode;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetScan;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetStatus;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SubmissionYear;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetApproved;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentId;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantId;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.shared.domain.model.events.DomainEvent;

/**
 * Covers what a field sheet announces at each step, since each announcement moves its poi task on:
 * the submission delivers it, an approval completes it, and a rejection puts it back. Only a field
 * sheet whose delivery poi accepted is reviewed, and one poi refused is voided.
 */
class FieldSheetTest {

    private static final LocalDateTime SUBMITTED_ON = LocalDateTime.of(2026, 9, 1, 10, 0);
    private static final BeneficiaryLegalDocument BENEFICIARY = new BeneficiaryLegalDocument(LegalDocumentType.DNI, "12345678");
    private static final TaskCode OTHER_TASK = new TaskCode("2025-2030-01-03-0033");
    private static final GeneralManagementAssistantId REVIEWER = new GeneralManagementAssistantId(UUID.randomUUID());

    @Test
    void submittingAnnouncesTheSubmittedFieldSheet() {
        FieldSheet fieldSheet = submitted();

        List<DomainEvent> events = fieldSheet.pullDomainEvents();
        assertEquals(1, events.size());
        FieldSheetSubmitted submitted = assertInstanceOf(FieldSheetSubmitted.class, events.getFirst());
        assertEquals(fieldSheet.getId(), submitted.fieldSheetId());
    }

    @Test
    void theYearsFirstFieldSheetIsNumberedOne() {
        assertEquals("FI-0001-2026", submitted().getCode().value());
    }

    @Test
    void aFieldSheetIsNumberedAfterTheYearsLast() {
        FieldSheet last = reconstitutedAs(new FieldSheetCode(41, new SubmissionYear(2026)));

        assertEquals("FI-0042-2026", submittedOn(SUBMITTED_ON, Optional.of(last)).getCode().value());
    }

    @Test
    void aYearPastItsLastNumberTakesNoMoreFieldSheets() {
        FieldSheet last = reconstitutedAs(new FieldSheetCode(9999, new SubmissionYear(2026)));

        assertThrows(
            FieldSheetNumbersExhaustedException.class,
            () -> submittedOn(SUBMITTED_ON, Optional.of(last))
        );
    }

    @Test
    void approvingAnnouncesTheApprovedFieldSheet() {
        FieldSheet fieldSheet = reviewable();

        fieldSheet.approve(REVIEWER, SUBMITTED_ON.plusDays(1), BENEFICIARY, List.of(), SUBMITTED_ON.minusDays(1), null);

        List<DomainEvent> events = fieldSheet.pullDomainEvents();
        assertEquals(1, events.size());
        FieldSheetApproved approved = assertInstanceOf(FieldSheetApproved.class, events.getFirst());
        assertEquals(fieldSheet.getId(), approved.fieldSheetId());
    }

    @Test
    void rejectingAnnouncesTheRejectedFieldSheet() {
        FieldSheet fieldSheet = reviewable();

        fieldSheet.reject(REVIEWER, SUBMITTED_ON.plusDays(1));

        List<DomainEvent> events = fieldSheet.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(FieldSheetRejected.class, events.getFirst());
    }

    @Test
    void aFieldSheetIsReviewedOnlyOncePoiAcceptsItsDelivery() {
        FieldSheet fieldSheet = submitted();

        assertThrows(FieldSheetNotDeliveredException.class, () -> fieldSheet.approve(REVIEWER, SUBMITTED_ON.plusDays(1), BENEFICIARY, List.of(), SUBMITTED_ON.minusDays(1), null));
        assertThrows(FieldSheetNotDeliveredException.class, () -> fieldSheet.reject(REVIEWER, SUBMITTED_ON.plusDays(1)));
    }

    @Test
    void aRefusedDeliveryVoidsTheSubmission() {
        FieldSheet fieldSheet = submitted();

        fieldSheet.recordDeliveryRefused();

        assertEquals(FieldSheetStatus.VOIDED, fieldSheet.getStatus());
        assertThrows(FieldSheetNotDeliveredException.class, () -> fieldSheet.approve(REVIEWER, SUBMITTED_ON.plusDays(1), BENEFICIARY, List.of(), SUBMITTED_ON.minusDays(1), null));
    }

    @Test
    void recordingTheSameAnswerAgainChangesNothing() {
        FieldSheet fieldSheet = submitted();
        fieldSheet.recordDeliveryAccepted();

        fieldSheet.recordDeliveryAccepted();

        assertEquals(FieldSheetStatus.DELIVERED, fieldSheet.getStatus());
    }

    @Test
    void poiAnswersADeliveryOnce() {
        FieldSheet fieldSheet = submitted();
        fieldSheet.recordDeliveryAccepted();

        assertThrows(FieldSheetNotSubmittedException.class, fieldSheet::recordDeliveryRefused);
    }

    @Test
    void aReviewedFieldSheetIsNotReviewedAgain() {
        FieldSheet fieldSheet = reviewable();
        fieldSheet.approve(REVIEWER, SUBMITTED_ON.plusDays(1), BENEFICIARY, List.of(), SUBMITTED_ON.minusDays(1), null);

        assertThrows(FieldSheetAlreadyReviewedException.class, () -> fieldSheet.reject(REVIEWER, SUBMITTED_ON.plusDays(1)));
    }

    @Test
    void aFieldSheetIsSubmittedForATask() {
        FieldSheet fieldSheet = submitted();

        assertEquals(new TaskCode("2025-2030-01-03-0032"), fieldSheet.getTaskCode());
    }

    @Test
    void aTaskCodeHasPoisShape() {
        assertThrows(InvalidTaskCodeException.class, () -> new TaskCode("2025-2030-01-03-32"));
    }

    @Test
    void aSubmittedFieldSheetHoldsNoEvaluationYet() {
        FieldSheet fieldSheet = submitted();

        assertNull(fieldSheet.getBeneficiaryLegalDocument());
        assertTrue(fieldSheet.getUsedSupplies().isEmpty());
    }

    @Test
    void approvingStoresWhatTheReviewerConfirmed() {
        FieldSheet fieldSheet = reviewable();

        fieldSheet.approve(REVIEWER, SUBMITTED_ON.plusDays(1), BENEFICIARY, List.of(), SUBMITTED_ON.minusDays(1), null);

        assertEquals(BENEFICIARY, fieldSheet.getBeneficiaryLegalDocument());
        assertEquals(SUBMITTED_ON.minusDays(1), fieldSheet.getEvaluationDate());
    }

    @Test
    void anApprovalConfirmsTheBeneficiary() {
        FieldSheet fieldSheet = reviewable();

        assertThrows(
            MissingBeneficiaryException.class,
            () -> fieldSheet.approve(REVIEWER, SUBMITTED_ON.plusDays(1), null, List.of(), SUBMITTED_ON, null)
        );
        assertEquals(FieldSheetStatus.DELIVERED, fieldSheet.getStatus());
    }

    @Test
    void aDeliveredFieldSheetIsRelinkedToAnotherTask() {
        FieldSheet fieldSheet = reviewable();

        fieldSheet.relinkTo(OTHER_TASK);

        assertEquals(OTHER_TASK, fieldSheet.getTaskCode());
    }

    @Test
    void onlyAFieldSheetUnderReviewIsRelinked() {
        FieldSheet submitted = submitted();
        FieldSheet approved = reviewable();
        approved.approve(REVIEWER, SUBMITTED_ON.plusDays(1), BENEFICIARY, List.of(), SUBMITTED_ON.minusDays(1), null);

        assertThrows(FieldSheetNotDeliveredException.class, () -> submitted.relinkTo(OTHER_TASK));
        assertThrows(FieldSheetAlreadyReviewedException.class, () -> approved.relinkTo(OTHER_TASK));
    }

    /** A field sheet poi accepted, whose submission was already announced, so only what follows shows. */
    @Test
    void nobodyReviewsTheirOwnSubmission() {
        FieldSheet fieldSheet = reviewable();
        GeneralManagementAssistantId submitterAsReviewer =
            new GeneralManagementAssistantId(fieldSheet.getSubmittedBy().value());

        assertThrows(ReviewerIsSubmitterException.class, () -> fieldSheet.reject(submitterAsReviewer, SUBMITTED_ON.plusDays(1)));
        assertThrows(ReviewerIsSubmitterException.class, () -> fieldSheet.approve(submitterAsReviewer, SUBMITTED_ON.plusDays(1), BENEFICIARY, List.of(), SUBMITTED_ON.minusDays(1), null));
    }

    private static FieldSheet reviewable() {
        FieldSheet fieldSheet = submitted();
        fieldSheet.recordDeliveryAccepted();
        fieldSheet.pullDomainEvents();
        return fieldSheet;
    }

    private static FieldSheet submitted() {
        return submittedOn(SUBMITTED_ON, Optional.empty());
    }

    private static FieldSheet submittedOn(LocalDateTime submissionDate, Optional<FieldSheet> lastOfYear) {
        return FieldSheet.submit(
            new FieldSheetId(UUID.randomUUID()),
            new TaskCode("2025-2030-01-03-0032"),
            new FieldSheetScan(new ScanKey("field-sheets/scan.pdf"), 1024),
            new FieldAgentId(UUID.randomUUID()),
            submissionDate,
            lastOfYear
        );
    }

    private static FieldSheet reconstitutedAs(FieldSheetCode code) {
        return FieldSheet.reconstitute(
            new FieldSheetId(UUID.randomUUID()),
            code,
            new TaskCode("2025-2030-01-03-0032"),
            FieldSheetStatus.SUBMITTED,
            null,
            List.of(),
            null,
            null,
            new FieldSheetScan(new ScanKey("field-sheets/scan.pdf"), 1024),
            new FieldAgentId(UUID.randomUUID()),
            SUBMITTED_ON,
            null,
            null
        );
    }
}

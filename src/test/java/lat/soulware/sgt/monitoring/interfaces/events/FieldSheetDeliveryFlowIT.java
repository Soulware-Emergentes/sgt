package lat.soulware.sgt.monitoring.interfaces.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.FieldSheetCommandService;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ApproveFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RejectFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RelinkFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.SubmitFieldSheet;
import lat.soulware.sgt.monitoring.domain.model.exceptions.RelinkRefusedException;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.DeliveryOutcome;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.EvaluationDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetScan;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetStatus;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.RelinkOutcome;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.RelinkResult;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.domain.services.ScanStore;
import lat.soulware.sgt.monitoring.domain.services.TaskLedger;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentId;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantId;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.support.PostgresIntegrationTest;

/**
 * Runs a field sheet's delivery to poi end to end, events and listeners included, against a stub of
 * poi's ledger: a refused delivery voids the submission, an accepted one opens it to review, and
 * the review settles it in poi.
 */
@SpringBootTest
class FieldSheetDeliveryFlowIT extends PostgresIntegrationTest {

    /** How long the asynchronous listeners get to settle before the test gives up. */
    private static final Duration LISTENER_DEADLINE = Duration.ofSeconds(10);

    private static final TaskCode TASK = new TaskCode("2025-2030-01-03-0032");
    private static final TaskCode OTHER_TASK = new TaskCode("2025-2030-01-03-0033");
    private static final GeneralManagementAssistantId REVIEWER = new GeneralManagementAssistantId(UUID.randomUUID());

    @MockitoBean
    private TaskLedger taskLedger;

    @MockitoBean
    private ScanStore scanStore;

    @Autowired
    private FieldSheetCommandService fieldSheetCommandService;

    @Autowired
    private FieldSheetRepository fieldSheetRepository;

    @Test
    void aSubmissionPoiRefusesIsVoided() throws InterruptedException {
        when(this.taskLedger.registerDelivery(any(), any(), any())).thenReturn(DeliveryOutcome.REFUSED);

        FieldSheetId fieldSheetId = this.fieldSheetCommandService.submit(this.submission());

        assertThat(await(() -> this.statusOf(fieldSheetId), FieldSheetStatus.VOIDED)).isEqualTo(FieldSheetStatus.VOIDED);
    }

    @Test
    void anApprovalCompletesTheTaskInPoi() throws InterruptedException {
        when(this.taskLedger.registerDelivery(any(), any(), any())).thenReturn(DeliveryOutcome.ACCEPTED);
        FieldSheetId fieldSheetId = this.fieldSheetCommandService.submit(this.submission());
        await(() -> this.statusOf(fieldSheetId), FieldSheetStatus.DELIVERED);

        this.fieldSheetCommandService.approve(new ApproveFieldSheet(
            fieldSheetId,
            REVIEWER,
            new BeneficiaryLegalDocument(LegalDocumentType.DNI, "12345678"),
            List.of(),
            new EvaluationDate(LocalDateTime.now().minusDays(1)),
            null
        ));

        verify(this.taskLedger, timeout(LISTENER_DEADLINE.toMillis())).complete(TASK, fieldSheetId);
    }

    @Test
    void aRejectionHandsTheTaskBackToPoi() throws InterruptedException {
        when(this.taskLedger.registerDelivery(any(), any(), any())).thenReturn(DeliveryOutcome.ACCEPTED);
        FieldSheetId fieldSheetId = this.fieldSheetCommandService.submit(this.submission());
        await(() -> this.statusOf(fieldSheetId), FieldSheetStatus.DELIVERED);

        this.fieldSheetCommandService.reject(new RejectFieldSheet(fieldSheetId, REVIEWER));

        verify(this.taskLedger, timeout(LISTENER_DEADLINE.toMillis())).reopen(TASK, fieldSheetId);
    }

    @Test
    void aRelinkOntoAPendingTaskMovesTheFieldSheet() throws InterruptedException {
        when(this.taskLedger.registerDelivery(any(), any(), any())).thenReturn(DeliveryOutcome.ACCEPTED);
        FieldSheetId fieldSheetId = this.delivered(TASK);
        when(this.taskLedger.relink(OTHER_TASK, TASK, fieldSheetId)).thenReturn(new RelinkOutcome(RelinkResult.MOVED, null));

        this.fieldSheetCommandService.relink(new RelinkFieldSheet(fieldSheetId, OTHER_TASK));

        assertThat(this.fieldSheetRepository.getById(fieldSheetId).getTaskCode()).isEqualTo(OTHER_TASK);
    }

    @Test
    void aRelinkOntoAHeldTaskSwapsBothFieldSheets() throws InterruptedException {
        when(this.taskLedger.registerDelivery(any(), any(), any())).thenReturn(DeliveryOutcome.ACCEPTED);
        FieldSheetId relinked = this.delivered(TASK);
        FieldSheetId swapped = this.delivered(OTHER_TASK);
        when(this.taskLedger.relink(OTHER_TASK, TASK, relinked)).thenReturn(new RelinkOutcome(RelinkResult.SWAPPED, swapped));

        this.fieldSheetCommandService.relink(new RelinkFieldSheet(relinked, OTHER_TASK));

        assertThat(this.fieldSheetRepository.getById(relinked).getTaskCode()).isEqualTo(OTHER_TASK);
        assertThat(this.fieldSheetRepository.getById(swapped).getTaskCode()).isEqualTo(TASK);
    }

    @Test
    void aRelinkPoiRefusesLeavesTheFieldSheetWhereItWas() throws InterruptedException {
        when(this.taskLedger.registerDelivery(any(), any(), any())).thenReturn(DeliveryOutcome.ACCEPTED);
        FieldSheetId fieldSheetId = this.delivered(TASK);
        when(this.taskLedger.relink(OTHER_TASK, TASK, fieldSheetId)).thenReturn(new RelinkOutcome(RelinkResult.REFUSED, null));

        assertThatThrownBy(() -> this.fieldSheetCommandService.relink(new RelinkFieldSheet(fieldSheetId, OTHER_TASK)))
            .isInstanceOf(RelinkRefusedException.class);
        assertThat(this.fieldSheetRepository.getById(fieldSheetId).getTaskCode()).isEqualTo(TASK);
    }

    private FieldSheetStatus statusOf(FieldSheetId fieldSheetId) {
        return this.fieldSheetRepository.getById(fieldSheetId).getStatus();
    }

    private SubmitFieldSheet submission() {
        return this.submission(TASK);
    }

    private SubmitFieldSheet submission(TaskCode task) {
        ScanKey key = new ScanKey("field-sheets/%s.pdf".formatted(UUID.randomUUID()));
        when(this.scanStore.inspect(key)).thenReturn(new FieldSheetScan(key, 1024));

        return new SubmitFieldSheet(task, key, new FieldAgentId(UUID.randomUUID()));
    }

    private FieldSheetId delivered(TaskCode task) throws InterruptedException {
        FieldSheetId fieldSheetId = this.fieldSheetCommandService.submit(this.submission(task));
        await(() -> this.statusOf(fieldSheetId), FieldSheetStatus.DELIVERED);
        return fieldSheetId;
    }

    /** Polls {@code read} until it answers {@code expected} or the deadline passes, and returns the last answer. */
    private static <T> T await(Supplier<T> read, T expected) throws InterruptedException {
        Instant deadline = Instant.now().plus(LISTENER_DEADLINE);
        T answer = read.get();
        while (!expected.equals(answer) && Instant.now().isBefore(deadline)) {
            Thread.sleep(50);
            answer = read.get();
        }
        return answer;
    }
}

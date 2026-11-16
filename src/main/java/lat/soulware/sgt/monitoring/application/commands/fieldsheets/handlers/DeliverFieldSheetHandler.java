package lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.DeliverFieldSheet;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.DeliveryOutcome;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ExecutionDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetStatus;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.domain.services.TaskLedger;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/**
 * Delivers a submitted field sheet to poi and records whether poi accepted it. A submission
 * announced again finds poi's answer already recorded and leaves it as it is.
 */
@Service
@Transactional
public class DeliverFieldSheetHandler implements CommandHandler {

    private final FieldSheetRepository fieldSheetRepository;
    private final TaskLedger taskLedger;

    public DeliverFieldSheetHandler(FieldSheetRepository fieldSheetRepository, TaskLedger taskLedger) {
        this.fieldSheetRepository = fieldSheetRepository;
        this.taskLedger = taskLedger;
    }

    public void handle(DeliverFieldSheet command) {
        FieldSheet fieldSheet = this.fieldSheetRepository.getById(command.fieldSheetId());
        boolean isAnswered = fieldSheet.getStatus() != FieldSheetStatus.SUBMITTED;
        if (isAnswered) {
            return;
        }

        DeliveryOutcome outcome = this.taskLedger.registerDelivery(
            fieldSheet.getTaskCode(),
            fieldSheet.getId(),
            new ExecutionDate(fieldSheet.getSubmissionDate().value().toLocalDate())
        );
        if (outcome == DeliveryOutcome.ACCEPTED) {
            fieldSheet.recordDeliveryAccepted();
        } else {
            fieldSheet.recordDeliveryRefused();
        }
        this.fieldSheetRepository.save(fieldSheet);
    }
}

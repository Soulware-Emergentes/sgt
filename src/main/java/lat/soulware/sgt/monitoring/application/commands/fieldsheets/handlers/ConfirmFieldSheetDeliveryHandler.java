package lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ConfirmFieldSheetDelivery;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.domain.services.TaskLedger;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/** Confirms an approved field sheet's delivery in poi, completing its task. */
@Service
@Transactional
public class ConfirmFieldSheetDeliveryHandler implements CommandHandler {

    private final FieldSheetRepository fieldSheetRepository;
    private final TaskLedger taskLedger;

    public ConfirmFieldSheetDeliveryHandler(FieldSheetRepository fieldSheetRepository, TaskLedger taskLedger) {
        this.fieldSheetRepository = fieldSheetRepository;
        this.taskLedger = taskLedger;
    }

    public void handle(ConfirmFieldSheetDelivery command) {
        FieldSheet fieldSheet = this.fieldSheetRepository.getById(command.fieldSheetId());
        this.taskLedger.confirmCompletion(fieldSheet.getTaskCode(), fieldSheet.getId());
    }
}

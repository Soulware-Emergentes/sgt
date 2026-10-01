package lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RejectFieldSheetDelivery;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.domain.services.TaskLedger;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/** Rejects a rejected field sheet's delivery in poi, putting its task back on the pending list. */
@Service
@Transactional
public class RejectFieldSheetDeliveryHandler implements CommandHandler {

    private final FieldSheetRepository fieldSheetRepository;
    private final TaskLedger taskLedger;

    public RejectFieldSheetDeliveryHandler(FieldSheetRepository fieldSheetRepository, TaskLedger taskLedger) {
        this.fieldSheetRepository = fieldSheetRepository;
        this.taskLedger = taskLedger;
    }

    public void handle(RejectFieldSheetDelivery command) {
        FieldSheet fieldSheet = this.fieldSheetRepository.getById(command.fieldSheetId());
        this.taskLedger.rejectDelivery(fieldSheet.getTaskCode(), fieldSheet.getId());
    }
}

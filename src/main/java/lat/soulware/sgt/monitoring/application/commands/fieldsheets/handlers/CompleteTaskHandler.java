package lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.CompleteTask;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.domain.services.TaskLedger;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/** Completes the poi task of an approved field sheet. */
@Service
@Transactional
public class CompleteTaskHandler implements CommandHandler {

    private final FieldSheetRepository fieldSheetRepository;
    private final TaskLedger taskLedger;

    public CompleteTaskHandler(FieldSheetRepository fieldSheetRepository, TaskLedger taskLedger) {
        this.fieldSheetRepository = fieldSheetRepository;
        this.taskLedger = taskLedger;
    }

    public void handle(CompleteTask command) {
        FieldSheet fieldSheet = this.fieldSheetRepository.getById(command.fieldSheetId());
        this.taskLedger.complete(fieldSheet.getTaskCode(), fieldSheet.getId());
    }
}

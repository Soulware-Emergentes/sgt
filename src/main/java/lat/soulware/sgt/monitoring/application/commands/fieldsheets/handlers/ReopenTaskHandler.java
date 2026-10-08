package lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ReopenTask;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.domain.services.TaskLedger;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/** Reopens the poi task of a rejected field sheet, putting it back on the pending list. */
@Service
@Transactional
public class ReopenTaskHandler implements CommandHandler {

    private final FieldSheetRepository fieldSheetRepository;
    private final TaskLedger taskLedger;

    public ReopenTaskHandler(FieldSheetRepository fieldSheetRepository, TaskLedger taskLedger) {
        this.fieldSheetRepository = fieldSheetRepository;
        this.taskLedger = taskLedger;
    }

    public void handle(ReopenTask command) {
        FieldSheet fieldSheet = this.fieldSheetRepository.getById(command.fieldSheetId());
        this.taskLedger.reopen(fieldSheet.getTaskCode(), fieldSheet.getId());
    }
}

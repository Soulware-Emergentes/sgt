package lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RelinkFieldSheet;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.model.exceptions.RelinkRefusedException;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.RelinkOutcome;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.RelinkResult;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.domain.services.TaskLedger;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/**
 * Relinks a field sheet to another task, asking poi first. When poi swaps it with the field sheet
 * that task held, both are relinked together. poi answers a relink sent again the same way, so a
 * failure after poi's answer is safe to retry.
 */
@Service
@Transactional
public class RelinkFieldSheetHandler implements CommandHandler {

    private final FieldSheetRepository fieldSheetRepository;
    private final TaskLedger taskLedger;

    public RelinkFieldSheetHandler(FieldSheetRepository fieldSheetRepository, TaskLedger taskLedger) {
        this.fieldSheetRepository = fieldSheetRepository;
        this.taskLedger = taskLedger;
    }

    public void handle(RelinkFieldSheet command) {
        FieldSheet fieldSheet = this.fieldSheetRepository.getById(command.fieldSheetId());
        TaskCode previousTask = fieldSheet.getTaskCode();

        RelinkOutcome outcome = this.taskLedger.relink(command.taskCode(), previousTask, fieldSheet.getId());
        if (outcome.result() == RelinkResult.REFUSED) {
            throw new RelinkRefusedException(fieldSheet.getId(), command.taskCode());
        }

        fieldSheet.relinkTo(command.taskCode());
        this.fieldSheetRepository.save(fieldSheet);
        if (outcome.result() == RelinkResult.SWAPPED) {
            FieldSheet swapped = this.fieldSheetRepository.getById(outcome.swappedWith());
            swapped.relinkTo(previousTask);
            this.fieldSheetRepository.save(swapped);
        }
    }
}

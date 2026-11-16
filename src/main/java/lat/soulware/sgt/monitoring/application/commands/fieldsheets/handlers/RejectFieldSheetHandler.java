package lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RejectFieldSheet;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ReviewDate;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/** Records the field sheet's review as a rejection, reviewed now. */
@Service
@Transactional
public class RejectFieldSheetHandler implements CommandHandler {

    private final FieldSheetRepository fieldSheetRepository;

    public RejectFieldSheetHandler(FieldSheetRepository fieldSheetRepository) {
        this.fieldSheetRepository = fieldSheetRepository;
    }

    public void handle(RejectFieldSheet command) {
        FieldSheet fieldSheet = this.fieldSheetRepository.getById(command.fieldSheetId());
        fieldSheet.reject(command.reviewer(), new ReviewDate(LocalDateTime.now()));
        this.fieldSheetRepository.save(fieldSheet);
    }
}

package lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ApproveFieldSheet;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/** Records the field sheet's review as an approval, reviewed now, with the data the reviewer confirmed. */
@Service
@Transactional
public class ApproveFieldSheetHandler implements CommandHandler {

    private final FieldSheetRepository fieldSheetRepository;

    public ApproveFieldSheetHandler(FieldSheetRepository fieldSheetRepository) {
        this.fieldSheetRepository = fieldSheetRepository;
    }

    public void handle(ApproveFieldSheet command) {
        FieldSheet fieldSheet = this.fieldSheetRepository.getById(command.fieldSheetId());
        boolean statesSecurement = command.securementDate() != null;
        LocalDateTime securementDate = statesSecurement
            ? command.securementDate().value()
            : null;

        fieldSheet.approve(
            command.reviewer(),
            LocalDateTime.now(),
            command.beneficiary(),
            command.usedSupplies(),
            command.evaluationDate().value(),
            securementDate
        );
        this.fieldSheetRepository.save(fieldSheet);
    }
}

package lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.SubmitFieldSheet;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.domain.services.ScanStore;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/** Stores a newly submitted field sheet, submitted now, once its scanned PDF is found uploaded. */
@Service
@Transactional
public class SubmitFieldSheetHandler implements CommandHandler {

    private final FieldSheetRepository fieldSheetRepository;
    private final ScanStore scanStore;

    public SubmitFieldSheetHandler(FieldSheetRepository fieldSheetRepository, ScanStore scanStore) {
        this.fieldSheetRepository = fieldSheetRepository;
        this.scanStore = scanStore;
    }

    public FieldSheetId handle(SubmitFieldSheet command) {
        FieldSheet fieldSheet = FieldSheet.submit(
            new FieldSheetId(UUID.randomUUID()),
            command.taskCode(),
            this.scanStore.inspect(command.scanKey()),
            command.submittedBy(),
            LocalDateTime.now()
        );

        return this.fieldSheetRepository.save(fieldSheet).getId();
    }
}

package lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.SubmitFieldSheet;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SubmissionDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SubmissionYear;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.domain.services.ScanStore;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/**
 * Stores a newly submitted field sheet, submitted now, once its scanned PDF is found uploaded, and
 * numbered after the last field sheet of the year.
 *
 * <p>Serializable, because the number depends on the field sheets already submitted. Two submissions
 * could otherwise each read the same last field sheet and take the same number. Postgres fails one
 * of the two at commit instead, and the table's unique code backs that up.
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class SubmitFieldSheetHandler implements CommandHandler {

    private final FieldSheetRepository fieldSheetRepository;
    private final ScanStore scanStore;

    public SubmitFieldSheetHandler(FieldSheetRepository fieldSheetRepository, ScanStore scanStore) {
        this.fieldSheetRepository = fieldSheetRepository;
        this.scanStore = scanStore;
    }

    public FieldSheetId handle(SubmitFieldSheet command) {
        SubmissionDate submissionDate = new SubmissionDate(LocalDateTime.now());
        FieldSheet fieldSheet = FieldSheet.submit(
            new FieldSheetId(UUID.randomUUID()),
            command.taskCode(),
            this.scanStore.inspect(command.scanKey()),
            command.submittedBy(),
            submissionDate,
            this.fieldSheetRepository.findLastSubmittedIn(SubmissionYear.of(submissionDate.value()))
        );

        return this.fieldSheetRepository.save(fieldSheet).getId();
    }
}

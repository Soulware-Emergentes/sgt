package lat.soulware.sgt.monitoring.application.commands.fieldsheets;

import org.springframework.stereotype.Service;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.ApproveFieldSheetHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.CompleteTaskHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.DeliverFieldSheetHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.ReopenTaskHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.RejectFieldSheetHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.RelinkFieldSheetHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.SubmitFieldSheetHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ApproveFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.CompleteTask;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.DeliverFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ReopenTask;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RejectFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RelinkFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.SubmitFieldSheet;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.commands.CommandService;

/** The write entry point for field sheets. */
@Service
public class FieldSheetCommandService implements CommandService {

    private final SubmitFieldSheetHandler submitFieldSheetHandler;
    private final ApproveFieldSheetHandler approveFieldSheetHandler;
    private final RejectFieldSheetHandler rejectFieldSheetHandler;
    private final RelinkFieldSheetHandler relinkFieldSheetHandler;
    private final DeliverFieldSheetHandler deliverFieldSheetHandler;
    private final CompleteTaskHandler completeTaskHandler;
    private final ReopenTaskHandler reopenTaskHandler;

    public FieldSheetCommandService(
        SubmitFieldSheetHandler submitFieldSheetHandler,
        ApproveFieldSheetHandler approveFieldSheetHandler,
        RejectFieldSheetHandler rejectFieldSheetHandler,
        RelinkFieldSheetHandler relinkFieldSheetHandler,
        DeliverFieldSheetHandler deliverFieldSheetHandler,
        CompleteTaskHandler completeTaskHandler,
        ReopenTaskHandler reopenTaskHandler
    ) {
        this.submitFieldSheetHandler = submitFieldSheetHandler;
        this.approveFieldSheetHandler = approveFieldSheetHandler;
        this.rejectFieldSheetHandler = rejectFieldSheetHandler;
        this.relinkFieldSheetHandler = relinkFieldSheetHandler;
        this.deliverFieldSheetHandler = deliverFieldSheetHandler;
        this.completeTaskHandler = completeTaskHandler;
        this.reopenTaskHandler = reopenTaskHandler;
    }

    public FieldSheetId submit(SubmitFieldSheet command) {
        return this.submitFieldSheetHandler.handle(command);
    }

    public void approve(ApproveFieldSheet command) {
        this.approveFieldSheetHandler.handle(command);
    }

    public void reject(RejectFieldSheet command) {
        this.rejectFieldSheetHandler.handle(command);
    }

    public void relink(RelinkFieldSheet command) {
        this.relinkFieldSheetHandler.handle(command);
    }

    public void deliver(DeliverFieldSheet command) {
        this.deliverFieldSheetHandler.handle(command);
    }

    public void completeTask(CompleteTask command) {
        this.completeTaskHandler.handle(command);
    }

    public void reopenTask(ReopenTask command) {
        this.reopenTaskHandler.handle(command);
    }
}

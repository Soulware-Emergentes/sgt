package lat.soulware.sgt.monitoring.application.commands.fieldsheets;

import org.springframework.stereotype.Service;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.ApproveFieldSheetHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.ConfirmFieldSheetDeliveryHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.DeliverFieldSheetHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.RejectFieldSheetDeliveryHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.RejectFieldSheetHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.RelinkFieldSheetHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.handlers.SubmitFieldSheetHandler;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ApproveFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ConfirmFieldSheetDelivery;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.DeliverFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RejectFieldSheetDelivery;
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
    private final ConfirmFieldSheetDeliveryHandler confirmFieldSheetDeliveryHandler;
    private final RejectFieldSheetDeliveryHandler rejectFieldSheetDeliveryHandler;

    public FieldSheetCommandService(
        SubmitFieldSheetHandler submitFieldSheetHandler,
        ApproveFieldSheetHandler approveFieldSheetHandler,
        RejectFieldSheetHandler rejectFieldSheetHandler,
        RelinkFieldSheetHandler relinkFieldSheetHandler,
        DeliverFieldSheetHandler deliverFieldSheetHandler,
        ConfirmFieldSheetDeliveryHandler confirmFieldSheetDeliveryHandler,
        RejectFieldSheetDeliveryHandler rejectFieldSheetDeliveryHandler
    ) {
        this.submitFieldSheetHandler = submitFieldSheetHandler;
        this.approveFieldSheetHandler = approveFieldSheetHandler;
        this.rejectFieldSheetHandler = rejectFieldSheetHandler;
        this.relinkFieldSheetHandler = relinkFieldSheetHandler;
        this.deliverFieldSheetHandler = deliverFieldSheetHandler;
        this.confirmFieldSheetDeliveryHandler = confirmFieldSheetDeliveryHandler;
        this.rejectFieldSheetDeliveryHandler = rejectFieldSheetDeliveryHandler;
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

    public void confirmDelivery(ConfirmFieldSheetDelivery command) {
        this.confirmFieldSheetDeliveryHandler.handle(command);
    }

    public void rejectDelivery(RejectFieldSheetDelivery command) {
        this.rejectFieldSheetDeliveryHandler.handle(command);
    }
}

package lat.soulware.sgt.monitoring.interfaces.events.fieldsheets;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.FieldSheetCommandService;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ReopenTask;
import lat.soulware.sgt.monitoring.domain.model.events.FieldSheetRejected;
import lat.soulware.sgt.shared.interfaces.events.EventHandler;

/** Puts the poi task of each rejected field sheet back on the pending list. */
@Component
public class FieldSheetRejectedHandler implements EventHandler {

    private final FieldSheetCommandService fieldSheetCommandService;

    public FieldSheetRejectedHandler(FieldSheetCommandService fieldSheetCommandService) {
        this.fieldSheetCommandService = fieldSheetCommandService;
    }

    @ApplicationModuleListener
    public void on(FieldSheetRejected event) {
        this.fieldSheetCommandService.reopenTask(new ReopenTask(event.fieldSheetId()));
    }
}

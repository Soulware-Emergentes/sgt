package lat.soulware.sgt.monitoring.interfaces.events.fieldsheets;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.FieldSheetCommandService;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.CompleteTask;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetApproved;
import lat.soulware.sgt.shared.interfaces.events.EventHandler;

/** Completes the poi task of each approved field sheet. */
@Component
public class FieldSheetApprovedHandler implements EventHandler {

    private final FieldSheetCommandService fieldSheetCommandService;

    public FieldSheetApprovedHandler(FieldSheetCommandService fieldSheetCommandService) {
        this.fieldSheetCommandService = fieldSheetCommandService;
    }

    @ApplicationModuleListener
    public void on(FieldSheetApproved event) {
        this.fieldSheetCommandService.completeTask(new CompleteTask(event.fieldSheetId()));
    }
}

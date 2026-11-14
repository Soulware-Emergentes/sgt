package lat.soulware.sgt.monitoring.interfaces.events.fieldsheets;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.FieldSheetCommandService;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.DeliverFieldSheet;
import lat.soulware.sgt.monitoring.domain.model.events.FieldSheetSubmitted;
import lat.soulware.sgt.shared.interfaces.events.EventHandler;

/** Delivers each submitted field sheet to poi, against the task it names. */
@Component
public class FieldSheetSubmittedHandler implements EventHandler {

    private final FieldSheetCommandService fieldSheetCommandService;

    public FieldSheetSubmittedHandler(FieldSheetCommandService fieldSheetCommandService) {
        this.fieldSheetCommandService = fieldSheetCommandService;
    }

    @ApplicationModuleListener
    public void on(FieldSheetSubmitted event) {
        this.fieldSheetCommandService.deliver(new DeliverFieldSheet(event.fieldSheetId()));
    }
}

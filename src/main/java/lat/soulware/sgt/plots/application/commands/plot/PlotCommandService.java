package lat.soulware.sgt.plots.application.commands.plot;

import org.springframework.stereotype.Service;

import lat.soulware.sgt.plots.application.commands.plot.handlers.RegisterPlotHandler;
import lat.soulware.sgt.plots.application.commands.plot.intent.RegisterPlot;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.shared.application.commands.CommandService;

/** The write entry point for plots. */
@Service
public class PlotCommandService implements CommandService {

    private final RegisterPlotHandler registerPlotHandler;

    public PlotCommandService(RegisterPlotHandler registerPlotHandler) {
        this.registerPlotHandler = registerPlotHandler;
    }

    public PlotId register(RegisterPlot command) {
        return this.registerPlotHandler.handle(command);
    }
}

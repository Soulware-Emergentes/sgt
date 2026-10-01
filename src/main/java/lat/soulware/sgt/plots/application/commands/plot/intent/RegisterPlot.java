package lat.soulware.sgt.plots.application.commands.plot.intent;

import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Register a surveyed plot.
 *
 * @param boundary the perimeter the survey produced
 */
public record RegisterPlot(PlotBoundary boundary) implements CommandIntent {
}

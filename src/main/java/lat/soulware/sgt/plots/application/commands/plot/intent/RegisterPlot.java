package lat.soulware.sgt.plots.application.commands.plot.intent;

import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Register a surveyed plot to a beneficiary. The district it lies in is worked out from the
 * boundary.
 *
 * @param owner    the legal document of the beneficiary the plot is registered to
 * @param boundary the perimeter the survey produced
 */
public record RegisterPlot(
    BeneficiaryLegalDocument owner,
    PlotBoundary boundary
) implements CommandIntent {
}

package lat.soulware.sgt.plots.interfaces.rest.plot.responses;

import java.util.List;

/**
 * Plots, ordered by ubigeo and then registration.
 *
 * @param plots the plots
 */
public record PlotsResponse(List<PlotResponse> plots) {
}

package lat.soulware.sgt.plots.interfaces.rest.plot;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

import lat.soulware.sgt.plots.application.commands.plot.PlotCommandService;
import lat.soulware.sgt.plots.application.queries.plots.PlotQueryService;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.plots.interfaces.rest.plot.mappers.PlotWireMapper;
import lat.soulware.sgt.plots.interfaces.rest.plot.requests.RegisterPlotRequest;
import lat.soulware.sgt.plots.interfaces.rest.plot.responses.PlotResponse;
import lat.soulware.sgt.plots.interfaces.rest.plot.responses.PlotsResponse;
import lat.soulware.sgt.shared.interfaces.rest.security.Caller;

/**
 * Plots, registered by the field agent who surveyed them and read back by id, by administrative
 * region or by the beneficiary they are registered to. Every endpoint takes the {@link Caller} the
 * request's token names, refused when not acting as a field agent; an admin always is.
 *
 * <p>Registering answers with the plot it created.
 */
@RestController
@RequestMapping("/api/v1")
public class PlotController {

    private final PlotCommandService plotCommandService;
    private final PlotQueryService plotQueryService;

    public PlotController(PlotCommandService plotCommandService, PlotQueryService plotQueryService) {
        this.plotCommandService = plotCommandService;
        this.plotQueryService = plotQueryService;
    }

    @Operation(summary = "Registers a plot to a beneficiary and returns it")
    @PostMapping("/plots")
    @ResponseStatus(HttpStatus.CREATED)
    public PlotResponse register(@RequestBody RegisterPlotRequest request, Caller caller) {
        PlotId registered = this.plotCommandService.register(PlotWireMapper.toRegisterPlot(request, caller));

        return this.plot(registered.value(), caller);
    }

    @Operation(summary = "Returns the plots within an administrative region")
    @GetMapping("/plots")
    public PlotsResponse inRegion(@RequestParam String region, Caller caller) {
        return PlotWireMapper.toPlotsResponse(
            this.plotQueryService.inRegion(PlotWireMapper.toPlotsInRegion(region, caller))
        );
    }

    @Operation(summary = "Returns the plot of a given owner")
    @GetMapping("/plots/owner")
    public PlotResponse ofOwner(
        @RequestParam String documentType,
        @RequestParam String documentNumber,
        Caller caller
    ) {
        return PlotWireMapper.toPlotResponse(
            this.plotQueryService.ofOwner(PlotWireMapper.toPlotOfOwner(documentType, documentNumber, caller))
        );
    }

    @Operation(summary = "Returns a plot by its id")
    @GetMapping("/plots/{plotId}")
    public PlotResponse plot(@PathVariable UUID plotId, Caller caller) {
        return PlotWireMapper.toPlotResponse(
            this.plotQueryService.byId(PlotWireMapper.toPlotById(plotId, caller))
        );
    }
}

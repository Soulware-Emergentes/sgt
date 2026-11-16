package lat.soulware.sgt.plots.application.commands.plot.handlers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.plots.application.commands.plot.intent.RegisterPlot;
import lat.soulware.sgt.plots.domain.model.aggregates.Plot;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.plots.domain.model.valueobjects.RegistrationDate;
import lat.soulware.sgt.plots.domain.model.valueobjects.Ubigeo;
import lat.soulware.sgt.plots.domain.repositories.PlotRepository;
import lat.soulware.sgt.plots.domain.services.PlotDistricts;
import lat.soulware.sgt.plots.domain.services.PlotGeometry;
import lat.soulware.sgt.shared.application.commands.handlers.CommandHandler;

/**
 * Registers a plot against the plots already around it, to a beneficiary who holds none yet, under
 * the district holding most of it.
 *
 * <p>Serializable, because the overlap rule spans aggregates. Two registrations of overlapping
 * plots could otherwise each load neighbours, find the other missing, and both commit. Postgres
 * tracks what each transaction read and fails one of the two at commit instead. The caller gets that
 * failure as an error, and submitting again finds the committed plot and reports the overlap. Two
 * registrations to the same beneficiary are stopped the same way, and the table's unique owner
 * backs that up.
 */
@Transactional(isolation = Isolation.SERIALIZABLE)
@Service
public class RegisterPlotHandler implements CommandHandler {

    private final PlotRepository plotRepository;
    private final PlotGeometry plotGeometry;
    private final PlotDistricts plotDistricts;

    public RegisterPlotHandler(PlotRepository plotRepository, PlotGeometry plotGeometry, PlotDistricts plotDistricts) {
        this.plotRepository = plotRepository;
        this.plotGeometry = plotGeometry;
        this.plotDistricts = plotDistricts;
    }

    public PlotId handle(RegisterPlot command) {
        Optional<Plot> ownersPlot = this.plotRepository.findOwnedBy(command.owner());
        List<Plot> neighbours = this.plotRepository.findIntersecting(command.boundary());
        Ubigeo ubigeo = this.plotDistricts.districtOf(command.boundary());
        PlotId id = new PlotId(UUID.randomUUID());
        Plot plot = Plot.register(
            id,
            command.owner(),
            command.boundary(),
            ubigeo,
            new RegistrationDate(LocalDateTime.now()),
            ownersPlot,
            neighbours,
            this.plotGeometry
        );

        return this.plotRepository.save(plot).getId();
    }
}

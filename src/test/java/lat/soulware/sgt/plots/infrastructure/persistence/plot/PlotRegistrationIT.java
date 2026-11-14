package lat.soulware.sgt.plots.infrastructure.persistence.plot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.plots.application.commands.plot.handlers.RegisterPlotHandler;
import lat.soulware.sgt.plots.application.commands.plot.intent.RegisterPlot;
import lat.soulware.sgt.plots.domain.model.aggregates.Plot;
import lat.soulware.sgt.plots.domain.model.exceptions.BeneficiaryAlreadyHoldsPlotException;
import lat.soulware.sgt.plots.domain.model.exceptions.OverlappingPlotException;
import lat.soulware.sgt.plots.domain.model.exceptions.PlotOutsideDistrictsException;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.plots.domain.model.valueobjects.RegistrationDate;
import lat.soulware.sgt.plots.domain.model.valueobjects.SurveyPoint;
import lat.soulware.sgt.plots.domain.model.valueobjects.Ubigeo;
import lat.soulware.sgt.plots.domain.model.valueobjects.UtmZone;
import lat.soulware.sgt.plots.domain.repositories.PlotRepository;
import lat.soulware.sgt.plots.domain.services.PlotGeometry;
import lat.soulware.sgt.support.PostgresIntegrationTest;

/**
 * Runs plot registration against PostGIS: which neighbours the repository finds, how a plot reads
 * back, that serializable isolation stops two overlapping registrations committing together, and
 * that a beneficiary holds a single plot.
 *
 * <p>The plots are squares near Lima in zone 18S, each test in its own stretch of ground, so a plot
 * one test commits cannot become another's neighbour. Each plot goes to its own beneficiary for the
 * same reason.
 */
@SpringBootTest
class PlotRegistrationIT extends PostgresIntegrationTest {

    private static final RegistrationDate REGISTERED = new RegistrationDate(LocalDateTime.of(2026, 10, 1, 9, 0));
    private static final Ubigeo DISTRICT = new Ubigeo("150101");
    /** The district of La Molina, in Lima, which the 18S squares near easting 287 000 lie in. */
    private static final Ubigeo LA_MOLINA = new Ubigeo("150114");
    private static final AtomicInteger NEXT_DOCUMENT_NUMBER = new AtomicInteger(10_000_000);

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private PlotGeometry plotGeometry;

    @Autowired
    private RegisterPlotHandler registerPlotHandler;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @Transactional
    void findsTheOverlappingAndTouchingPlotsAndNoOther() {
        PlotId overlapped = this.store(square(280_000, 8_665_000, 100));
        PlotId touched = this.store(square(280_200, 8_665_000, 100));
        this.store(square(281_000, 8_665_000, 100));

        PlotBoundary between = square(280_050, 8_665_000, 150);

        assertThat(this.plotRepository.findIntersecting(between))
            .extracting(p -> p.getId())
            .containsExactlyInAnyOrder(overlapped, touched);
    }

    @Test
    @Transactional
    void aStoredPlotLoadsBackInItsZoneWithItsOwnerDistrictAndRegistrationDate() {
        BeneficiaryLegalDocument owner = newOwner();
        PlotBoundary boundary = square(282_000, 8_665_000, 100);
        PlotId stored = this.store(owner, boundary);

        Plot loaded = this.plotRepository.getById(stored);
        assertThat(loaded.getBoundary()).isEqualTo(boundary);
        assertThat(loaded.getOwner()).isEqualTo(owner);
        assertThat(loaded.getUbigeo()).isEqualTo(DISTRICT);
        assertThat(loaded.getRegistrationDate()).isEqualTo(REGISTERED);
    }

    /**
     * Both registrations load their neighbours before either writes, so each sees an empty field and
     * passes the overlap rule. The barrier holds them there. Only isolation can stop the second, and
     * it does so at commit.
     */
    @Test
    void serializableIsolationRejectsOneOfTwoConcurrentOverlappingRegistrations() throws Exception {
        TransactionTemplate serializable = new TransactionTemplate(this.transactionManager);
        serializable.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        CyclicBarrier bothHaveRead = new CyclicBarrier(2);

        PlotBoundary first = square(283_000, 8_665_000, 100);
        PlotBoundary overlappingFirst = square(283_050, 8_665_000, 100);

        CompletableFuture<PlotId> one = CompletableFuture.supplyAsync(
            () -> serializable.execute(status -> this.registerAfter(bothHaveRead, first))
        );
        CompletableFuture<PlotId> other = CompletableFuture.supplyAsync(
            () -> serializable.execute(status -> this.registerAfter(bothHaveRead, overlappingFirst))
        );

        List<Throwable> failures = failuresOf(one, other);

        assertThat(failures)
            .singleElement()
            .isInstanceOf(ConcurrencyFailureException.class);
    }

    @Test
    @Transactional
    void registeringReportsAPlotAlreadyCommitted() {
        PlotBoundary first = square(284_000, 8_665_000, 100);
        PlotId stored = this.store(first);

        PlotBoundary inside = square(284_010, 8_665_010, 50);
        List<Plot> neighbours = this.plotRepository.findIntersecting(inside);

        assertThatExceptionOfType(OverlappingPlotException.class)
            .isThrownBy(() -> Plot.register(newId(), newOwner(), inside, DISTRICT, REGISTERED, Optional.empty(), neighbours, this.plotGeometry))
            .satisfies(exception -> assertThat(exception.getMessageArgs()).containsExactly(Set.of(stored)));
    }

    @Test
    @Transactional
    void aBeneficiaryWhoHoldsAPlotCannotBeRegisteredASecond() {
        BeneficiaryLegalDocument owner = newOwner();
        PlotId held = this.registerPlotHandler.handle(new RegisterPlot(owner, square(285_000, 8_665_000, 100)));

        RegisterPlot second = new RegisterPlot(owner, square(285_500, 8_665_000, 100));

        assertThatExceptionOfType(BeneficiaryAlreadyHoldsPlotException.class)
            .isThrownBy(() -> this.registerPlotHandler.handle(second))
            .satisfies(exception -> assertThat(exception.getMessageArgs()).containsExactly(owner, held));
    }

    @Test
    @Transactional
    void aRegisteredPlotIsFiledUnderTheDistrictItLiesIn() {
        PlotId registered = this.registerPlotHandler.handle(new RegisterPlot(newOwner(), square(287_000, 8_665_000, 100)));

        assertThat(this.plotRepository.getById(registered).getUbigeo()).isEqualTo(LA_MOLINA);
    }

    @Test
    @Transactional
    void aPlotInNoDistrictIsRefused() {
        RegisterPlot offshore = new RegisterPlot(newOwner(), square(100_000, 8_600_000, 100));

        assertThatExceptionOfType(PlotOutsideDistrictsException.class)
            .isThrownBy(() -> this.registerPlotHandler.handle(offshore));
    }

    @Test
    @Transactional
    void theTableHoldsASinglePlotPerBeneficiary() {
        BeneficiaryLegalDocument owner = newOwner();
        this.store(owner, square(286_000, 8_665_000, 100));

        assertThatExceptionOfType(ConstraintViolationException.class)
            .isThrownBy(() -> this.store(owner, square(286_500, 8_665_000, 100)))
            .satisfies(exception -> assertThat(exception.getConstraintName()).isEqualTo("plots_owner_key"));
    }

    /** The handler's steps, with a barrier between reading the neighbours and writing the plot. */
    private PlotId registerAfter(CyclicBarrier bothHaveRead, PlotBoundary boundary) {
        List<Plot> neighbours = this.plotRepository.findIntersecting(boundary);
        await(bothHaveRead);
        Plot plot = Plot.register(newId(), newOwner(), boundary, DISTRICT, REGISTERED, Optional.empty(), neighbours, this.plotGeometry);
        return this.plotRepository.save(plot).getId();
    }

    /**
     * Registers and saves a plot, then flushes. The repository's neighbour query is native SQL,
     * which Hibernate does not flush ahead of, so an unflushed plot would be invisible to it within
     * the test's one transaction. A real registration finds earlier plots already committed.
     */
    private PlotId store(PlotBoundary boundary) {
        return this.store(newOwner(), boundary);
    }

    private PlotId store(BeneficiaryLegalDocument owner, PlotBoundary boundary) {
        List<Plot> neighbours = this.plotRepository.findIntersecting(boundary);
        Plot plot = Plot.register(newId(), owner, boundary, DISTRICT, REGISTERED, Optional.empty(), neighbours, this.plotGeometry);
        PlotId stored = this.plotRepository.save(plot).getId();
        this.entityManager.flush();
        return stored;
    }

    private static List<Throwable> failuresOf(CompletableFuture<?>... registrations) throws InterruptedException {
        List<Throwable> failures = new ArrayList<>();
        for (CompletableFuture<?> registration : registrations) {
            try {
                registration.get();
            } catch (ExecutionException e) {
                failures.add(e.getCause());
            }
        }
        return failures;
    }

    private static void await(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (Exception e) {
            throw new IllegalStateException("Registrations did not meet at the barrier", e);
        }
    }

    private static BeneficiaryLegalDocument newOwner() {
        String number = String.valueOf(NEXT_DOCUMENT_NUMBER.getAndIncrement());

        return new BeneficiaryLegalDocument(LegalDocumentType.DNI, number);
    }

    private static PlotId newId() {
        return new PlotId(UUID.randomUUID());
    }

    private static PlotBoundary square(double easting, double northing, double side) {
        return new PlotBoundary(UtmZone.ZONE_18S, List.of(
            new SurveyPoint(easting, northing),
            new SurveyPoint(easting + side, northing),
            new SurveyPoint(easting + side, northing + side),
            new SurveyPoint(easting, northing + side)
        ));
    }
}

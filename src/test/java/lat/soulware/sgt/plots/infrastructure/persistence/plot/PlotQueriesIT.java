package lat.soulware.sgt.plots.infrastructure.persistence.plot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.plots.application.queries.plots.PlotQueryService;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotOfOwner;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotsInRegion;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotResult;
import lat.soulware.sgt.plots.application.queries.plots.results.VertexResult;
import lat.soulware.sgt.plots.domain.model.aggregates.Plot;
import lat.soulware.sgt.plots.domain.model.exceptions.BeneficiaryHoldsNoPlotException;
import lat.soulware.sgt.plots.domain.model.exceptions.InvalidRegionException;
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
 * Reads plots back from PostGIS by region and by owner, through the query service.
 *
 * <p>Four plots are stored per test, in districts of departments 04 and 08 and on ground away from
 * {@code PlotRegistrationIT}'s, so the plots that test commits never show up here.
 */
@SpringBootTest
@Transactional
class PlotQueriesIT extends PostgresIntegrationTest {

    private static final RegistrationDate REGISTERED = new RegistrationDate(LocalDateTime.of(2026, 10, 1, 9, 0));
    private static final BeneficiaryLegalDocument IN_WANCHAQ = owner("30000001");
    private static final BeneficiaryLegalDocument IN_CUSCO = owner("30000002");
    private static final BeneficiaryLegalDocument IN_ACOMAYO = owner("30000003");
    private static final BeneficiaryLegalDocument IN_AREQUIPA = owner("30000004");

    @Autowired
    private PlotQueryService plotQueryService;

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private PlotGeometry plotGeometry;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void storePlotsInFourDistricts() {
        this.store(IN_WANCHAQ, "080108", square(290_000, 8_500_000));
        this.store(IN_CUSCO, "080101", square(290_500, 8_500_000));
        this.store(IN_ACOMAYO, "080201", square(291_000, 8_500_000));
        this.store(IN_AREQUIPA, "040101", square(291_500, 8_500_000));
    }

    @Test
    void aDepartmentHoldsEveryPlotInItsProvincesOrderedByDistrict() {
        assertThat(this.ubigeosIn("08")).containsExactly("080101", "080108", "080201");
    }

    @Test
    void aProvinceAndADistrictNarrowTheDepartment() {
        assertThat(this.ubigeosIn("0801")).containsExactly("080101", "080108");
        assertThat(this.ubigeosIn("080201")).containsExactly("080201");
    }

    @Test
    void aRegionThatIsNoLevelOfAUbigeoIsRefused() {
        for (String region : List.of("0", "080", "08010", "0801011", "08a1")) {
            assertThatExceptionOfType(InvalidRegionException.class)
                .isThrownBy(() -> this.plotQueryService.inRegion(new PlotsInRegion(region)));
        }
    }

    @Test
    void aBeneficiarysPlotReadsBackAsSurveyed() {
        PlotResult plot = this.plotQueryService.ofOwner(new PlotOfOwner("DNI", "30000004"));

        assertThat(plot.ubigeo()).isEqualTo("040101");
        assertThat(plot.utmZone()).isEqualTo("ZONE_18S");
        assertThat(plot.registrationDate()).isEqualTo(REGISTERED.value());
        assertThat(plot.boundary()).containsExactly(
            new VertexResult(291_500, 8_500_000),
            new VertexResult(291_600, 8_500_000),
            new VertexResult(291_600, 8_500_100),
            new VertexResult(291_500, 8_500_100)
        );
    }

    @Test
    void aBeneficiaryWithNoPlotIsNotFound() {
        assertThatExceptionOfType(BeneficiaryHoldsNoPlotException.class)
            .isThrownBy(() -> this.plotQueryService.ofOwner(new PlotOfOwner("DNI", "39999999")));
    }

    private List<String> ubigeosIn(String region) {
        return this.plotQueryService.inRegion(new PlotsInRegion(region)).plots().stream()
            .map(plotResult -> plotResult.ubigeo())
            .toList();
    }

    /** Saves and flushes, so the projection's SQL sees the plot within the test's transaction. */
    private void store(BeneficiaryLegalDocument owner, String ubigeo, PlotBoundary boundary) {
        Plot plot = Plot.register(
            new PlotId(UUID.randomUUID()),
            owner,
            boundary,
            new Ubigeo(ubigeo),
            REGISTERED,
            Optional.empty(),
            List.of(),
            this.plotGeometry
        );
        this.plotRepository.save(plot);
        this.entityManager.flush();
    }

    private static BeneficiaryLegalDocument owner(String number) {
        return new BeneficiaryLegalDocument(LegalDocumentType.DNI, number);
    }

    private static PlotBoundary square(double easting, double northing) {
        return new PlotBoundary(UtmZone.ZONE_18S, List.of(
            new SurveyPoint(easting, northing),
            new SurveyPoint(easting + 100, northing),
            new SurveyPoint(easting + 100, northing + 100),
            new SurveyPoint(easting, northing + 100)
        ));
    }
}

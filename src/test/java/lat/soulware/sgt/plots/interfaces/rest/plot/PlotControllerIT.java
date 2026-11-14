package lat.soulware.sgt.plots.interfaces.rest.plot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import lat.soulware.sgt.support.PostgresIntegrationTest;
import lat.soulware.sgt.support.TestTokens;

/**
 * Drives the plot endpoints over HTTP: a field agent registers a plot and reads it back by id, by
 * region and by owner; an admin may do the same, and a reviewer may do none of it.
 *
 * <p>Every plot here lies in department 25 and on ground of its own, so the plots the other
 * integration tests commit never meet these.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PlotControllerIT extends PostgresIntegrationTest {

    /** The district of Putinza, in Yauyos, Lima, which every square of this class lies in. */
    private static final String UBIGEO = "151023";
    private static final AtomicInteger PLOTS = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void aRegisteredPlotIsAnsweredAndReadBackEveryWay() throws Exception {
        int plot = PLOTS.incrementAndGet();
        String owner = ownerOf(plot);

        String created = this.register(asFieldAgent(), plot)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.ownerDocumentType").value("DNI"))
            .andExpect(jsonPath("$.ownerDocumentNumber").value(owner))
            .andExpect(jsonPath("$.ubigeo").value(UBIGEO))
            .andExpect(jsonPath("$.utmZone").value("ZONE_18S"))
            .andExpect(jsonPath("$.boundary.length()").value(4))
            .andExpect(jsonPath("$.registrationDate").isNotEmpty())
            .andReturn()
            .getResponse()
            .getContentAsString();
        String id = created.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        this.mockMvc.perform(get("/api/v1/plots/{id}", id).header("Authorization", asFieldAgent()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ownerDocumentNumber").value(owner));
        this.mockMvc.perform(get("/api/v1/plots/owner")
                .param("documentType", "DNI")
                .param("documentNumber", owner)
                .header("Authorization", asFieldAgent())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id));
        this.mockMvc.perform(get("/api/v1/plots").param("region", "15").header("Authorization", asFieldAgent()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.plots[*].id").value(Matchers.hasItem(id)));
    }

    @Test
    void anAdminRegistersAndReads() throws Exception {
        int plot = PLOTS.incrementAndGet();

        this.register(asAdmin(), plot).andExpect(status().isCreated());
        this.mockMvc.perform(get("/api/v1/plots").param("region", "1510").header("Authorization", asAdmin()))
            .andExpect(status().isOk());
    }

    @Test
    void aReviewerCanNeitherRegisterNorRead() throws Exception {
        this.register(asReviewer(), PLOTS.incrementAndGet()).andExpect(status().isForbidden());
        this.mockMvc.perform(get("/api/v1/plots").param("region", "25").header("Authorization", asReviewer()))
            .andExpect(status().isForbidden());
    }

    @Test
    void aSecondPlotForTheSameBeneficiaryIsRefused() throws Exception {
        int plot = PLOTS.incrementAndGet();
        this.register(asFieldAgent(), plot).andExpect(status().isCreated());

        String body = body(ownerOf(plot), PLOTS.incrementAndGet());
        this.mockMvc.perform(post("/api/v1/plots")
                .header("Authorization", asFieldAgent())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
            )
            .andExpect(status().isUnprocessableContent());
    }

    @Test
    void aPlotInNoDistrictIsRefused() throws Exception {
        this.mockMvc.perform(post("/api/v1/plots")
                .header("Authorization", asFieldAgent())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"ownerDocumentType": "DNI", "ownerDocumentNumber": "%s", "utmZone": "ZONE_18S",
                     "boundary": [
                        {"easting": 100000, "northing": 8600000},
                        {"easting": 100100, "northing": 8600000},
                        {"easting": 100100, "northing": 8600100}
                     ]}""".formatted(ownerOf(PLOTS.incrementAndGet())))
            )
            .andExpect(status().isUnprocessableContent());
    }

    @Test
    void anUnknownPlotAndABeneficiaryWithoutOneAreNotFound() throws Exception {
        this.mockMvc.perform(get("/api/v1/plots/{id}", UUID.randomUUID()).header("Authorization", asFieldAgent()))
            .andExpect(status().isNotFound());
        this.mockMvc.perform(get("/api/v1/plots/owner")
                .param("documentType", "DNI")
                .param("documentNumber", "49999999")
                .header("Authorization", asFieldAgent())
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void aRegionThatIsNoLevelOfAUbigeoIsRefused() throws Exception {
        this.mockMvc.perform(get("/api/v1/plots").param("region", "250").header("Authorization", asFieldAgent()))
            .andExpect(status().isUnprocessableContent());
    }

    @Test
    void aMapViewAnswersThePlotsTouchingItAsGeoJson() throws Exception {
        int plot = PLOTS.incrementAndGet();
        String id = this.register(asFieldAgent(), plot)
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString()
            .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        // Zone 18S, 400 km east and 8,600 km north: around 75.9 degrees west and 12.6 south.
        this.mockMvc.perform(get("/api/v1/plots/within")
                .param("bbox", "-76.5,-13.0,-75.5,-12.0")
                .header("Authorization", asFieldAgent())
            )
            .andExpect(status().isOk())
            .andExpect(header().string("Cache-Control", "max-age=60, private"))
            .andExpect(header().stringValues("Vary", Matchers.hasItem("Authorization")))
            .andExpect(jsonPath("$.type").value("FeatureCollection"))
            .andExpect(jsonPath("$.truncated").value(false))
            .andExpect(jsonPath("$.features[?(@.id == '%s')].type".formatted(id)).value(Matchers.contains("Feature")))
            .andExpect(jsonPath("$.features[?(@.id == '%s')].geometry.type".formatted(id))
                .value(Matchers.contains("Polygon")))
            .andExpect(jsonPath("$.features[?(@.id == '%s')].geometry.coordinates[0].length()".formatted(id))
                .value(Matchers.contains(5)))
            .andExpect(jsonPath("$.features[?(@.id == '%s')].geometry.coordinates[0][0][0]".formatted(id))
                .value(Matchers.contains(near(-75.9))))
            .andExpect(jsonPath("$.features[?(@.id == '%s')].geometry.coordinates[0][0][1]".formatted(id))
                .value(Matchers.contains(near(-12.6))))
            .andExpect(jsonPath("$.features[?(@.id == '%s')].properties.ubigeo".formatted(id))
                .value(Matchers.contains(UBIGEO)));

        this.mockMvc.perform(get("/api/v1/plots/within")
                .param("bbox", "-70.5,-13.0,-70.0,-12.5")
                .header("Authorization", asFieldAgent())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.features[*].id").value(Matchers.not(Matchers.hasItem(id))));
    }

    @Test
    void aMapViewThatIsNoBoundingBoxIsRefused() throws Exception {
        for (String bbox : new String[] {"-76.5,-13.0,-75.5", "west,-13.0,-75.5,-12.0", "-75.5,-13.0,-76.5,-12.0", "-76.5,-13.0,-75.5,95"}) {
            this.mockMvc.perform(get("/api/v1/plots/within").param("bbox", bbox).header("Authorization", asFieldAgent()))
                .andExpect(status().isUnprocessableContent());
        }
    }

    @Test
    void aReviewerCannotReadAMapView() throws Exception {
        this.mockMvc.perform(get("/api/v1/plots/within")
                .param("bbox", "-76.5,-13.0,-75.5,-12.0")
                .header("Authorization", asReviewer())
            )
            .andExpect(status().isForbidden());
    }

    /** Any number within 0.2 of the one given, whichever type the JSON parser read it as. */
    private static Matcher<Object> near(double expected) {
        return new CustomMatcher<>("a number within 0.2 of " + expected) {
            @Override
            public boolean matches(Object actual) {
                return actual instanceof Number number && Math.abs(number.doubleValue() - expected) <= 0.2;
            }
        };
    }

    private ResultActions register(String authorization, int plot) throws Exception {
        return this.mockMvc.perform(post("/api/v1/plots")
            .header("Authorization", authorization)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body(ownerOf(plot), plot))
        );
    }

    /** A 100 m square on ground no other plot of this class uses. */
    private static String body(String owner, int ground) {
        int easting = 400_000 + ground * 200;
        int northing = 8_600_000;

        return """
            {"ownerDocumentType": "DNI", "ownerDocumentNumber": "%s", "utmZone": "ZONE_18S",
             "boundary": [
                {"easting": %d, "northing": %d},
                {"easting": %d, "northing": %d},
                {"easting": %d, "northing": %d},
                {"easting": %d, "northing": %d}
             ]}""".formatted(
            owner,
            easting, northing,
            easting + 100, northing,
            easting + 100, northing + 100,
            easting, northing + 100
        );
    }

    private static String ownerOf(int plot) {
        return "%08d".formatted(40_000_000 + plot);
    }

    private static String asFieldAgent() {
        return "Bearer " + TestTokens.person(UUID.randomUUID(), "field-agent");
    }

    private static String asAdmin() {
        return "Bearer " + TestTokens.person(UUID.randomUUID(), "admin");
    }

    private static String asReviewer() {
        return "Bearer " + TestTokens.person(UUID.randomUUID(), "general-management-assistant");
    }
}

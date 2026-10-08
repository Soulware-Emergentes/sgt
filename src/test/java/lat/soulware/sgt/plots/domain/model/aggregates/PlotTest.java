package lat.soulware.sgt.plots.domain.model.aggregates;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.plots.domain.model.exceptions.BeneficiaryAlreadyHoldsPlotException;
import lat.soulware.sgt.plots.domain.model.exceptions.MissingPlotOwnerException;
import lat.soulware.sgt.plots.domain.model.exceptions.MissingPlotUbigeoException;
import lat.soulware.sgt.plots.domain.model.exceptions.OverlappingPlotException;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.plots.domain.model.valueobjects.SharedArea;
import lat.soulware.sgt.plots.domain.model.valueobjects.SurveyPoint;
import lat.soulware.sgt.plots.domain.model.valueobjects.Ubigeo;
import lat.soulware.sgt.plots.domain.model.valueobjects.UtmZone;
import lat.soulware.sgt.plots.domain.services.PlotGeometry;

/**
 * Covers what a plot refuses to be registered without, the one-plot rule, and the overlap rule on
 * their own. The geometry is a stub answering a fixed shared area per
 * neighbour, so what is under test is the comparison against the tolerance and which neighbours the
 * failure names, with no polygon arithmetic involved.
 */
class PlotTest {

    private static final PlotBoundary CANDIDATE = square(0);
    private static final LocalDateTime REGISTERED = LocalDateTime.of(2026, 10, 1, 9, 0);
    private static final Ubigeo DISTRICT = new Ubigeo("150101");
    private static final BeneficiaryLegalDocument OWNER = new BeneficiaryLegalDocument(LegalDocumentType.DNI, "12345678");

    @Test
    void registersWithNoNeighbours() {
        Plot plot = Plot.register(newId(), OWNER, CANDIDATE, DISTRICT, REGISTERED, Optional.empty(), List.of(), stubSharing(Map.of()));

        assertEquals(CANDIDATE, plot.getBoundary());
        assertEquals(OWNER, plot.getOwner());
    }

    @Test
    void refusesAPlotRegisteredToNobody() {
        assertThrows(
            MissingPlotOwnerException.class,
            () -> Plot.register(newId(), null, CANDIDATE, DISTRICT, REGISTERED, Optional.empty(), List.of(), stubSharing(Map.of()))
        );
    }

    @Test
    void refusesAPlotInNoDistrict() {
        assertThrows(
            MissingPlotUbigeoException.class,
            () -> Plot.register(newId(), OWNER, CANDIDATE, null, REGISTERED, Optional.empty(), List.of(), stubSharing(Map.of()))
        );
    }

    @Test
    void refusesASecondPlotForTheSameBeneficiary() {
        Plot held = stored(1);

        BeneficiaryAlreadyHoldsPlotException failure = assertThrows(
            BeneficiaryAlreadyHoldsPlotException.class,
            () -> Plot.register(newId(), OWNER, CANDIDATE, DISTRICT, REGISTERED, Optional.of(held), List.of(), stubSharing(Map.of()))
        );

        assertArrayEquals(new Object[] {OWNER, held.getId()}, failure.getMessageArgs());
    }

    @Test
    void acceptsANeighbourSharingExactlyTheTolerance() {
        Plot neighbour = stored(1);
        PlotGeometry geometry = stubSharing(Map.of(neighbour.getBoundary(), Plot.OVERLAP_TOLERANCE.squareMeters()));

        Plot plot = Plot.register(newId(), OWNER, CANDIDATE, DISTRICT, REGISTERED, Optional.empty(), List.of(neighbour), geometry);

        assertEquals(CANDIDATE, plot.getBoundary());
    }

    @Test
    void namesOnlyTheNeighboursSharingMoreThanTheTolerance() {
        Plot touching = stored(1);
        Plot overlapping = stored(2);
        BigDecimal justOver = Plot.OVERLAP_TOLERANCE.squareMeters().add(new BigDecimal("0.01"));
        PlotGeometry geometry = stubSharing(Map.of(
            touching.getBoundary(), BigDecimal.ZERO,
            overlapping.getBoundary(), justOver
        ));

        OverlappingPlotException failure = assertThrows(
            OverlappingPlotException.class,
            () -> Plot.register(newId(), OWNER, CANDIDATE, DISTRICT, REGISTERED, Optional.empty(), List.of(touching, overlapping), geometry)
        );

        assertArrayEquals(new Object[] {Set.of(overlapping.getId())}, failure.getMessageArgs());
    }

    /** Answers the area listed for the second boundary, and zero for any boundary not listed. */
    private static PlotGeometry stubSharing(Map<PlotBoundary, BigDecimal> sharedWith) {
        return (first, second) -> new SharedArea(sharedWith.getOrDefault(second, BigDecimal.ZERO));
    }

    private static Plot stored(int offset) {
        return Plot.reconstitute(newId(), OWNER, square(offset), DISTRICT, REGISTERED);
    }

    private static PlotId newId() {
        return new PlotId(UUID.randomUUID());
    }

    /** A distinct unit square per offset, so each neighbour has its own boundary to stub against. */
    private static PlotBoundary square(int offset) {
        return new PlotBoundary(UtmZone.ZONE_18S, List.of(
            new SurveyPoint(offset, 0),
            new SurveyPoint(offset + 1, 0),
            new SurveyPoint(offset + 1, 1),
            new SurveyPoint(offset, 1)
        ));
    }
}

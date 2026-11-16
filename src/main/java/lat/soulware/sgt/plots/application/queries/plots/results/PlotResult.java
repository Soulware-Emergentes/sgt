package lat.soulware.sgt.plots.application.queries.plots.results;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * A registered plot.
 *
 * @param id                  the plot's identity
 * @param ownerDocumentType   the kind of its owner's legal document, by name
 * @param ownerDocumentNumber its owner's document number
 * @param ubigeo              the district it lies in
 * @param utmZone             the UTM zone its boundary was surveyed in, by name
 * @param boundary            the boundary's vertices in order, the ring left open
 * @param registrationDate    when it was registered
 */
public record PlotResult(
    UUID id,
    String ownerDocumentType,
    String ownerDocumentNumber,
    String ubigeo,
    String utmZone,
    List<VertexResult> boundary,
    LocalDateTime registrationDate
) implements QueryResult {

    public PlotResult {
        boundary = List.copyOf(boundary);
    }
}

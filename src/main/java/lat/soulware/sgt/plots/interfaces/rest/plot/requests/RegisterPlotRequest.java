package lat.soulware.sgt.plots.interfaces.rest.plot.requests;

import java.util.List;

/**
 * A surveyed plot registered to a beneficiary.
 *
 * @param ownerDocumentType   the kind of the beneficiary's legal document, by name
 * @param ownerDocumentNumber the beneficiary's document number
 * @param ubigeo              the district the plot lies in
 * @param utmZone             the UTM zone the boundary was surveyed in, by name
 * @param boundary            the perimeter's vertices in order
 */
public record RegisterPlotRequest(
    String ownerDocumentType,
    String ownerDocumentNumber,
    String ubigeo,
    String utmZone,
    List<VertexRequest> boundary
) {
}

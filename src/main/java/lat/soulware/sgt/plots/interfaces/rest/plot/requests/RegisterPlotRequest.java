package lat.soulware.sgt.plots.interfaces.rest.plot.requests;

import java.util.List;

/**
 * A surveyed plot registered to a beneficiary. The district it lies in is not sent; sgt works it
 * out from the boundary.
 *
 * @param ownerDocumentType   the kind of the beneficiary's legal document, by name
 * @param ownerDocumentNumber the beneficiary's document number
 * @param utmZone             the UTM zone the boundary was surveyed in, by name
 * @param boundary            the perimeter's vertices in order
 */
public record RegisterPlotRequest(
    String ownerDocumentType,
    String ownerDocumentNumber,
    String utmZone,
    List<VertexRequest> boundary
) {
}

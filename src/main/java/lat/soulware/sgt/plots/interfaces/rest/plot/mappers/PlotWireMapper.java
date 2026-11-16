package lat.soulware.sgt.plots.interfaces.rest.plot.mappers;

import java.util.List;
import java.util.UUID;

import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.plots.application.commands.plot.intent.RegisterPlot;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotById;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotOfOwner;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotsInRegion;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotsInView;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotResult;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotsInViewResult;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotsResult;
import lat.soulware.sgt.plots.domain.model.exceptions.InvalidBoundingBoxException;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.SurveyPoint;
import lat.soulware.sgt.plots.domain.model.valueobjects.UtmZone;
import lat.soulware.sgt.plots.interfaces.rest.plot.requests.RegisterPlotRequest;
import lat.soulware.sgt.plots.interfaces.rest.plot.responses.PlotFeatureCollectionResponse;
import lat.soulware.sgt.plots.interfaces.rest.plot.responses.PlotFeatureResponse;
import lat.soulware.sgt.plots.interfaces.rest.plot.responses.PlotPropertiesResponse;
import lat.soulware.sgt.plots.interfaces.rest.plot.responses.PlotResponse;
import lat.soulware.sgt.plots.interfaces.rest.plot.responses.PolygonGeometryResponse;
import lat.soulware.sgt.plots.interfaces.rest.plot.responses.PlotsResponse;
import lat.soulware.sgt.plots.interfaces.rest.plot.responses.VertexResponse;
import lat.soulware.sgt.shared.interfaces.rest.mappers.WireMapper;
import lat.soulware.sgt.shared.interfaces.rest.security.Caller;
import lat.soulware.sgt.shared.interfaces.rest.security.Role;

/**
 * Converts what {@code PlotController} receives and answers. Every plot operation is a field
 * agent's, so each conversion first refuses a caller not acting as one; an admin always does.
 */
public final class PlotWireMapper implements WireMapper {

    private PlotWireMapper() {
    }

    public static RegisterPlot toRegisterPlot(RegisterPlotRequest request, Caller caller) {
        caller.actingAs(Role.FIELD_AGENT);
        List<SurveyPoint> vertices = request.boundary().stream()
            .map(vertex -> new SurveyPoint(vertex.easting(), vertex.northing()))
            .toList();

        return new RegisterPlot(
            new BeneficiaryLegalDocument(
                LegalDocumentType.valueOf(request.ownerDocumentType()),
                request.ownerDocumentNumber()
            ),
            new PlotBoundary(UtmZone.valueOf(request.utmZone()), vertices)
        );
    }

    public static PlotById toPlotById(UUID plotId, Caller caller) {
        caller.actingAs(Role.FIELD_AGENT);

        return new PlotById(plotId);
    }

    public static PlotsInRegion toPlotsInRegion(String region, Caller caller) {
        caller.actingAs(Role.FIELD_AGENT);

        return new PlotsInRegion(region);
    }

    public static PlotOfOwner toPlotOfOwner(String documentType, String documentNumber, Caller caller) {
        caller.actingAs(Role.FIELD_AGENT);

        return new PlotOfOwner(documentType, documentNumber);
    }

    /**
     * @param boundingBox west, south, east and north in degrees, comma separated, the order GeoJSON
     *                    and OGC services write a bounding box in
     * @throws InvalidBoundingBoxException if it is not four numbers
     */
    public static PlotsInView toPlotsInView(String boundingBox, Caller caller) {
        caller.actingAs(Role.FIELD_AGENT);
        String[] edges = boundingBox.split(",");
        boolean namesFourEdges = edges.length == 4;
        if (!namesFourEdges) {
            throw new InvalidBoundingBoxException(boundingBox);
        }

        try {
            return new PlotsInView(
                Double.parseDouble(edges[0].strip()),
                Double.parseDouble(edges[1].strip()),
                Double.parseDouble(edges[2].strip()),
                Double.parseDouble(edges[3].strip())
            );
        } catch (NumberFormatException e) {
            throw new InvalidBoundingBoxException(boundingBox);
        }
    }

    public static PlotFeatureCollectionResponse toPlotFeatureCollectionResponse(PlotsInViewResult result) {
        List<PlotFeatureResponse> features = result.plots().stream()
            .map(plot -> new PlotFeatureResponse(
                PlotFeatureResponse.TYPE,
                plot.id(),
                new PolygonGeometryResponse(
                    PolygonGeometryResponse.TYPE,
                    List.of(plot.footprint().stream()
                        .map(position -> List.of(position.longitude(), position.latitude()))
                        .toList())
                ),
                new PlotPropertiesResponse(plot.ubigeo())
            ))
            .toList();

        return new PlotFeatureCollectionResponse(PlotFeatureCollectionResponse.TYPE, features, result.truncated());
    }

    public static PlotsResponse toPlotsResponse(PlotsResult result) {
        return new PlotsResponse(result.plots().stream()
            .map(PlotWireMapper::toPlotResponse)
            .toList()
        );
    }

    public static PlotResponse toPlotResponse(PlotResult result) {
        List<VertexResponse> boundary = result.boundary().stream()
            .map(vertex -> new VertexResponse(vertex.easting(), vertex.northing()))
            .toList();

        return new PlotResponse(
            result.id(),
            result.ownerDocumentType(),
            result.ownerDocumentNumber(),
            result.ubigeo(),
            result.utmZone(),
            boundary,
            result.registrationDate()
        );
    }
}

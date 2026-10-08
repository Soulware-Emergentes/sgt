package lat.soulware.sgt.plots.interfaces.rest.plot.mappers;

import java.util.List;
import java.util.UUID;

import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.plots.application.commands.plot.intent.RegisterPlot;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotById;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotOfOwner;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotsInRegion;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotResult;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotsResult;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.SurveyPoint;
import lat.soulware.sgt.plots.domain.model.valueobjects.Ubigeo;
import lat.soulware.sgt.plots.domain.model.valueobjects.UtmZone;
import lat.soulware.sgt.plots.interfaces.rest.plot.requests.RegisterPlotRequest;
import lat.soulware.sgt.plots.interfaces.rest.plot.responses.PlotResponse;
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
            new PlotBoundary(UtmZone.valueOf(request.utmZone()), vertices),
            new Ubigeo(request.ubigeo())
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

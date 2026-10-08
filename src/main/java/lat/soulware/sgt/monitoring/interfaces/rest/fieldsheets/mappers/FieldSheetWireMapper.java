package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.mappers;

import java.util.List;
import java.util.UUID;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ApproveFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RejectFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RelinkFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.SubmitFieldSheet;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetById;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetsPage;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanOfFieldSheet;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanUploadSlot;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetsPageResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.ScanLinkResult;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.EvaluationDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.MeasurementUnit;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.Quantity;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SecurementDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.Supply;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SupplyName;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.ApproveFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.RelinkFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.SubmitFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.SupplyRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetSummaryResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetsPageResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.ScanLinkResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.SupplyResponse;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentId;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantId;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.shared.interfaces.rest.mappers.WireMapper;
import lat.soulware.sgt.shared.interfaces.rest.security.Caller;
import lat.soulware.sgt.shared.interfaces.rest.security.Role;

/** Converts what {@code FieldSheetController} receives and answers. */
public final class FieldSheetWireMapper implements WireMapper {

    private FieldSheetWireMapper() {
    }

    public static SubmitFieldSheet toSubmitFieldSheet(SubmitFieldSheetRequest request, Caller caller) {
        return new SubmitFieldSheet(
            new TaskCode(request.taskCode()),
            new ScanKey(request.scanKey()),
            new FieldAgentId(caller.actingAs(Role.FIELD_AGENT))
        );
    }

    public static ApproveFieldSheet toApproveFieldSheet(
        UUID fieldSheetId,
        ApproveFieldSheetRequest request,
        Caller caller
    ) {
        GeneralManagementAssistantId reviewer = new GeneralManagementAssistantId(
            caller.actingAs(Role.GENERAL_MANAGEMENT_ASSISTANT)
        );
        boolean statesSecurement = request.securementDate() != null;
        SecurementDate securementDate = statesSecurement
            ? new SecurementDate(request.securementDate())
            : null;
        List<Supply> usedSupplies = request.usedSupplies().stream()
            .map(FieldSheetWireMapper::toSupply)
            .toList();

        return new ApproveFieldSheet(
            new FieldSheetId(fieldSheetId),
            reviewer,
            new BeneficiaryLegalDocument(
                LegalDocumentType.valueOf(request.beneficiaryDocumentType()),
                request.beneficiaryDocumentNumber()
            ),
            usedSupplies,
            new EvaluationDate(request.evaluationDate()),
            securementDate
        );
    }

    public static RejectFieldSheet toRejectFieldSheet(UUID fieldSheetId, Caller caller) {
        return new RejectFieldSheet(
            new FieldSheetId(fieldSheetId),
            new GeneralManagementAssistantId(caller.actingAs(Role.GENERAL_MANAGEMENT_ASSISTANT))
        );
    }

    public static RelinkFieldSheet toRelinkFieldSheet(UUID fieldSheetId, RelinkFieldSheetRequest request) {
        return new RelinkFieldSheet(new FieldSheetId(fieldSheetId), new TaskCode(request.taskCode()));
    }

    /** Blank filters, as a table sends an emptied field, match everything like absent ones. */
    public static FieldSheetsPage toFieldSheetsPage(
        String status,
        String code,
        String taskCode,
        String submittedFrom,
        String submittedTo,
        String sort,
        String direction,
        int page,
        int size
    ) {
        return new FieldSheetsPage(
            FieldSheetWireMapper.blankToNull(status),
            FieldSheetWireMapper.blankToNull(code),
            FieldSheetWireMapper.blankToNull(taskCode),
            FieldSheetWireMapper.blankToNull(submittedFrom),
            FieldSheetWireMapper.blankToNull(submittedTo),
            sort,
            direction,
            page,
            size
        );
    }

    public static FieldSheetById toFieldSheetById(UUID fieldSheetId) {
        return new FieldSheetById(fieldSheetId);
    }

    public static ScanOfFieldSheet toScanOfFieldSheet(UUID fieldSheetId) {
        return new ScanOfFieldSheet(fieldSheetId);
    }

    public static ScanUploadSlot toScanUploadSlot() {
        return new ScanUploadSlot();
    }

    public static FieldSheetsPageResponse toFieldSheetsPageResponse(FieldSheetsPageResult result) {
        List<FieldSheetSummaryResponse> content = result.content().stream()
            .map(fieldSheet -> new FieldSheetSummaryResponse(
                fieldSheet.id(),
                fieldSheet.code(),
                fieldSheet.taskCode(),
                fieldSheet.status(),
                fieldSheet.submittedBy(),
                fieldSheet.submissionDate()
            ))
            .toList();

        return new FieldSheetsPageResponse(
            content,
            result.totalPages(),
            result.totalElements(),
            result.actualPage(),
            result.pageSize()
        );
    }

    public static FieldSheetResponse toFieldSheetResponse(FieldSheetResult result) {
        List<SupplyResponse> usedSupplies = result.usedSupplies().stream()
            .map(supply -> new SupplyResponse(supply.name(), supply.amount(), supply.unit()))
            .toList();

        return new FieldSheetResponse(
            result.id(),
            result.code(),
            result.taskCode(),
            result.status(),
            result.beneficiaryDocumentType(),
            result.beneficiaryDocumentNumber(),
            usedSupplies,
            result.evaluationDate(),
            result.securementDate(),
            result.submittedBy(),
            result.submissionDate(),
            result.reviewedBy(),
            result.reviewDate()
        );
    }

    public static ScanLinkResponse toScanLinkResponse(ScanLinkResult result) {
        return new ScanLinkResponse(result.key(), result.url(), result.expiresAt());
    }

    private static String blankToNull(String value) {
        boolean isBlank = value == null || value.isBlank();
        return isBlank ? null : value;
    }

    private static Supply toSupply(SupplyRequest request) {
        return new Supply(
            new SupplyName(request.name()),
            new Quantity(request.amount(), MeasurementUnit.valueOf(request.unit()))
        );
    }
}

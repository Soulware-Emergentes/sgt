package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.mappers;

import java.util.List;
import java.util.UUID;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.ApproveFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RejectFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.RelinkFieldSheet;
import lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent.SubmitFieldSheet;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetById;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetsInStatus;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.PendingTasks;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanOfFieldSheet;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanUploadSlot;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.TaskByCode;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetsResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.PendingTasksResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.ScanLinkResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.TaskResult;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.EvaluationDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.MeasurementUnit;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.Quantity;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SecurementDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.Supply;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SupplyName;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.ApproveFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.RejectFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.RelinkFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.SubmitFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.SupplyRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetCreatedResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetSummaryResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetsResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.PendingTaskResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.PendingTasksResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.ScanLinkResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.SupplyResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.TaskResponse;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentId;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantId;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.shared.interfaces.rest.mappers.WireMapper;

/** Converts what {@code FieldSheetController} receives and answers. */
public final class FieldSheetWireMapper implements WireMapper {

    private FieldSheetWireMapper() {
    }

    public static SubmitFieldSheet toSubmitFieldSheet(SubmitFieldSheetRequest request) {
        return new SubmitFieldSheet(
            new TaskCode(request.taskCode()),
            new ScanKey(request.scanKey()),
            new FieldAgentId(request.submittedBy())
        );
    }

    public static ApproveFieldSheet toApproveFieldSheet(UUID fieldSheetId, ApproveFieldSheetRequest request) {
        boolean statesSecurement = request.securementDate() != null;
        SecurementDate securementDate = statesSecurement
            ? new SecurementDate(request.securementDate())
            : null;
        List<Supply> usedSupplies = request.usedSupplies().stream()
            .map(FieldSheetWireMapper::toSupply)
            .toList();

        return new ApproveFieldSheet(
            new FieldSheetId(fieldSheetId),
            new GeneralManagementAssistantId(request.reviewer()),
            new BeneficiaryLegalDocument(
                LegalDocumentType.valueOf(request.beneficiaryDocumentType()),
                request.beneficiaryDocumentNumber()
            ),
            usedSupplies,
            new EvaluationDate(request.evaluationDate()),
            securementDate
        );
    }

    public static RejectFieldSheet toRejectFieldSheet(UUID fieldSheetId, RejectFieldSheetRequest request) {
        return new RejectFieldSheet(new FieldSheetId(fieldSheetId), new GeneralManagementAssistantId(request.reviewer()));
    }

    public static RelinkFieldSheet toRelinkFieldSheet(UUID fieldSheetId, RelinkFieldSheetRequest request) {
        return new RelinkFieldSheet(new FieldSheetId(fieldSheetId), new TaskCode(request.taskCode()));
    }

    public static FieldSheetsInStatus toFieldSheetsInStatus(String status) {
        return new FieldSheetsInStatus(status);
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

    public static TaskByCode toTaskByCode(String taskCode) {
        return new TaskByCode(taskCode);
    }

    public static PendingTasks toPendingTasks(String activityCode) {
        return new PendingTasks(activityCode);
    }

    public static FieldSheetCreatedResponse toCreatedResponse(FieldSheetId fieldSheetId) {
        return new FieldSheetCreatedResponse(fieldSheetId.value());
    }

    public static FieldSheetsResponse toFieldSheetsResponse(FieldSheetsResult result) {
        return new FieldSheetsResponse(result.fieldSheets().stream()
            .map(fieldSheet -> new FieldSheetSummaryResponse(
                fieldSheet.id(),
                fieldSheet.taskCode(),
                fieldSheet.status(),
                fieldSheet.submittedBy(),
                fieldSheet.submissionDate()
            ))
            .toList()
        );
    }

    public static FieldSheetResponse toFieldSheetResponse(FieldSheetResult result) {
        List<SupplyResponse> usedSupplies = result.usedSupplies().stream()
            .map(supply -> new SupplyResponse(supply.name(), supply.amount(), supply.unit()))
            .toList();

        return new FieldSheetResponse(
            result.id(),
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

    public static TaskResponse toTaskResponse(TaskResult result) {
        return new TaskResponse(result.code(), result.description(), result.status(), result.beneficiaryId());
    }

    public static PendingTasksResponse toPendingTasksResponse(PendingTasksResult result) {
        return new PendingTasksResponse(result.tasks().stream()
            .map(task -> new PendingTaskResponse(
                task.code(),
                task.description(),
                task.activityCode(),
                task.activityDescription()
            ))
            .toList()
        );
    }

    private static Supply toSupply(SupplyRequest request) {
        return new Supply(
            new SupplyName(request.name()),
            new Quantity(request.amount(), MeasurementUnit.valueOf(request.unit()))
        );
    }
}

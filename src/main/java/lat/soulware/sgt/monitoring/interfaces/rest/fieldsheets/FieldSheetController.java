package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lat.soulware.sgt.monitoring.application.commands.fieldsheets.FieldSheetCommandService;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.FieldSheetQueryService;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.mappers.FieldSheetWireMapper;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.ApproveFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.RelinkFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.SubmitFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetsPageResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.ScanLinkResponse;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.interfaces.rest.security.Caller;

/**
 * Field sheets, from the field agent's upload to the assistant's review. The scanned PDF goes
 * straight to the object store through a signed link; the field sheet is then submitted with the
 * key it was uploaded under.
 *
 * <p>Submitting, approving and rejecting take the {@link Caller} the request's token names. Who submits
 * or reviews is that caller, never a field of the body, refused when not acting in the role recorded.
 *
 * <p>Submitting answers with the field sheet it created, and approving, rejecting and relinking with
 * the field sheet as it stands afterward.
 */
@RestController
@RequestMapping("/api/v1")
public class FieldSheetController {

    private final FieldSheetCommandService fieldSheetCommandService;
    private final FieldSheetQueryService fieldSheetQueryService;

    public FieldSheetController(
        FieldSheetCommandService fieldSheetCommandService,
        FieldSheetQueryService fieldSheetQueryService
    ) {
        this.fieldSheetCommandService = fieldSheetCommandService;
        this.fieldSheetQueryService = fieldSheetQueryService;
    }

    @PostMapping("/field-sheets/scan-uploads")
    public ScanLinkResponse scanUpload() {
        return FieldSheetWireMapper.toScanLinkResponse(
            this.fieldSheetQueryService.uploadSlot(FieldSheetWireMapper.toScanUploadSlot())
        );
    }

    @PostMapping("/field-sheets")
    @ResponseStatus(HttpStatus.CREATED)
    public FieldSheetResponse submit(@RequestBody SubmitFieldSheetRequest request, Caller caller) {
        FieldSheetId submitted = this.fieldSheetCommandService.submit(
            FieldSheetWireMapper.toSubmitFieldSheet(request, caller)
        );

        return this.fieldSheet(submitted.value());
    }

    /**
     * One page of field sheets for a table: every filter optional, newest submission first unless
     * sorted otherwise.
     */
    @GetMapping("/field-sheets")
    public FieldSheetsPageResponse page(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String code,
        @RequestParam(required = false) String taskCode,
        @RequestParam(required = false) String submittedFrom,
        @RequestParam(required = false) String submittedTo,
        @RequestParam(defaultValue = "submittedAt") String sort,
        @RequestParam(defaultValue = "desc") String direction,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return FieldSheetWireMapper.toFieldSheetsPageResponse(this.fieldSheetQueryService.page(
            FieldSheetWireMapper.toFieldSheetsPage(
                status,
                code,
                taskCode,
                submittedFrom,
                submittedTo,
                sort,
                direction,
                page,
                size
            )
        ));
    }

    @GetMapping("/field-sheets/{fieldSheetId}")
    public FieldSheetResponse fieldSheet(@PathVariable UUID fieldSheetId) {
        return FieldSheetWireMapper.toFieldSheetResponse(
            this.fieldSheetQueryService.byId(FieldSheetWireMapper.toFieldSheetById(fieldSheetId))
        );
    }

    @GetMapping("/field-sheets/{fieldSheetId}/scan")
    public ScanLinkResponse scan(@PathVariable UUID fieldSheetId) {
        return FieldSheetWireMapper.toScanLinkResponse(
            this.fieldSheetQueryService.scan(FieldSheetWireMapper.toScanOfFieldSheet(fieldSheetId))
        );
    }

    @PostMapping("/field-sheets/{fieldSheetId}/approval")
    public FieldSheetResponse approve(
        @PathVariable UUID fieldSheetId,
        @RequestBody ApproveFieldSheetRequest request,
        Caller caller
    ) {
        this.fieldSheetCommandService.approve(FieldSheetWireMapper.toApproveFieldSheet(fieldSheetId, request, caller));

        return this.fieldSheet(fieldSheetId);
    }

    @PostMapping("/field-sheets/{fieldSheetId}/rejection")
    public FieldSheetResponse reject(@PathVariable UUID fieldSheetId, Caller caller) {
        this.fieldSheetCommandService.reject(FieldSheetWireMapper.toRejectFieldSheet(fieldSheetId, caller));

        return this.fieldSheet(fieldSheetId);
    }

    @PostMapping("/field-sheets/{fieldSheetId}/relink")
    public FieldSheetResponse relink(
        @PathVariable UUID fieldSheetId,
        @RequestBody RelinkFieldSheetRequest request
    ) {
        this.fieldSheetCommandService.relink(FieldSheetWireMapper.toRelinkFieldSheet(fieldSheetId, request));

        return this.fieldSheet(fieldSheetId);
    }
}

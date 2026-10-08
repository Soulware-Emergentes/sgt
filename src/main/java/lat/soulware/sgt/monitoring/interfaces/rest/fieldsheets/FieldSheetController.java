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
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.RejectFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.RelinkFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests.SubmitFieldSheetRequest;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetCreatedResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.FieldSheetsResponse;
import lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses.ScanLinkResponse;

/**
 * Field sheets, from the field agent's upload to the assistant's review. The scanned PDF goes
 * straight to the object store through a signed link; the field sheet is then submitted with the
 * key it was uploaded under.
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
    public FieldSheetCreatedResponse submit(@RequestBody SubmitFieldSheetRequest request) {
        return FieldSheetWireMapper.toCreatedResponse(
            this.fieldSheetCommandService.submit(FieldSheetWireMapper.toSubmitFieldSheet(request))
        );
    }

    @GetMapping("/field-sheets")
    public FieldSheetsResponse inStatus(@RequestParam String status) {
        return FieldSheetWireMapper.toFieldSheetsResponse(
            this.fieldSheetQueryService.inStatus(FieldSheetWireMapper.toFieldSheetsInStatus(status))
        );
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(@PathVariable UUID fieldSheetId, @RequestBody ApproveFieldSheetRequest request) {
        this.fieldSheetCommandService.approve(FieldSheetWireMapper.toApproveFieldSheet(fieldSheetId, request));
    }

    @PostMapping("/field-sheets/{fieldSheetId}/rejection")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable UUID fieldSheetId, @RequestBody RejectFieldSheetRequest request) {
        this.fieldSheetCommandService.reject(FieldSheetWireMapper.toRejectFieldSheet(fieldSheetId, request));
    }

    @PostMapping("/field-sheets/{fieldSheetId}/relink")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void relink(@PathVariable UUID fieldSheetId, @RequestBody RelinkFieldSheetRequest request) {
        this.fieldSheetCommandService.relink(FieldSheetWireMapper.toRelinkFieldSheet(fieldSheetId, request));
    }
}

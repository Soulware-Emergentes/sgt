package lat.soulware.sgt.monitoring.application.queries.fieldsheets;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetById;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetsInStatus;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.PendingTasks;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanOfFieldSheet;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanUploadSlot;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.TaskByCode;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections.FieldSheetProjection;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections.ScanLinkProjection;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections.TaskProjection;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetsResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.PendingTasksResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.ScanLinkResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.TaskResult;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNotFoundException;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.queries.QueryService;

/** Answers the reads about field sheets, their scans, and the poi tasks they are submitted for. */
@Service
@Transactional(readOnly = true)
public class FieldSheetQueryService implements QueryService {

    private final FieldSheetProjection fieldSheetProjection;
    private final ScanLinkProjection scanLinkProjection;
    private final TaskProjection taskProjection;

    public FieldSheetQueryService(
        FieldSheetProjection fieldSheetProjection,
        ScanLinkProjection scanLinkProjection,
        TaskProjection taskProjection
    ) {
        this.fieldSheetProjection = fieldSheetProjection;
        this.scanLinkProjection = scanLinkProjection;
        this.taskProjection = taskProjection;
    }

    public FieldSheetsResult inStatus(FieldSheetsInStatus criteria) {
        return new FieldSheetsResult(this.fieldSheetProjection.findInStatus(criteria));
    }

    public FieldSheetResult byId(FieldSheetById criteria) {
        return this.fieldSheetProjection.findById(criteria)
            .orElseThrow(() -> this.notFound(criteria.id()));
    }

    public ScanLinkResult uploadSlot(ScanUploadSlot criteria) {
        return this.scanLinkProjection.uploadLink(criteria);
    }

    public ScanLinkResult scan(ScanOfFieldSheet criteria) {
        String key = this.fieldSheetProjection.findScanKey(criteria)
            .orElseThrow(() -> this.notFound(criteria.fieldSheetId()));

        return this.scanLinkProjection.downloadLink(key);
    }

    public TaskResult task(TaskByCode criteria) {
        return this.taskProjection.findByCode(criteria);
    }

    public PendingTasksResult pendingTasks(PendingTasks criteria) {
        return new PendingTasksResult(this.taskProjection.findPending(criteria));
    }

    private FieldSheetNotFoundException notFound(UUID id) {
        return new FieldSheetNotFoundException(List.of(new FieldSheetId(id)));
    }
}

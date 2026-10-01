package lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections;

import java.util.List;
import java.util.Optional;

import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetById;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetsInStatus;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanOfFieldSheet;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetSummaryResult;
import lat.soulware.sgt.shared.application.queries.projections.Projection;

/** The field sheets the read store holds. */
public interface FieldSheetProjection extends Projection {

    /**
     * @param criteria the step of the review to list
     * @return the field sheets at that step, oldest submission first
     */
    List<FieldSheetSummaryResult> findInStatus(FieldSheetsInStatus criteria);

    /**
     * @param criteria the field sheet to read
     * @return the field sheet, empty when none has that identity
     */
    Optional<FieldSheetResult> findById(FieldSheetById criteria);

    /**
     * @param criteria the field sheet whose scan to find
     * @return where its scanned PDF sits, empty when no field sheet has that identity
     */
    Optional<String> findScanKey(ScanOfFieldSheet criteria);
}

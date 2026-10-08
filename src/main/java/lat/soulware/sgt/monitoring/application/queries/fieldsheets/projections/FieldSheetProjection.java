package lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections;

import java.util.Optional;

import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetById;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetsPage;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanOfFieldSheet;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetsPageResult;
import lat.soulware.sgt.shared.application.queries.projections.Projection;

/** The field sheets the read store holds. */
public interface FieldSheetProjection extends Projection {

    /**
     * @param criteria the page to read, its filters, sort and bounds already checked
     * @return the field sheets on it, with the size of the whole selection
     */
    FieldSheetsPageResult findPage(FieldSheetsPage criteria);

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

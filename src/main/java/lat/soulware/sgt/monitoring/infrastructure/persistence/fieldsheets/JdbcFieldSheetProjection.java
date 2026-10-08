package lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetById;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetsInStatus;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanOfFieldSheet;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections.FieldSheetProjection;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetSummaryResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.SupplyResult;

/** Reads field sheets straight from their tables. */
@Component
public class JdbcFieldSheetProjection implements FieldSheetProjection {

    private static final String FIND_IN_STATUS = """
        SELECT id, code, task_code, status, submitted_by, submission_date
        FROM field_sheets
        WHERE status = :status
        ORDER BY submission_date""";

    private static final String FIND_BY_ID = """
        SELECT id, code, task_code, status, beneficiary_document_type, beneficiary_document_number,
            evaluation_date, securement_date, submitted_by, submission_date, reviewed_by, review_date
        FROM field_sheets
        WHERE id = :id""";

    private static final String FIND_SUPPLIES = """
        SELECT name, amount, unit
        FROM field_sheet_supplies
        WHERE field_sheet_id = :id
        ORDER BY position""";

    private static final String FIND_SCAN_KEY = """
        SELECT scan_key
        FROM field_sheets
        WHERE id = :id""";

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcFieldSheetProjection(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<FieldSheetSummaryResult> findInStatus(FieldSheetsInStatus criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("status", criteria.status());

        return this.jdbc.query(FIND_IN_STATUS, parameters, (row, rowNumber) -> new FieldSheetSummaryResult(
            row.getObject("id", UUID.class),
            row.getString("code"),
            row.getString("task_code"),
            row.getString("status"),
            row.getObject("submitted_by", UUID.class),
            row.getObject("submission_date", LocalDateTime.class)
        ));
    }

    @Override
    public Optional<FieldSheetResult> findById(FieldSheetById criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", criteria.id());
        List<SupplyResult> supplies = this.jdbc.query(FIND_SUPPLIES, parameters, (row, rowNumber) -> new SupplyResult(
            row.getString("name"),
            row.getBigDecimal("amount"),
            row.getString("unit")
        ));

        return this.jdbc.query(FIND_BY_ID, parameters, (row, rowNumber) -> this.toResult(row, supplies))
            .stream()
            .findFirst();
    }

    @Override
    public Optional<String> findScanKey(ScanOfFieldSheet criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", criteria.fieldSheetId());

        return this.jdbc.queryForList(FIND_SCAN_KEY, parameters, String.class)
            .stream()
            .findFirst();
    }

    private FieldSheetResult toResult(ResultSet row, List<SupplyResult> supplies) throws SQLException {
        return new FieldSheetResult(
            row.getObject("id", UUID.class),
            row.getString("code"),
            row.getString("task_code"),
            row.getString("status"),
            row.getString("beneficiary_document_type"),
            row.getString("beneficiary_document_number"),
            supplies,
            row.getObject("evaluation_date", LocalDateTime.class),
            row.getObject("securement_date", LocalDateTime.class),
            row.getObject("submitted_by", UUID.class),
            row.getObject("submission_date", LocalDateTime.class),
            row.getObject("reviewed_by", UUID.class),
            row.getObject("review_date", LocalDateTime.class)
        );
    }
}

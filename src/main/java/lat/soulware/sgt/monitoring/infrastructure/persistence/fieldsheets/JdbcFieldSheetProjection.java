package lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetById;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetsPage;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanOfFieldSheet;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections.FieldSheetProjection;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetSummaryResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetsPageResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.SupplyResult;

/** Reads field sheets straight from their tables. */
@Component
public class JdbcFieldSheetProjection implements FieldSheetProjection {

    private static final String MATCHING = """
        FROM field_sheets
        WHERE (CAST(:status AS VARCHAR) IS NULL OR status = :status)
            AND (CAST(:code AS VARCHAR) IS NULL OR lower(code) LIKE '%' || lower(:code) || '%')
            AND (CAST(:taskCode AS VARCHAR) IS NULL OR task_code LIKE '%' || :taskCode || '%')
            AND (CAST(:submittedFrom AS DATE) IS NULL OR submission_date >= CAST(:submittedFrom AS DATE))
            AND (CAST(:submittedTo AS DATE) IS NULL OR submission_date < CAST(:submittedTo AS DATE) + 1)""";

    private static final String COUNT_PAGE = "SELECT count(*) " + MATCHING;

    private static final String FIND_PAGE = """
        SELECT id, code, task_code, status, submitted_by, submission_date
        %s
        ORDER BY %s, id
        LIMIT :size OFFSET :offset""";

    /**
     * What each sortable column orders by, with {@code %1$s} standing for the direction. A code orders
     * by its year and then its number, the parts {@code FI-NNNN-YYYY} holds at fixed positions.
     */
    private static final Map<String, String> ORDER_BY = Map.of(
        "code", "substring(code, 9, 4) %1$s, substring(code, 4, 4) %1$s",
        "taskCode", "task_code %1$s",
        "submittedAt", "submission_date %1$s",
        "status", "status %1$s"
    );

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
    public FieldSheetsPageResult findPage(FieldSheetsPage criteria) {
        boolean isDescending = "desc".equalsIgnoreCase(criteria.direction());
        String direction = isDescending ? "DESC" : "ASC";
        String orderBy = ORDER_BY.get(criteria.sort()).formatted(direction);
        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("status", criteria.status())
            .addValue("code", criteria.code())
            .addValue("taskCode", criteria.taskCode())
            .addValue("submittedFrom", criteria.submittedFrom())
            .addValue("submittedTo", criteria.submittedTo())
            .addValue("size", criteria.size())
            .addValue("offset", (long) criteria.page() * criteria.size());

        long totalElements = this.jdbc.queryForObject(COUNT_PAGE, parameters, Long.class);
        List<FieldSheetSummaryResult> content = this.jdbc.query(
            FIND_PAGE.formatted(MATCHING, orderBy),
            parameters,
            (row, rowNumber) -> JdbcFieldSheetProjection.toSummary(row)
        );
        int totalPages = (int) Math.ceilDiv(totalElements, criteria.size());

        return new FieldSheetsPageResult(content, totalPages, totalElements, criteria.page(), criteria.size());
    }

    private static FieldSheetSummaryResult toSummary(ResultSet row) throws SQLException {
        return new FieldSheetSummaryResult(
            row.getObject("id", UUID.class),
            row.getString("code"),
            row.getString("task_code"),
            row.getString("status"),
            row.getObject("submitted_by", UUID.class),
            row.getObject("submission_date", LocalDateTime.class)
        );
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

package lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.FieldSheetQueryService;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetsPage;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetsPageResult;
import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.model.exceptions.InvalidSubmissionDayException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.InvertedSubmissionRangeException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.UnknownFieldSheetStatusException;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetCode;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetScan;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetStatus;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SubmissionYear;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.domain.repositories.FieldSheetRepository;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentId;
import lat.soulware.sgt.shared.domain.model.exceptions.InvalidPageException;
import lat.soulware.sgt.shared.domain.model.exceptions.InvalidSortDirectionException;
import lat.soulware.sgt.shared.domain.model.exceptions.UnsortableColumnException;
import lat.soulware.sgt.support.PostgresIntegrationTest;

/**
 * Reads pages of field sheets through the query service: filtered, sorted and bounded.
 *
 * <p>Four field sheets are stored per test, submitted in 2030 and 2031, and every query is held to
 * that range so the field sheets other integration tests commit today never show up here.
 */
@SpringBootTest
@Transactional
class FieldSheetsPageIT extends PostgresIntegrationTest {

    private static final String FROM = "2030-01-01";
    private static final String TO = "2031-12-31";

    @Autowired
    private FieldSheetQueryService fieldSheetQueryService;

    @Autowired
    private FieldSheetRepository fieldSheetRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void storeFourFieldSheets() {
        this.store(2, 2030, "2025-2030-01-03-0001", FieldSheetStatus.SUBMITTED, LocalDateTime.of(2030, 6, 1, 9, 0));
        this.store(1, 2031, "2025-2030-01-03-0002", FieldSheetStatus.DELIVERED, LocalDateTime.of(2031, 1, 5, 9, 0));
        this.store(10, 2031, "2025-2030-02-01-0003", FieldSheetStatus.DELIVERED, LocalDateTime.of(2031, 3, 1, 9, 0));
        this.store(9, 2031, "2025-2030-02-01-0004", FieldSheetStatus.VOIDED, LocalDateTime.of(2031, 2, 1, 9, 0));
    }

    @Test
    void theNewestSubmissionComesFirstByDefault() {
        assertThat(this.codes(this.page(null, null, null, "submittedAt", "desc", 0, 10)))
            .containsExactly("FI-0010-2031", "FI-0009-2031", "FI-0001-2031", "FI-0002-2030");
    }

    @Test
    void codesSortByYearAndThenNumber() {
        assertThat(this.codes(this.page(null, null, null, "code", "asc", 0, 10)))
            .containsExactly("FI-0002-2030", "FI-0001-2031", "FI-0009-2031", "FI-0010-2031");
    }

    @Test
    void filtersNarrowTheSelection() {
        assertThat(this.codes(this.page("DELIVERED", null, null, "code", "asc", 0, 10)))
            .containsExactly("FI-0001-2031", "FI-0010-2031");
        assertThat(this.codes(this.page(null, "fi-0010", null, "code", "asc", 0, 10)))
            .containsExactly("FI-0010-2031");
        assertThat(this.codes(this.page(null, null, "02-01", "code", "asc", 0, 10)))
            .containsExactly("FI-0009-2031", "FI-0010-2031");
    }

    @Test
    void aSubmissionRangeIncludesBothOfItsDays() {
        FieldSheetsPageResult page = this.fieldSheetQueryService.page(new FieldSheetsPage(
            null, null, null, "2031-01-05", "2031-02-01", "code", "asc", 0, 10
        ));

        assertThat(this.codes(page)).containsExactly("FI-0001-2031", "FI-0009-2031");
    }

    @Test
    void aPageHoldsItsShareAndCountsTheWholeSelection() {
        FieldSheetsPageResult page = this.page(null, null, null, "code", "asc", 1, 3);

        assertThat(this.codes(page)).containsExactly("FI-0010-2031");
        assertThat(page.totalElements()).isEqualTo(4);
        assertThat(page.totalPages()).isEqualTo(2);
        assertThat(page.actualPage()).isEqualTo(1);
        assertThat(page.pageSize()).isEqualTo(3);
    }

    @Test
    void whatNoPageCanBeIsRefused() {
        assertThatExceptionOfType(UnknownFieldSheetStatusException.class)
            .isThrownBy(() -> this.page("PENDING", null, null, "code", "asc", 0, 10));
        assertThatExceptionOfType(UnsortableColumnException.class)
            .isThrownBy(() -> this.page(null, null, null, "submittedBy", "asc", 0, 10));
        assertThatExceptionOfType(InvalidSortDirectionException.class)
            .isThrownBy(() -> this.page(null, null, null, "code", "up", 0, 10));
        assertThatExceptionOfType(InvalidPageException.class)
            .isThrownBy(() -> this.page(null, null, null, "code", "asc", -1, 10));
        assertThatExceptionOfType(InvalidPageException.class)
            .isThrownBy(() -> this.page(null, null, null, "code", "asc", 0, 101));
        assertThatExceptionOfType(InvalidSubmissionDayException.class)
            .isThrownBy(() -> this.fieldSheetQueryService.page(new FieldSheetsPage(
                null, null, null, "2031-13-01", null, "code", "asc", 0, 10
            )));
        assertThatExceptionOfType(InvertedSubmissionRangeException.class)
            .isThrownBy(() -> this.fieldSheetQueryService.page(new FieldSheetsPage(
                null, null, null, "2031-02-01", "2031-01-01", "code", "asc", 0, 10
            )));
    }

    private FieldSheetsPageResult page(
        String status,
        String code,
        String taskCode,
        String sort,
        String direction,
        int page,
        int size
    ) {
        return this.fieldSheetQueryService.page(new FieldSheetsPage(
            status, code, taskCode, FROM, TO, sort, direction, page, size
        ));
    }

    private List<String> codes(FieldSheetsPageResult page) {
        return page.content().stream()
            .map(fieldSheetSummaryResult -> fieldSheetSummaryResult.code())
            .toList();
    }

    /** Saves and flushes, so the projection's SQL sees the field sheet within the test's transaction. */
    private void store(int number, int year, String taskCode, FieldSheetStatus status, LocalDateTime submitted) {
        FieldSheet fieldSheet = FieldSheet.reconstitute(
            new FieldSheetId(UUID.randomUUID()),
            new FieldSheetCode(number, new SubmissionYear(year)),
            new TaskCode(taskCode),
            status,
            null,
            List.of(),
            null,
            null,
            new FieldSheetScan(new ScanKey("field-sheets/%d-%d.pdf".formatted(year, number)), 1024),
            new FieldAgentId(UUID.randomUUID()),
            submitted,
            null,
            null
        );
        this.fieldSheetRepository.save(fieldSheet);
        this.entityManager.flush();
    }
}

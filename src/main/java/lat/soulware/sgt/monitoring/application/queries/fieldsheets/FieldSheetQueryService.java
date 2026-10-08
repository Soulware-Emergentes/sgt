package lat.soulware.sgt.monitoring.application.queries.fieldsheets;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetById;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.FieldSheetsPage;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanOfFieldSheet;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.ScanUploadSlot;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections.FieldSheetProjection;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections.ScanLinkProjection;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.FieldSheetsPageResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.ScanLinkResult;
import lat.soulware.sgt.monitoring.domain.model.exceptions.FieldSheetNotFoundException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.InvalidSubmissionDayException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.InvertedSubmissionRangeException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.UnknownFieldSheetStatusException;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetStatus;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.application.queries.QueryService;
import lat.soulware.sgt.shared.domain.model.exceptions.InvalidPageException;
import lat.soulware.sgt.shared.domain.model.exceptions.InvalidSortDirectionException;
import lat.soulware.sgt.shared.domain.model.exceptions.UnsortableColumnException;

/** Answers the reads about field sheets, their scans, and the poi tasks they are submitted for. */
@Service
@Transactional(readOnly = true)
public class FieldSheetQueryService implements QueryService {

    /** The columns a page of field sheets sorts by, as the client names them. */
    private static final String SORTABLE = "code, taskCode, submittedAt, status";
    private static final Pattern SORTABLE_COLUMN = Pattern.compile("^(code|taskCode|submittedAt|status)$");
    private static final int MAX_PAGE_SIZE = 100;

    private final FieldSheetProjection fieldSheetProjection;
    private final ScanLinkProjection scanLinkProjection;

    public FieldSheetQueryService(FieldSheetProjection fieldSheetProjection, ScanLinkProjection scanLinkProjection) {
        this.fieldSheetProjection = fieldSheetProjection;
        this.scanLinkProjection = scanLinkProjection;
    }

    /**
     * @throws UnknownFieldSheetStatusException  if the status names no status a field sheet stands in
     * @throws InvalidSubmissionDayException     if either end of the submission range is not a date
     * @throws InvertedSubmissionRangeException  if the range ends before it starts
     * @throws UnsortableColumnException         if the sort names no sortable column
     * @throws InvalidSortDirectionException     if the direction is neither asc nor desc
     * @throws InvalidPageException              if the page is negative or the size out of range
     */
    public FieldSheetsPageResult page(FieldSheetsPage criteria) {
        boolean namesAStatus = criteria.status() == null || Arrays.stream(FieldSheetStatus.values())
            .anyMatch(status -> status.name().equals(criteria.status()));
        if (!namesAStatus) {
            throw new UnknownFieldSheetStatusException(criteria.status());
        }
        Optional<LocalDate> from = FieldSheetQueryService.submissionDay(criteria.submittedFrom());
        Optional<LocalDate> to = FieldSheetQueryService.submissionDay(criteria.submittedTo());
        boolean isInverted = from.isPresent() && to.isPresent() && to.get().isBefore(from.get());
        if (isInverted) {
            throw new InvertedSubmissionRangeException(criteria.submittedFrom(), criteria.submittedTo());
        }
        boolean isSortable = criteria.sort() != null && SORTABLE_COLUMN.matcher(criteria.sort()).matches();
        if (!isSortable) {
            throw new UnsortableColumnException(criteria.sort(), SORTABLE);
        }
        boolean isADirection = "asc".equalsIgnoreCase(criteria.direction())
            || "desc".equalsIgnoreCase(criteria.direction());
        if (!isADirection) {
            throw new InvalidSortDirectionException(criteria.direction());
        }
        boolean isAPage = criteria.page() >= 0 && criteria.size() >= 1 && criteria.size() <= MAX_PAGE_SIZE;
        if (!isAPage) {
            throw new InvalidPageException(criteria.page(), criteria.size(), MAX_PAGE_SIZE);
        }

        return this.fieldSheetProjection.findPage(criteria);
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

    private static Optional<LocalDate> submissionDay(String day) {
        if (day == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDate.parse(day));
        } catch (DateTimeParseException e) {
            throw new InvalidSubmissionDayException(day);
        }
    }

    private FieldSheetNotFoundException notFound(UUID id) {
        return new FieldSheetNotFoundException(List.of(new FieldSheetId(id)));
    }
}

package lat.soulware.sgt.monitoring.domain.repositories;

import java.util.Optional;

import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SubmissionYear;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.repositories.DomainRepository;

/** Stores {@link FieldSheet} aggregates. */
public interface FieldSheetRepository extends DomainRepository<FieldSheet, FieldSheetId> {

    /**
     * @param year a year
     * @return the field sheet with the highest number among those submitted in it, empty when none was
     */
    Optional<FieldSheet> findLastSubmittedIn(SubmissionYear year);
}

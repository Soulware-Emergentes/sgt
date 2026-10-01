package lat.soulware.sgt.monitoring.domain.repositories;

import lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.repositories.DomainRepository;

/** Stores {@link FieldSheet} aggregates. */
public interface FieldSheetRepository extends DomainRepository<FieldSheet, FieldSheetId> {
}

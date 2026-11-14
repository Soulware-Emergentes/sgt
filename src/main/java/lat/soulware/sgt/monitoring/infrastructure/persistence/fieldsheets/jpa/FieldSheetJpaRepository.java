package lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** The Spring Data delegate behind {@code JpaFieldSheetRepositoryAdapter}. */
public interface FieldSheetJpaRepository extends JpaRepository<FieldSheetEntity, UUID> {

    /**
     * Codes of one year share their suffix and pad their number to four digits, so the highest code
     * among them is the highest number.
     */
    Optional<FieldSheetEntity> findFirstByCodeEndingWithOrderByCodeDesc(String yearSuffix);
}

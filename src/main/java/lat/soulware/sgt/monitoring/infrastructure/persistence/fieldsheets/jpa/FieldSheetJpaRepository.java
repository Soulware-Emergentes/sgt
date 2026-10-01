package lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** The Spring Data delegate behind {@code JpaFieldSheetRepositoryAdapter}. */
public interface FieldSheetJpaRepository extends JpaRepository<FieldSheetEntity, UUID> {
}

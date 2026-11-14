package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * Where a {@link lat.soulware.sgt.monitoring.domain.model.aggregates.FieldSheet} stands in its review.
 * The lifecycle runs one way: a field sheet is submitted, delivered once poi accepts it for the
 * task it names, then approved or rejected. A rejected field sheet is never reopened. A corrected
 * evaluation is submitted as a new field sheet.
 *
 * <p>A submission poi refuses, because the task it names does not exist or already has a delivery,
 * is voided and never reviewed.
 */
public enum FieldSheetStatus implements ValueObject {
    SUBMITTED,
    DELIVERED,
    APPROVED,
    REJECTED,
    VOIDED
}

package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/** What poi answered to a delivery registered for one of its tasks. */
public enum DeliveryOutcome implements ValueObject {
    /** The task was pending and took the delivery. */
    ACCEPTED,
    /** The task does not exist, or already has a delivery. */
    REFUSED
}

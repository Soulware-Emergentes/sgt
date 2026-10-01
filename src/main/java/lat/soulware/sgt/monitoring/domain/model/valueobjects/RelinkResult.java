package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/** What poi did with a delivery relinked to another task. */
public enum RelinkResult implements ValueObject {
    /** The other task was pending and took the delivery. */
    MOVED,
    /** The other task held another field sheet's delivery, and the two were swapped. */
    SWAPPED,
    /** The other task does not exist or was completed, so nothing changed. */
    REFUSED
}

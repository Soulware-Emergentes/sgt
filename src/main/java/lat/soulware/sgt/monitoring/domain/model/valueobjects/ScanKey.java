package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import lat.soulware.sgt.monitoring.domain.model.exceptions.BlankScanKeyException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * Where a field sheet's scanned PDF sits in the object store.
 *
 * @param value the object's key, never blank
 */
public record ScanKey(String value) implements ValueObject {

    public ScanKey {
        if (value == null || value.isBlank()) {
            throw new BlankScanKeyException();
        }
    }
}

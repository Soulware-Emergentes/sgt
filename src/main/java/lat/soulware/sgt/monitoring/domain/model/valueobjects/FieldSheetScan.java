package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import lat.soulware.sgt.monitoring.domain.model.exceptions.EmptyScanException;
import lat.soulware.sgt.monitoring.domain.model.exceptions.MissingScanException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The scanned PDF a field agent submits as a field sheet's evidence, as the object store holds it.
 * Nobody changes it once submitted.
 *
 * @param key         where the PDF sits in the object store
 * @param sizeInBytes how large it is, more than zero
 */
public record FieldSheetScan(ScanKey key, long sizeInBytes) implements ValueObject {

    public FieldSheetScan {
        if (key == null) {
            throw new MissingScanException();
        }
        if (sizeInBytes <= 0) {
            throw new EmptyScanException();
        }
    }
}

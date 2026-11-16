package lat.soulware.sgt.monitoring.domain.model.exceptions;

import java.util.Collection;

import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.model.exceptions.EntityNotFoundException;

/** Thrown when no field sheet carries the requested identity. */
public final class FieldSheetNotFoundException extends EntityNotFoundException {

    /**
     * @param missing the identities that matched nothing
     */
    public FieldSheetNotFoundException(Collection<FieldSheetId> missing) {
        super(missing);
    }
}

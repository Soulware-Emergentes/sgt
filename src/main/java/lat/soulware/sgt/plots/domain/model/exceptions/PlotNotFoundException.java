package lat.soulware.sgt.plots.domain.model.exceptions;

import java.util.Collection;

import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.shared.domain.model.exceptions.EntityNotFoundException;

/** Thrown when no plot carries the requested identity. */
public final class PlotNotFoundException extends EntityNotFoundException {

    /**
     * @param missing the identities that matched nothing
     */
    public PlotNotFoundException(Collection<PlotId> missing) {
        super(missing);
    }
}

package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/**
 * Thrown when plots are asked for by a region that is no level of a ubigeo: two digits for a
 * department, four for a province, or six for a district.
 */
public final class InvalidRegionException extends BusinessRuleViolationException {

    /**
     * @param region the region given
     */
    public InvalidRegionException(String region) {
        super(region);
    }
}

package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/**
 * Thrown when plots are asked for by a bounding box that is not four longitudes and latitudes in
 * degrees, west, south, east and north, with west below east and south below north.
 */
public final class InvalidBoundingBoxException extends BusinessRuleViolationException {

    /**
     * @param boundingBox the bounding box given, as it was written
     */
    public InvalidBoundingBoxException(String boundingBox) {
        super(boundingBox);
    }
}

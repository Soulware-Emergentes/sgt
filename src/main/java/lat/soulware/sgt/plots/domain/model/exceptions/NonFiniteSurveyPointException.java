package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.plots.domain.model.valueobjects.SurveyPoint} coordinate is NaN or infinite. */
public final class NonFiniteSurveyPointException extends BusinessRuleViolationException {

    /**
     * @param easting  the rejected easting
     * @param northing the rejected northing
     */
    public NonFiniteSurveyPointException(double easting, double northing) {
        super(easting, northing);
    }
}

package lat.soulware.sgt.plots.domain.model.valueobjects;

import lat.soulware.sgt.plots.domain.model.exceptions.NonFiniteSurveyPointException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * A vertex measured by a total station, projected in the UTM zone of the boundary holding it.
 *
 * @param easting  metres east, finite
 * @param northing metres north, finite
 */
public record SurveyPoint(double easting, double northing) implements ValueObject {

    public SurveyPoint {
        boolean isFinite = Double.isFinite(easting) && Double.isFinite(northing);
        if (!isFinite) {
            throw new NonFiniteSurveyPointException(easting, northing);
        }
    }
}

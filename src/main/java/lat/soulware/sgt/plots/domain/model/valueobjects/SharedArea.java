package lat.soulware.sgt.plots.domain.model.valueobjects;

import java.math.BigDecimal;

import lat.soulware.sgt.plots.domain.model.exceptions.NegativeSharedAreaException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The area two plots have in common. Zero for plots that are apart or only share an edge.
 *
 * @param squareMeters the area, never null and never negative
 */
public record SharedArea(BigDecimal squareMeters) implements ValueObject {

    public SharedArea {
        if (squareMeters == null || squareMeters.signum() < 0) {
            throw new NegativeSharedAreaException(squareMeters);
        }
    }
}

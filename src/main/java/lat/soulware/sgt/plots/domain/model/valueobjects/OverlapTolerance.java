package lat.soulware.sgt.plots.domain.model.valueobjects;

import java.math.BigDecimal;

import lat.soulware.sgt.plots.domain.model.exceptions.NegativeOverlapToleranceException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The shared area two plots may have before they count as overlapping. Two crews surveying the same
 * boundary disagree by centimetres, so neighbouring plots share slivers that are measurement error
 * rather than a dispute.
 *
 * @param squareMeters the largest shared area still accepted, never null and never negative
 */
public record OverlapTolerance(BigDecimal squareMeters) implements ValueObject {

    public OverlapTolerance {
        if (squareMeters == null || squareMeters.signum() < 0) {
            throw new NegativeOverlapToleranceException(squareMeters);
        }
    }

    /**
     * @param shared the area two plots have in common
     * @return whether {@code shared} is more than this tolerance accepts
     */
    public boolean isExceededBy(SharedArea shared) {
        return shared.squareMeters().compareTo(this.squareMeters) > 0;
    }
}

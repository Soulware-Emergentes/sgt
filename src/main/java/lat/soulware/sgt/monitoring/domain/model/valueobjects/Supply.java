package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import lat.soulware.sgt.shared.domain.model.valueobjects.HoldsNoInvariants;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * An agricultural input a beneficiary used, and how much of it.
 *
 * @param name     what was used
 * @param quantity how much of it was used
 */
@HoldsNoInvariants
public record Supply(SupplyName name, Quantity quantity) implements ValueObject {
}

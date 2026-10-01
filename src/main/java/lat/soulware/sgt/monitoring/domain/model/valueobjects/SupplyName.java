package lat.soulware.sgt.monitoring.domain.model.valueobjects;

import lat.soulware.sgt.monitoring.domain.model.exceptions.BlankSupplyNameException;
import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The agricultural input's name, as the program names it (e.g. "fertilizante", "semilla").
 *
 * @param value the name, never blank
 */
public record SupplyName(String value) implements ValueObject {

    public SupplyName {
        if (value == null || value.isBlank()) {
            throw new BlankSupplyNameException();
        }
    }
}

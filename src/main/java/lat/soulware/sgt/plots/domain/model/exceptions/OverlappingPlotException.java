package lat.soulware.sgt.plots.domain.model.exceptions;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;
import lat.soulware.sgt.shared.domain.model.exceptions.DomainException;

/**
 * Thrown when a plot's boundary overlaps registered plots by more than the tolerance allows. Its
 * details name those plots, under {@code overlappingPlots}.
 */
public final class OverlappingPlotException extends BusinessRuleViolationException implements DomainException.Detailed {

    private final List<UUID> overlapped;

    /**
     * @param overlapped the registered plots the boundary overlaps, never empty
     */
    public OverlappingPlotException(Set<PlotId> overlapped) {
        super(overlapped);
        this.overlapped = overlapped.stream().map(plotId -> plotId.value()).sorted().toList();
    }

    @Override
    public Map<String, Object> details() {
        return Map.of("overlappingPlots", this.overlapped);
    }
}

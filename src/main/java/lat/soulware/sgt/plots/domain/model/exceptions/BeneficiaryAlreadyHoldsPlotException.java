package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotId;
import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a plot is registered to a beneficiary who already holds one. */
public final class BeneficiaryAlreadyHoldsPlotException extends BusinessRuleViolationException {

    /**
     * @param owner the beneficiary the plot was registered to
     * @param held  the plot they already hold
     */
    public BeneficiaryAlreadyHoldsPlotException(BeneficiaryLegalDocument owner, PlotId held) {
        super(owner, held);
    }
}

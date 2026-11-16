package lat.soulware.sgt.plots.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.EntityNotFoundException;

/** Thrown when the plot of a beneficiary is asked for and no plot is registered to them. */
public final class BeneficiaryHoldsNoPlotException extends EntityNotFoundException {

    /**
     * @param documentType   the kind of the beneficiary's legal document
     * @param documentNumber its number
     */
    public BeneficiaryHoldsNoPlotException(String documentType, String documentNumber) {
        super(documentType, documentNumber);
    }
}

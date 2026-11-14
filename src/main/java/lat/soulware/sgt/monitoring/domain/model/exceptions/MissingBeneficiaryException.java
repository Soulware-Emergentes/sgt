package lat.soulware.sgt.monitoring.domain.model.exceptions;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a field sheet is submitted without the evaluated beneficiary's legal document. */
public final class MissingBeneficiaryException extends BusinessRuleViolationException {

    public MissingBeneficiaryException() {
        super();
    }
}

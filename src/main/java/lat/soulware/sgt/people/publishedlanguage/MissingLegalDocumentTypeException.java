package lat.soulware.sgt.people.publishedlanguage;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a {@link lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument} carries no type. */
public final class MissingLegalDocumentTypeException extends BusinessRuleViolationException {

    public MissingLegalDocumentTypeException() {
        super();
    }
}

package lat.soulware.sgt.people.publishedlanguage;

import lat.soulware.sgt.shared.domain.model.exceptions.BusinessRuleViolationException;

/** Thrown when a document number does not have the shape its {@link LegalDocumentType} requires. */
public final class InvalidLegalDocumentNumberException extends BusinessRuleViolationException {

    /**
     * @param type   the declared document type
     * @param number the rejected number
     */
    public InvalidLegalDocumentNumberException(LegalDocumentType type, String number) {
        super(type, number);
    }
}

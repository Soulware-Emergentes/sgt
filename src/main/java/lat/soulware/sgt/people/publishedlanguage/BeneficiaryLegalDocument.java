package lat.soulware.sgt.people.publishedlanguage;

import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The legal document identifying a beneficiary.
 *
 * @param type   the kind of document
 * @param number the document number, valid for {@code type}
 */
public record BeneficiaryLegalDocument(LegalDocumentType type, String number) implements ValueObject {

    public BeneficiaryLegalDocument {
        if (type == null) {
            throw new MissingLegalDocumentTypeException();
        }
        if (!type.matches(number)) {
            throw new InvalidLegalDocumentNumberException(type, number);
        }
    }
}

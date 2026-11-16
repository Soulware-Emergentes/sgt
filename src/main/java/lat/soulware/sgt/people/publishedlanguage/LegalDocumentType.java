package lat.soulware.sgt.people.publishedlanguage;

import java.util.regex.Pattern;

import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/** The kind of legal document identifying a beneficiary, and the shape its number must take. */
public enum LegalDocumentType implements ValueObject {
    DNI(Pattern.compile("^\\d{8}$")),
    FOREIGNER_ID_CARD(Pattern.compile("^[A-Za-z0-9]{9,12}$")),
    PASSPORT(Pattern.compile("^[A-Za-z0-9]{6,12}$"));

    private final Pattern numberPattern;

    LegalDocumentType(Pattern numberPattern) {
        this.numberPattern = numberPattern;
    }

    /**
     * @param number the document number to check
     * @return whether {@code number} has the shape this document type requires
     */
    public boolean matches(String number) {
        return number != null && this.numberPattern.matcher(number).matches();
    }
}

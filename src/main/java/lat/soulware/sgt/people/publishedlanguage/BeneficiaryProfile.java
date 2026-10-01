package lat.soulware.sgt.people.publishedlanguage;

import java.time.LocalDate;

/**
 * A beneficiary's personal data, as the beneficiary registry holds it.
 *
 * @param document        the legal document identifying the beneficiary
 * @param names           the given names
 * @param paternalSurname the paternal surname
 * @param maternalSurname the maternal surname
 * @param dateOfBirth     the date of birth
 * @param ubigeo          the six-digit code of the district the beneficiary is registered in
 */
public record BeneficiaryProfile(
    BeneficiaryLegalDocument document,
    String names,
    String paternalSurname,
    String maternalSurname,
    LocalDate dateOfBirth,
    String ubigeo
) {
}

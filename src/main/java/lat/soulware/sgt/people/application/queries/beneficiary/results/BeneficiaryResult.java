package lat.soulware.sgt.people.application.queries.beneficiary.results;

import java.time.LocalDate;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * One beneficiary, as the beneficiary registry returned it.
 *
 * @param documentType    the name of the document type
 * @param documentNumber  the document number
 * @param names           the given names
 * @param paternalSurname the paternal surname
 * @param maternalSurname the maternal surname
 * @param dateOfBirth     the date of birth
 * @param ubigeo          the district code
 */
public record BeneficiaryResult(
    String documentType,
    String documentNumber,
    String names,
    String paternalSurname,
    String maternalSurname,
    LocalDate dateOfBirth,
    String ubigeo
) implements QueryResult {
}

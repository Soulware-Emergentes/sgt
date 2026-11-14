package lat.soulware.sgt.people.application.queries.beneficiary.criteria;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * One beneficiary to look up, by legal document.
 *
 * @param type   the name of the document type
 * @param number the document number
 */
public record BeneficiaryByDocument(String type, String number) implements QueryCriteria {
}

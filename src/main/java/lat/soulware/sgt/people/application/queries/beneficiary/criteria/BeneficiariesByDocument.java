package lat.soulware.sgt.people.application.queries.beneficiary.criteria;

import java.util.List;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * The beneficiaries to look up, by legal document.
 *
 * @param documents the documents, copied defensively
 */
public record BeneficiariesByDocument(List<Document> documents) implements QueryCriteria {

    /**
     * @param type   the name of the document type
     * @param number the document number
     */
    public record Document(String type, String number) {
    }

    public BeneficiariesByDocument {
        documents = List.copyOf(documents);
    }
}

package lat.soulware.sgt.people.application.queries.beneficiary.projections;

import java.util.List;

import lat.soulware.sgt.people.application.queries.beneficiary.criteria.BeneficiariesByDocument.Document;
import lat.soulware.sgt.people.application.queries.beneficiary.results.BeneficiaryResult;
import lat.soulware.sgt.shared.application.queries.projections.Projection;

/** The beneficiaries the registry holds. */
public interface BeneficiaryProjection extends Projection {

    /**
     * @param documents the documents to look up
     * @return the beneficiaries found, leaving out the documents the registry does not know
     */
    List<BeneficiaryResult> findByDocuments(List<Document> documents);
}

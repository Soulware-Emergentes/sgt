package lat.soulware.sgt.people.application.queries.beneficiary;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.people.application.queries.beneficiary.criteria.BeneficiariesByDocument;
import lat.soulware.sgt.people.application.queries.beneficiary.projections.BeneficiaryProjection;
import lat.soulware.sgt.people.application.queries.beneficiary.results.BeneficiaryResult;
import lat.soulware.sgt.shared.application.queries.QueryService;

/** The read entry point for beneficiaries. */
@Transactional(readOnly = true)
@Service
public class BeneficiaryQueryService implements QueryService {

    private final BeneficiaryProjection beneficiaryProjection;

    public BeneficiaryQueryService(BeneficiaryProjection beneficiaryProjection) {
        this.beneficiaryProjection = beneficiaryProjection;
    }

    public List<BeneficiaryResult> byDocument(BeneficiariesByDocument criteria) {
        if (criteria.documents().isEmpty()) {
            return List.of();
        }
        return this.beneficiaryProjection.findByDocuments(criteria.documents().stream().distinct().toList());
    }
}

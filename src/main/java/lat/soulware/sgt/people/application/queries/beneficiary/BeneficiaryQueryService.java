package lat.soulware.sgt.people.application.queries.beneficiary;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lat.soulware.sgt.people.application.queries.beneficiary.criteria.BeneficiariesByDocument;
import lat.soulware.sgt.people.application.queries.beneficiary.projections.BeneficiaryProjection;
import lat.soulware.sgt.people.application.queries.beneficiary.results.BeneficiariesResult;
import lat.soulware.sgt.shared.application.queries.QueryService;

/** The read entry point for beneficiaries. */
@Transactional(readOnly = true)
@Service
public class BeneficiaryQueryService implements QueryService {

    private final BeneficiaryProjection beneficiaryProjection;

    public BeneficiaryQueryService(BeneficiaryProjection beneficiaryProjection) {
        this.beneficiaryProjection = beneficiaryProjection;
    }

    public BeneficiariesResult byDocument(BeneficiariesByDocument criteria) {
        if (criteria.documents().isEmpty()) {
            return new BeneficiariesResult(List.of());
        }
        BeneficiariesByDocument distinct = new BeneficiariesByDocument(criteria.documents().stream().distinct().toList());

        return new BeneficiariesResult(this.beneficiaryProjection.findByDocuments(distinct));
    }
}

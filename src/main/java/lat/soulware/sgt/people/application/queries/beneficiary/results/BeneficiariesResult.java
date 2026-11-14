package lat.soulware.sgt.people.application.queries.beneficiary.results;

import java.util.List;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * The beneficiaries a lookup found.
 *
 * @param beneficiaries the beneficiaries, empty when none were found
 */
public record BeneficiariesResult(List<BeneficiaryResult> beneficiaries) implements QueryResult {

    public BeneficiariesResult {
        beneficiaries = List.copyOf(beneficiaries);
    }
}

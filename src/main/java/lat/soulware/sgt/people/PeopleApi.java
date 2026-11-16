package lat.soulware.sgt.people;

import java.util.Collection;
import java.util.List;

import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryProfile;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentId;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentProfile;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantId;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantProfile;
import lat.soulware.sgt.shared.ModuleApi;

/**
 * Resolves the people other modules refer to into their personal data, for reads that show it.
 *
 * <p>Lookups take a batch, so a list of records resolves its people in one call. An identity the
 * owning system does not know is left out of the answer rather than failing it.
 */
public interface PeopleApi extends ModuleApi {

    /**
     * @param documents the legal documents of the beneficiaries to look up
     * @return the profiles found, one per distinct document, in unspecified order
     */
    List<BeneficiaryProfile> findBeneficiaries(Collection<BeneficiaryLegalDocument> documents);

    /**
     * @param ids the identities of the field agents to look up
     * @return the profiles found, one per distinct identity, in unspecified order
     */
    List<FieldAgentProfile> findFieldAgents(Collection<FieldAgentId> ids);

    /**
     * @param ids the identities of the general management assistants to look up
     * @return the profiles found, one per distinct identity, in unspecified order
     */
    List<GeneralManagementAssistantProfile> findGeneralManagementAssistants(
        Collection<GeneralManagementAssistantId> ids
    );
}

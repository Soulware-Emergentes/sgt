package lat.soulware.sgt.people.application;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import lat.soulware.sgt.people.PeopleApi;
import lat.soulware.sgt.people.application.queries.beneficiary.BeneficiaryQueryService;
import lat.soulware.sgt.people.application.queries.beneficiary.criteria.BeneficiariesByDocument;
import lat.soulware.sgt.people.application.queries.beneficiary.criteria.BeneficiaryByDocument;
import lat.soulware.sgt.people.application.queries.beneficiary.results.BeneficiaryResult;
import lat.soulware.sgt.people.application.queries.staff.StaffQueryService;
import lat.soulware.sgt.people.application.queries.staff.criteria.StaffMembersById;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryProfile;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentId;
import lat.soulware.sgt.people.publishedlanguage.FieldAgentProfile;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantId;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantProfile;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;

/** Answers {@link PeopleApi} through the module's query services. */
@Service
public class PeopleApiService implements PeopleApi {

    private final BeneficiaryQueryService beneficiaryQueryService;
    private final StaffQueryService staffQueryService;

    public PeopleApiService(BeneficiaryQueryService beneficiaryQueryService, StaffQueryService staffQueryService) {
        this.beneficiaryQueryService = beneficiaryQueryService;
        this.staffQueryService = staffQueryService;
    }

    @Override
    public List<BeneficiaryProfile> findBeneficiaries(Collection<BeneficiaryLegalDocument> documents) {
        List<BeneficiaryByDocument> wanted = documents.stream()
            .map(document -> new BeneficiaryByDocument(document.type().name(), document.number()))
            .toList();

        return this.beneficiaryQueryService.byDocument(new BeneficiariesByDocument(wanted)).beneficiaries().stream()
            .map(PeopleApiService::toProfile)
            .toList();
    }

    @Override
    public List<FieldAgentProfile> findFieldAgents(Collection<FieldAgentId> ids) {
        StaffMembersById criteria = new StaffMembersById(ids.stream().map(f -> f.value()).toList());

        return this.staffQueryService.fieldAgentsById(criteria).staffMembers().stream()
            .map(result -> new FieldAgentProfile(new FieldAgentId(result.id()), result.username(), result.displayName()))
            .toList();
    }

    @Override
    public List<GeneralManagementAssistantProfile> findGeneralManagementAssistants(
        Collection<GeneralManagementAssistantId> ids
    ) {
        StaffMembersById criteria = new StaffMembersById(ids.stream().map(generalManagementAssistantId -> generalManagementAssistantId.value()).toList());

        return this.staffQueryService.generalManagementAssistantsById(criteria).staffMembers().stream()
            .map(result -> new GeneralManagementAssistantProfile(
                new GeneralManagementAssistantId(result.id()),
                result.username(),
                result.displayName()
            ))
            .toList();
    }

    private static BeneficiaryProfile toProfile(BeneficiaryResult result) {
        BeneficiaryLegalDocument document = new BeneficiaryLegalDocument(
            LegalDocumentType.valueOf(result.documentType()),
            result.documentNumber()
        );
        return new BeneficiaryProfile(
            document,
            result.names(),
            result.paternalSurname(),
            result.maternalSurname(),
            result.dateOfBirth(),
            result.ubigeo()
        );
    }
}

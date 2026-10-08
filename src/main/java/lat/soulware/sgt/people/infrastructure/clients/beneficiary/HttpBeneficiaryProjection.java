package lat.soulware.sgt.people.infrastructure.clients.beneficiary;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import lat.soulware.sgt.people.application.queries.beneficiary.criteria.BeneficiariesByDocument;
import lat.soulware.sgt.people.application.queries.beneficiary.criteria.BeneficiaryByDocument;
import lat.soulware.sgt.people.application.queries.beneficiary.projections.BeneficiaryProjection;
import lat.soulware.sgt.people.application.queries.beneficiary.results.BeneficiaryResult;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.LegalDocumentType;
import lat.soulware.sgt.shared.domain.model.exceptions.DomainException;
import lat.soulware.sgt.shared.infrastructure.clients.ServiceTokens;
import lat.soulware.sgt.shared.infrastructure.exceptions.InfrastructureException;

/**
 * Reads beneficiaries from the beneficiary registry's v1 lookup endpoint, with a token staff issues
 * to sgt for the registry. The registry answers at
 * most {@value #MAX_LOOKUP_SIZE} documents per request, so a larger batch is sent in chunks. The
 * documents it does not know come back under {@code notFound} and are left out here.
 */
@Component
public class HttpBeneficiaryProjection implements BeneficiaryProjection {

    private static final int MAX_LOOKUP_SIZE = 100;
    private static final String LOOKUP_PATH = "/api/v1/beneficiaries/lookup";
    private static final String BENEFICIARIES_REGISTRATION = "beneficiaries";

    private final RestClient client;

    public HttpBeneficiaryProjection(
        RestClient.Builder builder,
        @Value("${people.beneficiaries.base-url}") String baseUrl,
        ServiceTokens serviceTokens
    ) {
        this.client = builder
            .baseUrl(baseUrl)
            .requestInterceptor(serviceTokens.bearerFor(BENEFICIARIES_REGISTRATION))
            .build();
    }

    @Override
    public List<BeneficiaryResult> findByDocuments(BeneficiariesByDocument criteria) {
        List<BeneficiaryByDocument> documents = criteria.documents();
        List<BeneficiaryResult> found = new ArrayList<>();
        for (int from = 0; from < documents.size(); from += MAX_LOOKUP_SIZE) {
            List<BeneficiaryByDocument> chunk = documents.subList(from, Math.min(from + MAX_LOOKUP_SIZE, documents.size()));
            this.lookup(chunk).found().stream()
                .map(HttpBeneficiaryProjection::toResult)
                .forEach(found::add);
        }
        return found;
    }

    private BeneficiaryLookupResponse lookup(List<BeneficiaryByDocument> chunk) {
        List<LegalDocumentResource> wanted = chunk.stream()
            .map(document -> new LegalDocumentResource(document.type(), document.number()))
            .toList();
        try {
            BeneficiaryLookupResponse response = this.client.post()
                .uri(LOOKUP_PATH)
                .body(new LookupRequest(wanted))
                .retrieve()
                .body(BeneficiaryLookupResponse.class);
            if (response == null || response.found() == null) {
                throw new InfrastructureException("Beneficiary lookup returned no body", null);
            }
            return response;
        } catch (RestClientException e) {
            throw new InfrastructureException("Beneficiary lookup of %d documents failed".formatted(chunk.size()), e);
        }
    }

    /**
     * Rejects a document sgt cannot read, a type it does not know or a number breaking the type's
     * rules, as a registry failure: the data came from the registry, not from the caller.
     */
    private static BeneficiaryResult toResult(BeneficiaryResource resource) {
        try {
            new BeneficiaryLegalDocument(
                LegalDocumentType.valueOf(resource.legalDocument().type()),
                resource.legalDocument().number()
            );
        } catch (IllegalArgumentException | NullPointerException | DomainException e) {
            throw new InfrastructureException(
                "Beneficiary registry returned an unreadable legal document %s".formatted(resource.legalDocument()),
                e
            );
        }
        return new BeneficiaryResult(
            resource.legalDocument().type(),
            resource.legalDocument().number(),
            resource.names(),
            resource.paternalSurname(),
            resource.maternalSurname(),
            resource.dateOfBirth(),
            resource.ubigeo()
        );
    }

    private record LookupRequest(List<LegalDocumentResource> legalDocuments) {
    }

    private record BeneficiaryLookupResponse(List<BeneficiaryResource> found, List<LegalDocumentResource> notFound) {
    }

    private record LegalDocumentResource(String type, String number) {
    }

    private record BeneficiaryResource(
        LegalDocumentResource legalDocument,
        String names,
        String paternalSurname,
        String maternalSurname,
        LocalDate dateOfBirth,
        String ubigeo
    ) {
    }
}

package lat.soulware.sgt.people.infrastructure.clients.staff;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import lat.soulware.sgt.people.application.queries.staff.projections.FieldAgentProjection;
import lat.soulware.sgt.people.application.queries.staff.results.FieldAgentResult;
import lat.soulware.sgt.shared.infrastructure.exceptions.InfrastructureException;

/**
 * Reads field agents from the staff directory's v2 lookup endpoint. The directory answers at most
 * {@value #MAX_LOOKUP_SIZE} GUIDs per request, so a larger batch is sent in chunks. The GUIDs it
 * does not know come back under {@code notFound} and are left out here.
 */
@Component
public class HttpFieldAgentProjection implements FieldAgentProjection {

    private static final int MAX_LOOKUP_SIZE = 100;
    private static final String LOOKUP_PATH = "/api/v2/field-agents/lookup";
    private static final String API_KEY_HEADER = "X-API-KEY";

    private final RestClient client;

    public HttpFieldAgentProjection(
        RestClient.Builder builder,
        @Value("${people.field-agents.base-url}") String baseUrl,
        @Value("${people.field-agents.api-key}") String apiKey
    ) {
        this.client = builder
            .baseUrl(baseUrl)
            .defaultHeader(API_KEY_HEADER, apiKey)
            .build();
    }

    @Override
    public List<FieldAgentResult> findByIds(List<UUID> ids) {
        List<FieldAgentResult> found = new ArrayList<>();
        for (int from = 0; from < ids.size(); from += MAX_LOOKUP_SIZE) {
            List<UUID> chunk = ids.subList(from, Math.min(from + MAX_LOOKUP_SIZE, ids.size()));
            this.lookup(chunk).found().stream()
                .map(HttpFieldAgentProjection::toResult)
                .forEach(found::add);
        }
        return found;
    }

    private FieldAgentLookupResponse lookup(List<UUID> chunk) {
        try {
            FieldAgentLookupResponse response = this.client.post()
                .uri(LOOKUP_PATH)
                .body(new LookupRequest(chunk))
                .retrieve()
                .body(FieldAgentLookupResponse.class);
            if (response == null || response.found() == null) {
                throw new InfrastructureException("Field agent lookup returned no body", null);
            }
            return response;
        } catch (RestClientException e) {
            throw new InfrastructureException("Field agent lookup of %d GUIDs failed".formatted(chunk.size()), e);
        }
    }

    /**
     * Rejects an entry sgt cannot identify as a directory failure: the data came from the
     * directory, not from the caller.
     */
    private static FieldAgentResult toResult(FieldAgentResource resource) {
        if (resource.id() == null) {
            throw new InfrastructureException(
                "Staff directory returned a field agent with no GUID: %s".formatted(resource),
                null
            );
        }
        return new FieldAgentResult(resource.id(), resource.username(), resource.displayName());
    }

    private record LookupRequest(List<UUID> ids) {
    }

    private record FieldAgentLookupResponse(List<FieldAgentResource> found, List<UUID> notFound) {
    }

    private record FieldAgentResource(UUID id, String username, String displayName) {
    }
}

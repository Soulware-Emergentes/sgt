package lat.soulware.sgt.people.infrastructure.clients.staff;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import lat.soulware.sgt.people.application.queries.staff.projections.PersonProjection;
import lat.soulware.sgt.people.application.queries.staff.results.StaffMemberResult;
import lat.soulware.sgt.shared.infrastructure.clients.ServiceTokens;
import lat.soulware.sgt.shared.infrastructure.exceptions.InfrastructureException;

/**
 * Reads people from the staff directory's v3 lookup endpoint, by GUID whatever their job. The
 * directory answers at most {@value #MAX_LOOKUP_SIZE} GUIDs per request, so a larger batch is sent in
 * chunks. The GUIDs it does not know come back under {@code notFound} and are left out here.
 */
@Component
public class HttpPersonProjection implements PersonProjection {

    private static final int MAX_LOOKUP_SIZE = 100;
    private static final String DIRECTORY_REGISTRATION = "directory";
    private static final String LOOKUP_PATH = "/api/v3/people/lookup";

    private final RestClient client;

    /**
     * @param builder       the builder the client is made from
     * @param baseUrl       where the staff directory is
     * @param serviceTokens where sgt gets its token for the directory API
     */
    public HttpPersonProjection(
        RestClient.Builder builder,
        @Value("${people.directory.base-url}") String baseUrl,
        ServiceTokens serviceTokens
    ) {
        this.client = builder
            .baseUrl(baseUrl)
            .requestInterceptor(serviceTokens.bearerFor(DIRECTORY_REGISTRATION))
            .build();
    }

    @Override
    public List<StaffMemberResult> findByIds(List<UUID> ids) {
        List<StaffMemberResult> found = new ArrayList<>();
        for (int from = 0; from < ids.size(); from += MAX_LOOKUP_SIZE) {
            List<UUID> chunk = ids.subList(from, Math.min(from + MAX_LOOKUP_SIZE, ids.size()));
            this.lookup(chunk).found().stream()
                .map(this::toResult)
                .forEach(found::add);
        }
        return found;
    }

    private StaffLookupResponse lookup(List<UUID> chunk) {
        try {
            StaffLookupResponse response = this.client.post()
                .uri(LOOKUP_PATH)
                .body(new LookupRequest(chunk))
                .retrieve()
                .body(StaffLookupResponse.class);
            if (response == null || response.found() == null) {
                throw new InfrastructureException("Person lookup returned no body", null);
            }
            return response;
        } catch (RestClientException e) {
            throw new InfrastructureException(
                "Person lookup of %d GUIDs failed".formatted(chunk.size()),
                e
            );
        }
    }

    /**
     * Rejects an entry sgt cannot identify as a directory failure: the data came from the
     * directory, not from the caller.
     */
    private StaffMemberResult toResult(StaffResource resource) {
        if (resource.id() == null) {
            throw new InfrastructureException(
                "Staff directory returned a person with no GUID: %s".formatted(resource),
                null
            );
        }
        return new StaffMemberResult(resource.id(), resource.username(), resource.displayName());
    }

    private record LookupRequest(List<UUID> ids) {
    }

    private record StaffLookupResponse(List<StaffResource> found, List<UUID> notFound) {
    }

    private record StaffResource(UUID id, String username, String displayName) {
    }
}

package lat.soulware.sgt.people.infrastructure.clients.staff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import lat.soulware.sgt.people.application.queries.staff.results.StaffMemberResult;
import lat.soulware.sgt.shared.infrastructure.clients.ServiceTokens;
import lat.soulware.sgt.shared.infrastructure.exceptions.InfrastructureException;

/** The directory lookup sgt resolves both its field agents and its assistants through. */
class HttpPersonProjectionTest {

    private static final String BASE_URL = "http://staff.test";
    /** Stands in for staff: every request carries a token named for the registration it was got under. */
    private static final ServiceTokens SERVICE_TOKENS = registrationId -> (request, body, execution) -> {
        request.getHeaders().setBearerAuth("token-for-" + registrationId);
        return execution.execute(request, body);
    };
    private static final String LOOKUP = BASE_URL + "/api/v3/people/lookup";

    private static final UUID ID = UUID.randomUUID();
    private static final String FOUND = """
        {"found": [{"id": "%s", "username": "jperez", "displayName": "Juan Perez", "jobTitle": "Field agent", "department": "Field operations"}], "notFound": []}"""
        .formatted(ID);

    @Test
    void peopleAreLookedUpByGuidWithATokenForTheDirectory() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        HttpPersonProjection projection = new HttpPersonProjection(builder, BASE_URL, SERVICE_TOKENS);
        server.expect(requestTo(LOOKUP))
            .andExpect(header("Authorization", "Bearer token-for-directory"))
            .andRespond(withSuccess(FOUND, MediaType.APPLICATION_JSON));

        List<StaffMemberResult> found = projection.findByIds(List.of(ID));

        assertThat(found).containsExactly(new StaffMemberResult(ID, "jperez", "Juan Perez"));
        server.verify();
    }

    @Test
    void aLargeBatchIsSentInChunksOfTheDirectorysLimit() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        HttpPersonProjection projection = new HttpPersonProjection(builder, BASE_URL, SERVICE_TOKENS);
        List<UUID> ids = IntStream.range(0, 250).mapToObj(i -> UUID.randomUUID()).toList();
        String none = """
            {"found": [], "notFound": []}""";
        server.expect(ExpectedCount.times(3), requestTo(LOOKUP))
            .andRespond(withSuccess(none, MediaType.APPLICATION_JSON));

        projection.findByIds(ids);

        server.verify();
    }

    @Test
    void aDirectoryFailureIsAnInfrastructureFailure() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        HttpPersonProjection projection = new HttpPersonProjection(builder, BASE_URL, SERVICE_TOKENS);
        server.expect(requestTo(LOOKUP))
            .andRespond(withServerError());

        assertThatThrownBy(() -> projection.findByIds(List.of(ID)))
            .isInstanceOf(InfrastructureException.class)
            .hasMessageContaining("Person lookup");
    }
}

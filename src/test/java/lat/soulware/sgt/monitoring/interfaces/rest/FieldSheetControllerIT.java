package lat.soulware.sgt.monitoring.interfaces.rest;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import lat.soulware.sgt.monitoring.domain.model.exceptions.ScanNotUploadedException;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.FieldSheetScan;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ScanKey;
import lat.soulware.sgt.monitoring.domain.services.ScanStore;
import lat.soulware.sgt.monitoring.domain.services.TaskLedger;
import lat.soulware.sgt.support.PostgresIntegrationTest;
import lat.soulware.sgt.support.TestTokens;

/**
 * Drives the field sheet endpoints over HTTP against stubs of poi and the object store, with tokens
 * signed the way staff signs them: a field agent gets an upload link and submits; a reviewer reads
 * the field sheet and its scan. Each endpoint admits only a person's token for sgt, acting in the
 * endpoint's role.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FieldSheetControllerIT extends PostgresIntegrationTest {

    private static final String TASK = "2025-2030-01-03-0032";
    private static final String SCAN_KEY = "field-sheets/scan.pdf";
    private static final UUID FIELD_AGENT = UUID.randomUUID();
    private static final UUID REVIEWER = UUID.randomUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskLedger taskLedger;

    @MockitoBean
    private ScanStore scanStore;

    @Test
    void theApiDocsAreServedWithoutAToken() throws Exception {
        this.mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paths['/api/v1/field-sheets'].get.tags[0]").value("fieldsheets"))
            .andExpect(jsonPath("$.components.securitySchemes['bearer-token'].scheme").value("bearer"))
            .andExpect(jsonPath("$.security[0]['bearer-token']").isArray());
        this.mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(status().isOk());
    }

    @Test
    void anUploadLinkIsSignedForAFreshKey() throws Exception {
        this.mockMvc.perform(post("/api/v1/field-sheets/scan-uploads").header("Authorization", asFieldAgent()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.key").isNotEmpty())
            .andExpect(jsonPath("$.url").isNotEmpty());
    }

    @Test
    void aSubmittedFieldSheetIsReadBackWithItsScan() throws Exception {
        ScanKey key = new ScanKey(SCAN_KEY);
        when(this.scanStore.inspect(key)).thenReturn(new FieldSheetScan(key, 1024));

        String created = this.submit(asFieldAgent())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value(matchesPattern("^FI-\\d{4}-\\d{4}$")))
            .andExpect(jsonPath("$.taskCode").value(TASK))
            .andExpect(jsonPath("$.status").value("SUBMITTED"))
            .andReturn()
            .getResponse()
            .getContentAsString();
        String id = created.replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        this.mockMvc.perform(get("/api/v1/field-sheets/{id}", id).header("Authorization", asReviewer()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskCode").value(TASK))
            .andExpect(jsonPath("$.submittedBy").value(FIELD_AGENT.toString()))
            .andExpect(jsonPath("$.beneficiaryDocumentNumber").doesNotExist());
        this.mockMvc.perform(get("/api/v1/field-sheets/{id}/scan", id).header("Authorization", asReviewer()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.key").value(SCAN_KEY));
    }

    @Test
    void eachSubmissionIsNumberedAfterTheYearsLast() throws Exception {
        ScanKey key = new ScanKey(SCAN_KEY);
        when(this.scanStore.inspect(key)).thenReturn(new FieldSheetScan(key, 1024));

        int first = this.numberOf(this.submit(asFieldAgent()));
        int second = this.numberOf(this.submit(asFieldAgent()));

        assertEquals(first + 1, second);
    }

    @Test
    void aPageOfFieldSheetsIsServedWithItsDefaults() throws Exception {
        this.mockMvc.perform(get("/api/v1/field-sheets").header("Authorization", asReviewer()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.actualPage").value(0))
            .andExpect(jsonPath("$.pageSize").value(10));
        this.mockMvc.perform(get("/api/v1/field-sheets").param("size", "0").header("Authorization", asReviewer()))
            .andExpect(status().isUnprocessableContent());
    }

    @Test
    void aFieldSheetWithoutAnUploadedScanIsRefused() throws Exception {
        when(this.scanStore.inspect(any())).thenThrow(
            new ScanNotUploadedException(new ScanKey(SCAN_KEY))
        );

        this.submit(asFieldAgent()).andExpect(status().isUnprocessableContent());
    }

    @Test
    void aRequestWithoutATokenIsAnsweredWithTheUsualEnvelope() throws Exception {
        this.mockMvc.perform(get("/api/v1/field-sheets").param("status", "SUBMITTED"))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string("WWW-Authenticate", startsWith("Bearer")))
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void aTokenStaffDidNotIssueToAPersonForSgtIsRefused() throws Exception {
        String subject = FIELD_AGENT.toString();
        List<String> roles = List.of("field-agent");
        List<String> refused = List.of(
            TestTokens.issue("http://elsewhere.test", TestTokens.AUDIENCE, "user", subject, roles),
            TestTokens.issue(TestTokens.ISSUER, "report-api-dev", "user", subject, roles),
            TestTokens.issue(TestTokens.ISSUER, TestTokens.AUDIENCE, "service", subject, roles)
        );

        for (String token : refused) {
            this.mockMvc.perform(get("/api/v1/field-sheets")
                    .param("status", "SUBMITTED")
                    .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isUnauthorized());
        }
    }

    @Test
    void aFieldAgentCannotReview() throws Exception {
        this.mockMvc.perform(post("/api/v1/field-sheets/{id}/rejection", UUID.randomUUID())
                .header("Authorization", asFieldAgent())
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void aReviewerCannotSubmit() throws Exception {
        this.submit(asReviewer()).andExpect(status().isForbidden());
    }

    @Test
    void someoneActingInNoRoleCannotSubmit() throws Exception {
        String undecided = "Bearer " + TestTokens.person(UUID.randomUUID());

        this.submit(undecided).andExpect(status().isForbidden());
    }

    @Test
    void aRoleSgtDoesNotDefineIsRefused() throws Exception {
        String unknown = "Bearer " + TestTokens.person(FIELD_AGENT, "field-agent", "auditor");

        this.submit(unknown).andExpect(status().isForbidden());
    }

    private int numberOf(ResultActions submission) throws Exception {
        String created = submission.andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return Integer.parseInt(created.replaceAll(".*\"code\":\"FI-(\\d{4})-\\d{4}\".*", "$1"));
    }

    private ResultActions submit(String authorization) throws Exception {
        String body = """
            {"taskCode": "%s", "scanKey": "%s"}""".formatted(TASK, SCAN_KEY);

        return this.mockMvc.perform(post("/api/v1/field-sheets")
            .header("Authorization", authorization)
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
        );
    }

    private static String asFieldAgent() {
        return "Bearer " + TestTokens.person(FIELD_AGENT, "field-agent");
    }

    private static String asReviewer() {
        return "Bearer " + TestTokens.person(REVIEWER, "general-management-assistant");
    }
}

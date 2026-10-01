package lat.soulware.sgt.monitoring.infrastructure.clients.taskledger;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.PendingTasks;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.criteria.TaskByCode;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.projections.TaskProjection;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.PendingTaskResult;
import lat.soulware.sgt.monitoring.application.queries.fieldsheets.results.TaskResult;
import lat.soulware.sgt.monitoring.domain.model.exceptions.UnknownTaskException;
import lat.soulware.sgt.shared.infrastructure.exceptions.InfrastructureException;

/** Reads poi's tasks through its v1 task API. */
@Component
public class HttpTaskProjection implements TaskProjection {

    private static final String TASK_PATH = "/api/v1/tasks/{taskCode}";
    private static final String PENDING_PATH = "/api/v1/tasks/pending?activityCode={activityCode}";
    private static final String API_KEY_HEADER = "X-API-KEY";

    private final RestClient client;

    public HttpTaskProjection(
        RestClient.Builder builder,
        @Value("${monitoring.poi.base-url}") String baseUrl,
        @Value("${monitoring.poi.api-key}") String apiKey
    ) {
        this.client = builder
            .baseUrl(baseUrl)
            .defaultHeader(API_KEY_HEADER, apiKey)
            .build();
    }

    @Override
    public TaskResult findByCode(TaskByCode criteria) {
        try {
            TaskResponse task = this.client.get()
                .uri(TASK_PATH, criteria.code())
                .retrieve()
                .body(TaskResponse.class);

            return new TaskResult(task.code(), task.description(), task.status(), task.beneficiaryId());
        } catch (HttpClientErrorException.NotFound e) {
            throw new UnknownTaskException(criteria.code());
        } catch (RestClientException e) {
            throw new InfrastructureException("Reading task %s failed".formatted(criteria.code()), e);
        }
    }

    @Override
    public List<PendingTaskResult> findPending(PendingTasks criteria) {
        try {
            PendingTasksResponse pending = this.client.get()
                .uri(PENDING_PATH, criteria.activityCode())
                .retrieve()
                .body(PendingTasksResponse.class);

            return pending.tasks().stream()
                .map(task -> new PendingTaskResult(
                    task.code(),
                    task.description(),
                    task.activityCode(),
                    task.activityDescription()
                ))
                .toList();
        } catch (RestClientException e) {
            throw new InfrastructureException("Reading poi's pending tasks failed", e);
        }
    }

    private record TaskResponse(String code, String description, String status, String beneficiaryId) {
    }

    private record PendingTaskResponse(String code, String description, String activityCode, String activityDescription) {
    }

    private record PendingTasksResponse(List<PendingTaskResponse> tasks) {
    }
}

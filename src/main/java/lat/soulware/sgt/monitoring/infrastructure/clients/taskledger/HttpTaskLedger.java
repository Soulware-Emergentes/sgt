package lat.soulware.sgt.monitoring.infrastructure.clients.taskledger;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.DeliveryOutcome;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ExecutionDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.RelinkOutcome;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.RelinkResult;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.monitoring.domain.services.TaskLedger;
import lat.soulware.sgt.shared.infrastructure.clients.ServiceTokens;
import lat.soulware.sgt.shared.infrastructure.exceptions.InfrastructureException;

/**
 * Keeps poi's task ledger through its v1 task API. Every request names the field sheet by its id,
 * which poi keeps as the delivery's submission.
 *
 * <p>poi answers a delivery or a relink it cannot take with 404, for a task that does not exist, or
 * 422, for one that already has a delivery or was completed. Both are a refusal. Anything else that fails is the ledger being out
 * of reach, raised so the step is retried.
 */
@Component
public class HttpTaskLedger implements TaskLedger {

    private static final String DELIVERY_PATH = "/api/v1/tasks/{taskCode}/delivery";
    private static final String COMPLETION_PATH = "/api/v1/tasks/{taskCode}/completion";
    private static final String REOPENING_PATH = "/api/v1/tasks/{taskCode}/reopening";
    private static final String RELINK_PATH = DELIVERY_PATH + "/relink";
    private static final String POI_REGISTRATION = "poi";

    private final RestClient client;

    public HttpTaskLedger(
        RestClient.Builder builder,
        @Value("${monitoring.poi.base-url}") String baseUrl,
        ServiceTokens serviceTokens
    ) {
        this.client = builder
            .baseUrl(baseUrl)
            .requestInterceptor(serviceTokens.bearerFor(POI_REGISTRATION))
            .build();
    }

    @Override
    public DeliveryOutcome registerDelivery(
        TaskCode taskCode,
        FieldSheetId fieldSheetId,
        ExecutionDate executionDate
    ) {
        try {
            this.client.post()
                .uri(DELIVERY_PATH, taskCode.value())
                .body(new DeliveryRequest(fieldSheetId.value(), executionDate.value()))
                .retrieve()
                .toBodilessEntity();
            return DeliveryOutcome.ACCEPTED;
        } catch (HttpClientErrorException e) {
            if (isRefusal(e.getStatusCode())) {
                return DeliveryOutcome.REFUSED;
            }
            throw new InfrastructureException("Delivery of task %s failed".formatted(taskCode.value()), e);
        } catch (RestClientException e) {
            throw new InfrastructureException("Delivery of task %s failed".formatted(taskCode.value()), e);
        }
    }

    @Override
    public RelinkOutcome relink(TaskCode taskCode, TaskCode fromTaskCode, FieldSheetId fieldSheetId) {
        try {
            TaskResponse left = this.client.post()
                .uri(RELINK_PATH, taskCode.value())
                .body(new RelinkRequest(fieldSheetId.value(), fromTaskCode.value()))
                .retrieve()
                .body(TaskResponse.class);
            boolean wasSwapped = left.submissionId() != null;

            return wasSwapped
                ? new RelinkOutcome(RelinkResult.SWAPPED, new FieldSheetId(left.submissionId()))
                : new RelinkOutcome(RelinkResult.MOVED, null);
        } catch (HttpClientErrorException e) {
            if (isRefusal(e.getStatusCode())) {
                return new RelinkOutcome(RelinkResult.REFUSED, null);
            }
            throw new InfrastructureException("Relinking to task %s failed".formatted(taskCode.value()), e);
        } catch (RestClientException e) {
            throw new InfrastructureException("Relinking to task %s failed".formatted(taskCode.value()), e);
        }
    }

    @Override
    public void complete(TaskCode taskCode, FieldSheetId fieldSheetId) {
        this.report(COMPLETION_PATH, taskCode, fieldSheetId, "Completing");
    }

    @Override
    public void reopen(TaskCode taskCode, FieldSheetId fieldSheetId) {
        this.report(REOPENING_PATH, taskCode, fieldSheetId, "Reopening");
    }

    private void report(String path, TaskCode taskCode, FieldSheetId fieldSheetId, String action) {
        try {
            this.client.post()
                .uri(path, taskCode.value())
                .body(new ReviewedDeliveryRequest(fieldSheetId.value()))
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientException e) {
            throw new InfrastructureException(
                "%s task %s failed".formatted(action, taskCode.value()),
                e
            );
        }
    }

    private static boolean isRefusal(HttpStatusCode status) {
        return status.isSameCodeAs(HttpStatus.NOT_FOUND) || status.isSameCodeAs(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    private record DeliveryRequest(UUID submissionId, LocalDate executionDate) {
    }

    private record RelinkRequest(UUID submissionId, String fromTaskCode) {
    }

    /** The task a relinked delivery left, naming the field sheet swapped onto it, if any. */
    private record TaskResponse(UUID submissionId) {
    }

    private record ReviewedDeliveryRequest(UUID submissionId) {
    }
}

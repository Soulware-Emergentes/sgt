package lat.soulware.sgt.monitoring.domain.services;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.DeliveryOutcome;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.ExecutionDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.RelinkOutcome;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.TaskCode;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.shared.domain.services.DomainService;

/**
 * poi's record of which tasks have a delivery, and how each was reviewed. A refusal is an answer, not
 * a failure; a ledger that cannot be reached fails instead, so the step is retried.
 *
 * <p>Every step names the field sheet that made the delivery, so a step retried after it already
 * reached poi changes nothing there.
 */
public interface TaskLedger extends DomainService {

    /**
     * Registers a delivery for a task, which takes it off poi's pending list.
     *
     * @param taskCode      the task the delivery is for
     * @param fieldSheetId  the field sheet making the delivery
     * @param executionDate the day the field sheet was submitted
     * @return whether poi accepted the delivery
     */
    DeliveryOutcome registerDelivery(
        TaskCode taskCode,
        FieldSheetId fieldSheetId,
        ExecutionDate executionDate
    );

    /**
     * Relinks a field sheet's delivery to another task, swapping it with the delivery that task held.
     * A relink already done is answered the same way again.
     *
     * @param taskCode     the task the delivery is relinked to
     * @param fromTaskCode the task holding it
     * @param fieldSheetId the field sheet that made the delivery
     * @return whether poi moved it, swapped it, or refused
     */
    RelinkOutcome relink(TaskCode taskCode, TaskCode fromTaskCode, FieldSheetId fieldSheetId);

    /**
     * Completes a task whose delivery was reviewed and confirmed.
     *
     * @param taskCode     the task
     * @param fieldSheetId the approved field sheet that made the delivery
     */
    void confirmCompletion(TaskCode taskCode, FieldSheetId fieldSheetId);

    /**
     * Puts a task back on poi's pending list, its delivery rejected on review.
     *
     * @param taskCode     the task
     * @param fieldSheetId the rejected field sheet that made the delivery
     */
    void rejectDelivery(TaskCode taskCode, FieldSheetId fieldSheetId);
}

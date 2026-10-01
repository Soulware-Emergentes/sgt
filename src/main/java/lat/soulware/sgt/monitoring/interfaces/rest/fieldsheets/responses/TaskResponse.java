package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses;

/**
 * A poi task, as a reviewer checks a field sheet's evidence against it.
 *
 * @param code          the task's code
 * @param description   what the task is
 * @param status        where the task stands in poi
 * @param beneficiaryId the beneficiary the task is planned to visit
 */
public record TaskResponse(String code, String description, String status, String beneficiaryId) {
}

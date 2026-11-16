package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests;

/**
 * A relink to the task the field sheet's evidence belongs to.
 *
 * @param taskCode the task's code
 */
public record RelinkFieldSheetRequest(String taskCode) {
}

package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests;

/**
 * A field sheet submitted for a poi task, its scanned PDF already uploaded.
 *
 * @param taskCode the task the field sheet is submitted for
 * @param scanKey  where the scanned PDF was uploaded
 */
public record SubmitFieldSheetRequest(String taskCode, String scanKey) {
}

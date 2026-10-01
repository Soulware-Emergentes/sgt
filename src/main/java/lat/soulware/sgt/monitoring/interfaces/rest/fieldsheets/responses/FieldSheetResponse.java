package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * One field sheet.
 *
 * @param id                        the field sheet's identity
 * @param taskCode                  the poi task it is linked to
 * @param status                    where it stands
 * @param beneficiaryDocumentType   the evaluated beneficiary's document type, null until approved
 * @param beneficiaryDocumentNumber the evaluated beneficiary's document number, null until approved
 * @param usedSupplies              the inputs used, empty until approved
 * @param evaluationDate            when the evaluation took place, null until approved
 * @param securementDate            the securement date, null until approved or when none is stated
 * @param submittedBy               the field agent who submitted it
 * @param submissionDate            when it was submitted
 * @param reviewedBy                who reviewed it, null until reviewed
 * @param reviewDate                when it was reviewed, null until reviewed
 */
public record FieldSheetResponse(
    UUID id,
    String taskCode,
    String status,
    String beneficiaryDocumentType,
    String beneficiaryDocumentNumber,
    List<SupplyResponse> usedSupplies,
    LocalDateTime evaluationDate,
    LocalDateTime securementDate,
    UUID submittedBy,
    LocalDateTime submissionDate,
    UUID reviewedBy,
    LocalDateTime reviewDate
) {
}

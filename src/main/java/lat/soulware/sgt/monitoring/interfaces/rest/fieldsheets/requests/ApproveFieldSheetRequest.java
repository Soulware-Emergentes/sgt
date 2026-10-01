package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * An approval, with what the reviewer confirmed the scanned PDF says.
 *
 * @param reviewer                  the general management assistant approving it
 * @param beneficiaryDocumentType   the evaluated beneficiary's document type
 * @param beneficiaryDocumentNumber the evaluated beneficiary's document number
 * @param usedSupplies              the inputs the beneficiary used
 * @param evaluationDate            when the evaluation took place
 * @param securementDate            the securement date, null when the PDF states none
 */
public record ApproveFieldSheetRequest(
    UUID reviewer,
    String beneficiaryDocumentType,
    String beneficiaryDocumentNumber,
    List<SupplyRequest> usedSupplies,
    LocalDateTime evaluationDate,
    LocalDateTime securementDate
) {
}

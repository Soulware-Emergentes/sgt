package lat.soulware.sgt.monitoring.application.commands.fieldsheets.intent;

import java.util.List;

import lat.soulware.sgt.monitoring.domain.model.valueobjects.EvaluationDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.SecurementDate;
import lat.soulware.sgt.monitoring.domain.model.valueobjects.Supply;
import lat.soulware.sgt.monitoring.publishedlanguage.FieldSheetId;
import lat.soulware.sgt.people.publishedlanguage.BeneficiaryLegalDocument;
import lat.soulware.sgt.people.publishedlanguage.GeneralManagementAssistantId;
import lat.soulware.sgt.shared.application.commands.intent.CommandIntent;

/**
 * Approve a field sheet under review, confirming what its scanned PDF says.
 *
 * @param fieldSheetId   the field sheet under review
 * @param reviewer       the general management assistant reviewing it
 * @param beneficiary    the evaluated beneficiary's legal document, as the PDF states it
 * @param usedSupplies   the inputs the beneficiary used, as the PDF states them
 * @param evaluationDate when the evaluation took place
 * @param securementDate the securement date, null when the PDF states none
 */
public record ApproveFieldSheet(
    FieldSheetId fieldSheetId,
    GeneralManagementAssistantId reviewer,
    BeneficiaryLegalDocument beneficiary,
    List<Supply> usedSupplies,
    EvaluationDate evaluationDate,
    SecurementDate securementDate
) implements CommandIntent {
}

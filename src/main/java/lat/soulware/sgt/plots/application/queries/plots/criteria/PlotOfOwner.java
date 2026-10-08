package lat.soulware.sgt.plots.application.queries.plots.criteria;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * The plot registered to a beneficiary, named by their legal document.
 *
 * @param documentType   the kind of document, by name
 * @param documentNumber the document number
 */
public record PlotOfOwner(String documentType, String documentNumber) implements QueryCriteria {
}

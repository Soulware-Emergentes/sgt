package lat.soulware.sgt.plots.application.queries.plots.criteria;

import lat.soulware.sgt.shared.application.queries.criteria.QueryCriteria;

/**
 * The plots lying in an administrative region, at any level of the ubigeo.
 *
 * @param region the first two digits of a ubigeo for a department, four for a province, or all six
 *               for a district
 */
public record PlotsInRegion(String region) implements QueryCriteria {
}

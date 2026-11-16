package lat.soulware.sgt.plots.domain.services;

import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.Ubigeo;
import lat.soulware.sgt.shared.domain.services.DomainService;

/** Tells which district of Peru a plot lies in, from where its boundary is on the ground. */
public interface PlotDistricts extends DomainService {

    /**
     * @param boundary the plot's perimeter, in any UTM zone
     * @return the INEI ubigeo of the district holding most of the plot's area; fails with
     *         {@link lat.soulware.sgt.plots.domain.model.exceptions.PlotOutsideDistrictsException}
     *         when the plot lies in no district
     */
    Ubigeo districtOf(PlotBoundary boundary);
}

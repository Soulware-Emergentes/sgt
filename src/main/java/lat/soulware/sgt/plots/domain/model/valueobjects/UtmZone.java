package lat.soulware.sgt.plots.domain.model.valueobjects;

import lat.soulware.sgt.shared.domain.model.valueobjects.ValueObject;

/**
 * The WGS84 UTM zone a survey's coordinates are projected in. Peru spans the three southern zones
 * listed here, and an easting and northing mean nothing without the zone they were measured in.
 */
public enum UtmZone implements ValueObject {
    ZONE_17S(32717),
    ZONE_18S(32718),
    ZONE_19S(32719);

    private final int epsgCode;

    UtmZone(int epsgCode) {
        this.epsgCode = epsgCode;
    }

    /** @return the EPSG code identifying this zone's coordinate reference system */
    public int epsgCode() {
        return this.epsgCode;
    }
}

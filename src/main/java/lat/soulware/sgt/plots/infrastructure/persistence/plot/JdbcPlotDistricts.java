package lat.soulware.sgt.plots.infrastructure.persistence.plot;

import java.util.List;

import org.locationtech.jts.geom.Polygon;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lat.soulware.sgt.plots.domain.model.exceptions.PlotOutsideDistrictsException;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.plots.domain.model.valueobjects.Ubigeo;
import lat.soulware.sgt.plots.domain.services.PlotDistricts;

/**
 * Finds a plot's district among the boundaries in the {@code districts} table. The plot is moved
 * from its UTM zone onto WGS84, where the districts are, and the GiST index narrows them to the few
 * it touches. A plot across a district line goes to the district holding most of it; comparing
 * areas in square degrees is enough for that, since the districts it touches are side by side.
 */
@Component
public class JdbcPlotDistricts implements PlotDistricts {

    private static final String DISTRICT_HOLDING_MOST = """
        SELECT ubigeo
        FROM districts, ST_Transform(ST_GeomFromText(:wkt, :srid), 4326) AS plot
        WHERE ST_Intersects(boundary, plot)
        ORDER BY ST_Area(ST_Intersection(boundary, plot)) DESC
        LIMIT 1""";

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcPlotDistricts(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Ubigeo districtOf(PlotBoundary boundary) {
        Polygon polygon = PlotGeometries.toPolygon(boundary);
        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("wkt", polygon.toText())
            .addValue("srid", polygon.getSRID());

        List<String> ubigeos = this.jdbc.queryForList(DISTRICT_HOLDING_MOST, parameters, String.class);
        if (ubigeos.isEmpty()) {
            throw new PlotOutsideDistrictsException();
        }
        return new Ubigeo(ubigeos.getFirst());
    }
}

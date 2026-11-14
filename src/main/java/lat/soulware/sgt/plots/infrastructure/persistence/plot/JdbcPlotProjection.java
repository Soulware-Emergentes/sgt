package lat.soulware.sgt.plots.infrastructure.persistence.plot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotById;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotOfOwner;
import lat.soulware.sgt.plots.application.queries.plots.criteria.PlotsInRegion;
import lat.soulware.sgt.plots.application.queries.plots.projections.PlotProjection;
import lat.soulware.sgt.plots.application.queries.plots.results.PlotResult;
import lat.soulware.sgt.plots.application.queries.plots.results.VertexResult;
import lat.soulware.sgt.plots.domain.model.valueobjects.PlotBoundary;
import lat.soulware.sgt.shared.infrastructure.exceptions.InfrastructureException;

/**
 * Reads plots from the {@code plots} table. The boundary comes back as well-known text in its own
 * SRID and goes through {@link PlotGeometries}, the same mapping the repository stores it with, so
 * a plot reads the way it was surveyed.
 */
@Component
public class JdbcPlotProjection implements PlotProjection {

    private static final String COLUMNS = """
        SELECT id, owner_document_type, owner_document_number, ubigeo, registration_date,
            ST_AsText(boundary) AS boundary_wkt, ST_SRID(boundary) AS boundary_srid
        FROM plots
        """;

    private static final String FIND_BY_ID = COLUMNS + """
        WHERE id = :id""";

    private static final String FIND_IN_REGION = COLUMNS + """
        WHERE ubigeo LIKE :region || '%'
        ORDER BY ubigeo, registration_date""";

    private static final String FIND_OF_OWNER = COLUMNS + """
        WHERE owner_document_type = :documentType AND owner_document_number = :documentNumber""";

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcPlotProjection(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<PlotResult> findById(PlotById criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("id", criteria.id());

        return this.jdbc.query(FIND_BY_ID, parameters, (row, rowNumber) -> JdbcPlotProjection.toResult(row))
            .stream()
            .findFirst();
    }

    @Override
    public List<PlotResult> findInRegion(PlotsInRegion criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource("region", criteria.region());

        return this.jdbc.query(FIND_IN_REGION, parameters, (row, rowNumber) -> JdbcPlotProjection.toResult(row));
    }

    @Override
    public Optional<PlotResult> findOfOwner(PlotOfOwner criteria) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("documentType", criteria.documentType())
            .addValue("documentNumber", criteria.documentNumber());

        return this.jdbc.query(FIND_OF_OWNER, parameters, (row, rowNumber) -> JdbcPlotProjection.toResult(row))
            .stream()
            .findFirst();
    }

    private static PlotResult toResult(ResultSet row) throws SQLException {
        PlotBoundary boundary = PlotGeometries.toBoundary(JdbcPlotProjection.polygonOf(row));
        List<VertexResult> vertices = boundary.vertices().stream()
            .map(vertex -> new VertexResult(vertex.easting(), vertex.northing()))
            .toList();

        return new PlotResult(
            row.getObject("id", UUID.class),
            row.getString("owner_document_type"),
            row.getString("owner_document_number"),
            row.getString("ubigeo"),
            boundary.zone().name(),
            vertices,
            row.getObject("registration_date", LocalDateTime.class)
        );
    }

    private static Polygon polygonOf(ResultSet row) throws SQLException {
        String wkt = row.getString("boundary_wkt");
        int srid = row.getInt("boundary_srid");

        try {
            Polygon polygon = (Polygon) new WKTReader(new GeometryFactory()).read(wkt);
            polygon.setSRID(srid);
            return polygon;
        } catch (ParseException e) {
            throw new InfrastructureException("Stored plot boundary is not well-known text: %s".formatted(wkt), e);
        }
    }
}

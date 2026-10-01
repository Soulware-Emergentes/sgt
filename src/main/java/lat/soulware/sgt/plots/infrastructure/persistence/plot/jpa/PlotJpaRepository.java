package lat.soulware.sgt.plots.infrastructure.persistence.plot.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** The Spring Data delegate behind {@code JpaPlotRepositoryAdapter}. */
public interface PlotJpaRepository extends JpaRepository<PlotEntity, UUID> {

    /**
     * Compares on {@code footprint}, so plots surveyed in different UTM zones meet, and the GiST
     * index on it narrows the scan to the plots nearby.
     *
     * @param wkt  the boundary as well-known text, in the coordinates of {@code srid}
     * @param srid the EPSG code of the boundary's UTM zone
     * @return the stored plots touching or overlapping the boundary
     */
    @Query(nativeQuery = true, value = """
        SELECT p.id, p.boundary
        FROM plots p
        WHERE ST_Intersects(p.footprint, ST_Transform(ST_GeomFromText(:wkt, :srid), 4326)::geography)""")
    List<PlotEntity> findIntersecting(@Param("wkt") String wkt, @Param("srid") int srid);
}

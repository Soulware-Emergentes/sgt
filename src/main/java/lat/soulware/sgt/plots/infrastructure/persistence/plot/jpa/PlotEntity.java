package lat.soulware.sgt.plots.infrastructure.persistence.plot.jpa;

import java.util.UUID;

import org.locationtech.jts.geom.Polygon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The row storing a plot. The polygon carries the SRID of the UTM zone it was surveyed in, which is
 * how the zone survives the round trip. The table's derived {@code footprint} column is left
 * unmapped: Postgres writes it and only the overlap query reads it.
 */
@Entity
@Table(name = "plots")
public class PlotEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Polygon boundary;

    /** For JPA. */
    protected PlotEntity() {
    }

    public PlotEntity(UUID id, Polygon boundary) {
        this.id = id;
        this.boundary = boundary;
    }

    public UUID getId() {
        return this.id;
    }

    public Polygon getBoundary() {
        return this.boundary;
    }
}

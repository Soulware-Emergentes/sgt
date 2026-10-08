package lat.soulware.sgt.plots.infrastructure.persistence.plot.jpa;

import java.time.LocalDateTime;
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

    @Column(name = "owner_document_type", nullable = false)
    private String ownerDocumentType;

    @Column(name = "owner_document_number", nullable = false)
    private String ownerDocumentNumber;

    @Column(nullable = false)
    private Polygon boundary;

    @Column(nullable = false)
    private String ubigeo;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    /** For JPA. */
    protected PlotEntity() {
    }

    public PlotEntity(
        UUID id,
        String ownerDocumentType,
        String ownerDocumentNumber,
        Polygon boundary,
        String ubigeo,
        LocalDateTime registrationDate
    ) {
        this.id = id;
        this.ownerDocumentType = ownerDocumentType;
        this.ownerDocumentNumber = ownerDocumentNumber;
        this.boundary = boundary;
        this.ubigeo = ubigeo;
        this.registrationDate = registrationDate;
    }

    public UUID getId() {
        return this.id;
    }

    public String getOwnerDocumentType() {
        return this.ownerDocumentType;
    }

    public String getOwnerDocumentNumber() {
        return this.ownerDocumentNumber;
    }

    public Polygon getBoundary() {
        return this.boundary;
    }

    public String getUbigeo() {
        return this.ubigeo;
    }

    public LocalDateTime getRegistrationDate() {
        return this.registrationDate;
    }
}

package lat.soulware.sgt.monitoring.infrastructure.persistence.fieldsheets.jpa;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** One supply of a field sheet, as a row of {@code field_sheet_supplies}. */
@Embeddable
public class SupplyRow {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String unit;

    /** For JPA. */
    protected SupplyRow() {
    }

    public SupplyRow(String name, BigDecimal amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public String getUnit() {
        return this.unit;
    }
}

package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.responses;

import java.math.BigDecimal;

/**
 * One input a beneficiary used.
 *
 * @param name   what the input is
 * @param amount how much of it
 * @param unit   the unit the amount is in
 */
public record SupplyResponse(String name, BigDecimal amount, String unit) {
}

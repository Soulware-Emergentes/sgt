package lat.soulware.sgt.monitoring.interfaces.rest.fieldsheets.requests;

import java.math.BigDecimal;

/**
 * One input a beneficiary used, as the scanned PDF states it.
 *
 * @param name   what the input is
 * @param amount how much of it
 * @param unit   the unit the amount is in
 */
public record SupplyRequest(String name, BigDecimal amount, String unit) {
}

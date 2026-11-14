package lat.soulware.sgt.monitoring.application.queries.fieldsheets.results;

import java.math.BigDecimal;

import lat.soulware.sgt.shared.application.queries.results.QueryResult;

/**
 * One input a beneficiary used, as the reviewer confirmed it.
 *
 * @param name   what the input is
 * @param amount how much of it
 * @param unit   the unit the amount is in, by name
 */
public record SupplyResult(String name, BigDecimal amount, String unit) implements QueryResult {
}

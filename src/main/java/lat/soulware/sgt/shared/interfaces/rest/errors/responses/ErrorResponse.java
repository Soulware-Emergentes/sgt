package lat.soulware.sgt.shared.interfaces.rest.errors.responses;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Envelope returned for any domain failure. The status appears in the body as well as on the
 * response.
 *
 * @param status    the HTTP status code
 * @param error     the status reason phrase
 * @param code      which failure it is, as its message key, for a client to tell failures apart;
 *                  omitted for one that is not the domain's
 * @param message   the failure, resolved against the caller's locale
 * @param details   what the client may act on beyond the message, omitted when there is nothing
 * @param reference the request identifier the failure was logged under, omitted when absent
 * @param timestamp when the response was built
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    int status,
    String error,
    String code,
    String message,
    Map<String, Object> details,
    String reference,
    Instant timestamp
) {

    /**
     * Builds an envelope for a domain failure, which leaves no log entry to point at.
     *
     * @param status  the status this failure maps to
     * @param code    the failure's message key
     * @param message the resolved message
     * @param details the failure's details, null when it carries none
     * @return the envelope to serialize
     */
    public static ErrorResponse of(HttpStatus status, String code, String message, Map<String, Object> details) {
        return new ErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            code,
            message,
            details == null ? null : Map.copyOf(details),
            null,
            Instant.now()
        );
    }

    /**
     * Builds an envelope for a failure that leaves no log entry to point at.
     *
     * @param status  the status this failure maps to
     * @param message the resolved message
     * @return the envelope to serialize
     */
    public static ErrorResponse of(HttpStatus status, String message) {
        return of(status, message, null);
    }

    /**
     * Builds an envelope from the status the adapter chose, taking the code and the reason phrase
     * from it.
     *
     * @param status    the status this failure maps to
     * @param message   the resolved message
     * @param reference the request identifier the failure was logged under
     * @return the envelope to serialize
     */
    public static ErrorResponse of(HttpStatus status, String message, String reference) {
        return new ErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            null,
            message,
            null,
            reference,
            Instant.now()
        );
    }
}

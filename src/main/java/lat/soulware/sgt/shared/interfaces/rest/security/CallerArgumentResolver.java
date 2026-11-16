package lat.soulware.sgt.shared.interfaces.rest.security;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import lat.soulware.sgt.shared.domain.model.exceptions.UnauthenticatedException;

/**
 * Hands a controller the {@link Caller} its request's token names. This is the one class that reads
 * claims, and the claim names come from configuration, so a provider calling them something else is
 * a change of properties.
 */
@Component
public class CallerArgumentResolver implements HandlerMethodArgumentResolver {

    private final String subjectClaim;
    private final String rolesClaim;

    public CallerArgumentResolver(
        @Value("${sgt.security.claims.subject}") String subjectClaim,
        @Value("${sgt.security.claims.roles}") String rolesClaim
    ) {
        this.subjectClaim = subjectClaim;
        this.rolesClaim = rolesClaim;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Caller.class.equals(parameter.getParameterType());
    }

    @Override
    public Caller resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer container,
        NativeWebRequest request,
        WebDataBinderFactory binderFactory
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication)) {
            throw new UnauthenticatedException();
        }

        boolean isJwt = authentication.getPrincipal() instanceof Jwt;
        if (!isJwt) {
            throw new UnauthenticatedException();
        }

        Jwt token = (Jwt) authentication.getPrincipal();
        String subject = token.getClaimAsString(this.subjectClaim);
        List<String> roles = token.getClaimAsStringList(this.rolesClaim);
        boolean namesItsSubject = subject != null;
        if (!namesItsSubject) {
            throw new UnauthenticatedException();
        }

        return new Caller(toPersonId(subject), Role.fromClaim(roles));
    }

    private static UUID toPersonId(String subject) {
        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException e) {
            throw new UnauthenticatedException();
        }
    }
}

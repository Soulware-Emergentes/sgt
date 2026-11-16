package lat.soulware.sgt.shared.interfaces.rest.security;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Lets every controller take a {@link Caller} parameter. */
@Configuration
public class CallerConfiguration implements WebMvcConfigurer {

    private final CallerArgumentResolver callerArgumentResolver;

    public CallerConfiguration(CallerArgumentResolver callerArgumentResolver) {
        this.callerArgumentResolver = callerArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(this.callerArgumentResolver);
    }
}

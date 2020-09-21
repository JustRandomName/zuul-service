package zuul.filter;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.netflix.zuul.context.RequestContext.getCurrentContext;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zuul.service.SecurityService;

import javax.servlet.http.HttpServletRequest;

/***
 * @author n.zhuchkevich
 * @since 09/21/2020
 * Filter for every request
 */
@Component
public class PreRouteFilter extends ZuulFilter {

    private final SecurityService securityService;

    private static final String NOT_BEARER = "JWT Token does not begin with Bearer String";
    private static final String NOT_AUTHORIZED = "Not Authorized";
    private static final String STATUS_CODE = "error.status_code";

    @Autowired
    public PreRouteFilter(final SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * Run when filter get request. If request unknown or user not authorized error is appear
     */
    @Override
    public Object run() throws ZuulException {
        final RequestContext ctx = getCurrentContext();
        final HttpServletRequest request = ctx.getRequest();
        final String header = request.getHeader(AUTHORIZATION);

        if (securityService.isRequestValid(header, ctx)) {
            return null;
        } else {
            ctx.set(STATUS_CODE, SC_UNAUTHORIZED);
            throw new ZuulException(NOT_BEARER, SC_UNAUTHORIZED, NOT_AUTHORIZED);
        }
    }
}
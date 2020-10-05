package zuul.service;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zuul.client.SecurityClient;

import java.util.List;

/**
 * @author n.zhuchkevich
 * @since 09/21/2020
 * Default service for security client
 */
@Service
public class SecurityService {

    private final SecurityClient securityClient;

    private static final String AUTH_PATH = "/auth";
    private static final String ADMIN_PATH = "/admin";
    private static final String REGISTRATION = "/registration";
    private static final String CONFIRM = "/confirm";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Autowired
    public SecurityService(final SecurityClient securityClient) {
        this.securityClient = securityClient;
    }

    /**
     * @param ctx    request context
     * @param header token for authenticated
     */
    public boolean isRequestValid(final String header, final RequestContext ctx) {
        return isAuthRequest(ctx)
                || isRegistrationRequest(ctx)
                || isMainRequest(header)
                || isAdminRequest(header, ctx);
    }

    private boolean isAuthRequest(final RequestContext ctx) {
        return ctx.getRequest().getServletPath().contains(AUTH_PATH);
    }

    private boolean isRegistrationRequest(final RequestContext ctx) {
        return ctx.getRequest().getServletPath().contains(REGISTRATION)
                || ctx.getRequest().getServletPath().contains(CONFIRM);
    }

    private boolean isMainRequest(final String header) {
        return isNotBlank(header)
                && (getRoles(header).contains(ROLE_USER)
                || getRoles(header).contains(ROLE_ADMIN))
                && isValid(header);
    }

    private boolean isAdminRequest(final String header, final RequestContext ctx) {
        return isNotBlank(header)
                && ctx.getRequest().getServletPath().contains(ADMIN_PATH)
                && getRoles(header).contains(ROLE_ADMIN);
    }

    private boolean isValid(final String token) {
        return securityClient.isValid(token, token);
    }

    private List getRoles(final String token) {
        return securityClient.getRoles(token, token);
    }
}

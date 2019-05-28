package com.cannontech.web.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.api.errorHandler.model.ApiError;
import com.cannontech.web.api.token.AuthenticationException;
import com.cannontech.web.api.token.RequestContext;
import com.cannontech.web.api.token.TokenHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 *  Validate generated JWT token for all API request except (/api/token)
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final Logger log = YukonLogManager.getLogger(TokenAuthenticationFilter.class);

    @Autowired private YukonUserDao userDao;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Validate all api calls except login request
        boolean apiLoginrequest = ServletUtil.isPathMatch(request, Lists.newArrayList("/api/token"));

        if (!apiLoginrequest) {
            try {
                String authToken = TokenHelper.resolveToken(request);

                if (authToken != null) {
                    String userId = TokenHelper.getUserId(authToken); // validate token and get userId from claim
                    log.debug("Recieved API request " + request.getHeader("Host") + request.getContextPath() +  " for" + userId);

                    if (userId != null) {
                        LiteYukonUser user = userDao.getLiteYukonUser(Integer.valueOf(userId));
                        if (!user.isEnabled() || user.isForceReset()) {
                            throw new AuthenticationException("Expired user" + user.getUsername());
                        }
                        RequestContext.getContext().setLiteYukonUser(user);
                    }

                }
            } catch (Exception e) {
                buildApiError(response, e);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    /**
     *  Build API error to show Authentication message.
     */
    private void buildApiError(HttpServletResponse response, Exception e) throws IOException {
        String uniqueKey = CtiUtilities.getYKUniqueKey();
        log.error(uniqueKey + " Expired or invalid token" + e);
        final ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), "Authentication Required", uniqueKey);
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        PrintWriter out = response.getWriter();
        out.print(mapper.writeValueAsString(apiError));
        out.flush();
        out.close();
    }

    @Override
    public void initFilterBean() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }

}

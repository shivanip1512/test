package com.cannontech.web.filter;

import static java.nio.charset.StandardCharsets.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.api.dr.ecobee.EcobeeZeusJwtTokenAuthService;
import com.cannontech.web.api.errorHandler.ApiExceptionHandler;
import com.cannontech.web.api.token.AuthenticationException;
import com.cannontech.web.api.token.TokenHelper;
import com.google.common.collect.Lists;
/**
 *  Validate generated JWT token for all API request except (/api/token)
 */

public class TokenAuthenticationAndLoggingFilter extends OncePerRequestFilter {

    private static final Logger apiLog = YukonLogManager.getApiLogger();
    
    private static final List<String> apiLoginEndpoints = Lists.newArrayList(
        "/api/token", 
        "/api/refreshToken",
        "/api/logout", 
        "/api/forgottenPassword", 
        "/api/admin/config/currentTheme", 
        "/api/common/images/*"
    );
    
    private static final List<String> derEdgeEndpoints = Lists.newArrayList(
        "/api/unicastMessage", 
        "/api/multipointMessage", 
        "/api/broadcastMessage");
    
    private static final List<String> ecobeeRuntimeEndpoints = Lists.newArrayList("/api/ecobee/runtimeData");
    
    @Autowired private EcobeeZeusJwtTokenAuthService ecobeeZeusJwtTokenAuthService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserDao userDao;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        boolean apiLoginRequest = doesRequestMatchEndpoint(request, apiLoginEndpoints);

        long before = System.currentTimeMillis();
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Validate all api calls except login request
        if (!apiLoginRequest) {
            try {
                String authToken = TokenHelper.resolveToken(request);
                
                // Replace with Global settings (Runtime data Url) when we support ecobee data push
                boolean isEcobeeRuntimeApi = doesRequestMatchEndpoint(request, ecobeeRuntimeEndpoints);
                
                if (authToken != null && !isEcobeeRuntimeApi) {
                    String userId = TokenHelper.getUserId(authToken); // validate token and get userId from
                                                                      // claim
                    apiLog.debug("Received API request for " + request.getHeader("Host") + request.getContextPath()
                        + " for " + userId);

                    if (userId != null) {
                        LiteYukonUser user = userDao.getLiteYukonUser(Integer.valueOf(userId));
                        if (!user.isEnabled() || user.isForceReset()) {
                            throw new AuthenticationException("Expired user" + user.getUsername());
                        }
                        validateSetoUserEndpointRestrictions(user, request);
                        ApiRequestContext.getContext().setLiteYukonUser(user);
                    }

                } else if (isEcobeeRuntimeApi) {
                    ecobeeZeusJwtTokenAuthService.validateEcobeeJwtToken(authToken);
                    // We may need to do more here when the ecobee data push feature is supported 
                }
                
            } catch (Exception e) {
                buildApiError(request, response, e);
                return;
            }

        }
        chain.doFilter(requestWrapper, responseWrapper);
        if (!apiLoginRequest) {
            apiLogging(requestWrapper, responseWrapper, before);
        } else {
            responseWrapper.copyBodyToResponse();
        }
    }
    
    /**
     * Check if the request path matches one of the specified endpoints.
     */
    private boolean doesRequestMatchEndpoint(HttpServletRequest request, List<String> endpoints) {
        return ServletUtil.isPathMatch(request, endpoints);
    }
    
    /**
     * Check whether the user is a SETO DER Edge user (they have the role property). SETO users may not access any
     * API endpoints other than the DER Edge endpoints.
     * 
     * @throws NotAuthorizedException if it is a SETO user, but the request path is not a DER Edge endpoint.
     */
    private void validateSetoUserEndpointRestrictions(LiteYukonUser user, HttpServletRequest request) {
        boolean isSetoUser = rolePropertyDao.checkProperty(YukonRoleProperty.DER_EDGE_COORDINATOR_PERMISSION, user);
        if (isSetoUser && !doesRequestMatchEndpoint(request, derEdgeEndpoints)) {
            throw new NotAuthorizedException("DER Edge API user " + user.getUsername() + " may only access "
                    + "authentication and DER Edge API endpoints.");
        }
    }
    
    /**
     * Getting the request body using Wrapper.
     */
    private String getBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }
                return payload;
            }
        }
        return null;
    }

    /**
     * Logging API request/response except login request.
     */
    private void apiLogging(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper, long before)
            throws IOException, ServletException {

        String requestBody = getBody(requestWrapper);
        long after = System.currentTimeMillis();
        apiLog.info("Request URI: " + requestWrapper.getRequestURI() + " : " + (after - before)
            + "ms : HTTP Status " + responseWrapper.getStatus());
        if (apiLog.isDebugEnabled()) {
            if (StringUtils.isNotEmpty(requestBody)) {
                apiLog.debug("Request Body: \n" + JsonUtils.beautifyJson(requestBody));
            }
            String responseBody = IOUtils.toString(responseWrapper.getContentInputStream(), UTF_8);
            apiLog.debug("Response Body: \n " + JsonUtils.beautifyJson(responseBody));
        }
        responseWrapper.copyBodyToResponse();
    }

    /**
     *  Build API error to show Authentication or Authorization message.
     */
    private void buildApiError(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        String uniqueKey = CtiUtilities.getYKUniqueKey();
        if (e instanceof NotAuthorizedException) {
            apiLog.error(uniqueKey + "Access Denied", e);
            ApiExceptionHandler.notAuthorized(request, response, uniqueKey);
        } else {
            apiLog.error(uniqueKey + " Expired or invalid token", e);
            ApiExceptionHandler.authenticationRequired(request, response, uniqueKey);
        }
    }

    @Override
    public void initFilterBean() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }

}

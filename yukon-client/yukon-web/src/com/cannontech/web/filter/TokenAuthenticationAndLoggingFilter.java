package com.cannontech.web.filter;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.YukonUserDao;
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

    private final Logger apiLog = YukonLogManager.getApiLogger();

    @Autowired private YukonUserDao userDao;
    @Autowired private EcobeeZeusJwtTokenAuthService ecobeeZeusJwtTokenAuthService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        boolean apiLoginRequest = ServletUtil.isPathMatch(request, Lists.newArrayList("/api/token", "/api/refreshToken",
                "/api/logout", "/api/admin/config/currentTheme", "/api/common/images/*"));
        long before = System.currentTimeMillis();
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Validate all api calls except login request
        if (!apiLoginRequest) {
            try {
                String authToken = TokenHelper.resolveToken(request);
                //TODO Replace with Global settings (Runtime data Url)
                boolean isEcobeeRuntimeApi = ServletUtil.isPathMatch(request, Lists.newArrayList("/api/ecobee/runtimeData"));

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
                        ApiRequestContext.getContext().setLiteYukonUser(user);
                    }

                } else {
                    if (isEcobeeRuntimeApi) {
                        ecobeeZeusJwtTokenAuthService.validateEcobeeJwtToken(authToken);
                        // TODO Please discuss User info
                    }
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
     *  Build API error to show Authentication message.
     */
    private void buildApiError(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        String uniqueKey = CtiUtilities.getYKUniqueKey();
        apiLog.error(uniqueKey + " Expired or invalid token", e);
        ApiExceptionHandler.authorizationRequired(request, response, uniqueKey);
    }

    @Override
    public void initFilterBean() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }

}

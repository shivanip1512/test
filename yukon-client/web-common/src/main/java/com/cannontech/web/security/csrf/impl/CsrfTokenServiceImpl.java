package com.cannontech.web.security.csrf.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.web.util.WebUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.csrf.CsrfTokenService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;

public class CsrfTokenServiceImpl implements CsrfTokenService {
    private final Logger log = YukonLogManager.getLogger(CsrfTokenServiceImpl.class); 

    @Override
    public String getTokenForSession(HttpSession session) {
        String token = null;
        synchronized (WebUtils.getSessionMutex(session)) {
            token = (String) session.getAttribute(SESSION_CSRF_TOKEN);
            if (token == null) {
                //TODO: Remove this logging later. Added for YUK-18491.
                log.trace(SESSION_CSRF_TOKEN + " for session with id " + session.getId() + " is null");
                token = UUID.randomUUID().toString();
                log.trace("new " + SESSION_CSRF_TOKEN + " generate and set in session.");
                session.setAttribute(SESSION_CSRF_TOKEN, token);
            }
        }
        return token;
    }

    @Override
    public String getTokenFromRequest(HttpServletRequest request) {
        return request.getParameter(REQUEST_CSRF_TOKEN);
    }

    public void validateToken(HttpServletRequest request) throws SecurityException {
        if (request.getMethod().equalsIgnoreCase("GET")) {
            return;
        }
        String refHeader = request.getHeader("Referer");
        if (refHeader == null) {
            log.error("Missing referer header field for" + request.getRequestURL());
            throw new SecurityException("Missing referer header field for " + request.getRequestURL());
        }
        String host = null;
        try {
            host = new URI(refHeader).getHost();
        } catch (URISyntaxException e) {
            log.error("Unable to fetch the host from referer header");
        }

        boolean ok = host != null && (host.equals(request.getServerName()));
        if (!ok) {
            log.error("Request blocked due to referer header field being: " + refHeader);
            throw new SecurityException("Request blocked due to referer header field being: " + refHeader);
        }
        String requestToken = null;
        String payload = null;
        try (Scanner scanner = new Scanner(request.getInputStream(), "ISO-8859-1").useDelimiter("\\A")) {
            payload = scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            log.error("Error occurred in fetching the payload content for the request", e);
        }
        requestToken = getTokenFromRequest(request);
        if (ServletUtil.isAjaxRequest(request)) {
            if (requestToken == null && !Strings.isNullOrEmpty(payload)) {
                try {
                    Map<String, Object> data = JsonUtils.fromJson(payload, new TypeReference<Map<String, Object>>() {
                    });
                    Object mapValue = data.get(REQUEST_CSRF_TOKEN);
                    String stringify = JsonUtils.toJson(mapValue);
                    requestToken = JsonUtils.fromJson(stringify, JsonUtils.STRING_TYPE);
                } catch (IOException e) {
                    log.error("Error occurred in fetching the CSRF token from the request", e);
                }
            }
        }
        String actualToken = getTokenForSession(request.getSession());
        if (!actualToken.equals(requestToken)) {
            //TODO: Remove this logging later. Added for YUK-18491.
            if (ServletUtil.isAjaxRequest(request)) {
                log.trace(request.getRequestURI() + " is an ajax request.");
            } else {
                log.trace(request.getRequestURI() + " is not an ajax request.");
            }
            log.trace(SESSION_CSRF_TOKEN + " and " + REQUEST_CSRF_TOKEN + " in " + request.getRequestURI() +" did not match.");
            log.trace(request.getRequestURI() + SESSION_CSRF_TOKEN + ": " + actualToken);
            log.trace(request.getRequestURI() + REQUEST_CSRF_TOKEN + ": " + requestToken);
            log.trace("Payload for " + request.getRequestURI() + ": " + ((AuthenticationRequestWrapper)request).getPayload());
            request.getSession().removeAttribute(SESSION_CSRF_TOKEN); // owasp recomendation
            
            if (StringUtils.isEmpty(requestToken)) {
                throw new SecurityException("Request token not found: " + request.getRequestURL());
            } else {
                throw new SecurityException("Request token is invalid: " + request.getRequestURL());
            }
        }
    }
}

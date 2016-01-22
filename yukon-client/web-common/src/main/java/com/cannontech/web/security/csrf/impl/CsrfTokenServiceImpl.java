package com.cannontech.web.security.csrf.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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
                token = UUID.randomUUID().toString();
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
        String payload = null;
        String requestToken = null;
        try (Scanner scanner = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A")) {
            payload = scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            log.error("Error occured in fetching the payload content for the request", e);
        }
        if (ServletUtil.isAjaxRequest(request)) {
            if (Strings.isNullOrEmpty(payload)) {
                requestToken = getTokenFromRequest(request);
            } else {
                try {
                    Map<String, Object> data = JsonUtils.fromJson(payload, new TypeReference<Map<String, Object>>() {
                    });
                    Object mapValue = data.get(REQUEST_CSRF_TOKEN);
                    String stringify = JsonUtils.toJson(mapValue);
                    requestToken = JsonUtils.fromJson(stringify, JsonUtils.STRING_TYPE);
                } catch (IOException e) {
                    log.error("Error occured in fetching the CSRF token from the request", e);
                }
            }
        } else {
            requestToken = getTokenFromRequest(request);
        }
        String actualToken = getTokenForSession(request.getSession());
        if (!actualToken.equals(requestToken)) {
            request.getSession().removeAttribute(SESSION_CSRF_TOKEN); // owasp recomendation
            throw new SecurityException("Request token not found or invalid. " + request.getRequestURL());
        }
    }
}

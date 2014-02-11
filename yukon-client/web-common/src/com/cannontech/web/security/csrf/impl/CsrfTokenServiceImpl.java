package com.cannontech.web.security.csrf.impl;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.util.WebUtils;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.csrf.CsrfTokenService;

public class CsrfTokenServiceImpl implements CsrfTokenService {

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

    @Override
    public void validateToken(HttpServletRequest request) {
        if (ServletUtil.isAjaxRequest(request) || !request.getMethod().equalsIgnoreCase("POST")) {
            return;
        }

        String requestToken = getTokenFromRequest(request);
        String actualToken = getTokenForSession(request.getSession());

        if (!actualToken.equals(requestToken)) {
            request.getSession().removeAttribute(SESSION_CSRF_TOKEN); // owasp recomendation
            throw new SecurityException("Request token not found or invalid. " + request.getRequestURL());
        }
    }
}

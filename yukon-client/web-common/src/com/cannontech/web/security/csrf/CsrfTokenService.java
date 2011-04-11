package com.cannontech.web.security.csrf;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import com.cannontech.common.exception.BadAuthenticationException;
import com.cannontech.common.exception.YukonSecurityException;
import com.cannontech.common.util.StringFilters;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.roleproperties.enums.CsrfTokenMode;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.Iterables;

public class CsrfTokenService {
    private static final String sessionKey = "COM.CANNONTECH.CSRF.SESSIONTOKEN";
    private static final Pattern parameterPattern = Pattern.compile("^" + CsrfTokenHolder.prefix);
    
    private int maxTokens;
    private Duration maxLife;
    
    private AuthenticationService authenticationService;
    private RolePropertyDao rolePropertyDao;
    
    @PostConstruct
    public void initialize() {
        maxTokens = 4;
        maxLife = Duration.standardMinutes(15);
    }
    
    public String getToken(HttpServletRequest request) {
        CsrfTokenHolder tokenHolder = getTokenHolder(request);
        
        String token = tokenHolder.getToken();
        
        return token;
    }

    private CsrfTokenHolder getTokenHolder(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        CsrfTokenHolder tokenHolder = (CsrfTokenHolder)session.getAttribute(sessionKey);
        if (tokenHolder == null) {
            tokenHolder = new CsrfTokenHolder(maxTokens, maxLife);
            session.setAttribute(sessionKey, tokenHolder);
        }
        return tokenHolder;
    }
    
    public boolean checkRequest(HttpServletRequest request, BindingResult bindingResult) {
        String errorCode = checkRequest(request);
        
        if (StringUtils.isNotEmpty(errorCode)) {
            bindingResult.reject(errorCode);
        }
        
        return true;
    }

    public String checkRequest(HttpServletRequest request) {
        try {
            validateRequest(request);
        } catch (BadAuthenticationException e) {
            return "yukon.web.error.csrf.badPassword";
        } catch (YukonSecurityException e) {
            return "yukon.web.error.csrf.badToken";
        }
        
        return null;
    }

    
    public void validateRequest(HttpServletRequest request) throws BadAuthenticationException, YukonSecurityException {
        CsrfTokenMode mode = getTokenMode(request);
        if (mode == CsrfTokenMode.OFF) {
            return;
        }
        Iterable<String> parameterNames = Iterables.filter(request.getParameterMap().keySet(), String.class);
        String matchingParameter;
        try {
            matchingParameter = Iterables.find(parameterNames, StringFilters.getRegExFilter(parameterPattern));
        } catch (Exception e) {
            throw new YukonSecurityException("missing token");
        }
        CsrfTokenHolder tokenHolder = getTokenHolder(request);
        boolean valid = tokenHolder.validateToken(matchingParameter);
        if (!valid) {
            throw new YukonSecurityException("bad token");
        }
        
        if (mode == CsrfTokenMode.TOKEN) {
            return;
        }
        
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        String password = request.getParameter(matchingParameter);
        
        authenticationService.login(yukonUser.getUsername(), password);
    }

    public CsrfTokenMode getTokenMode(HttpServletRequest request) {
        LiteYukonUser yukonUser = ServletUtil.getYukonUser(request);
        CsrfTokenMode mode = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.CSRF_TOKEN_MODE, CsrfTokenMode.class, yukonUser);
        return mode;
    }
    
    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }

}

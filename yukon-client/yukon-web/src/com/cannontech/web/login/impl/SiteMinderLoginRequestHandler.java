package com.cannontech.web.login.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.authentication.service.impl.SimpleMessageDigestHasher;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.login.AbstractLoginRequestHandler;

public class SiteMinderLoginRequestHandler extends AbstractLoginRequestHandler {
    private static final String HEADER_PROPERTY_KEY = "SITE_MINDER_HEADER";
    private static final String SECRET_PROPERTY_KEY = "SITE_MINDER_SECRET";
    
    private SimpleMessageDigestHasher simpleHasher;
    private final Logger logger = YukonLogManager.getLogger(getClass());

    @Override
    public boolean handleLoginRequest(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        
        String headerName = config.getString(HEADER_PROPERTY_KEY, "");
        if (headerName.equals("")) return false;
        
        String loginToken = request.getHeader(headerName);
        if (loginToken == null) return false;
        logger.debug("Header found");

        String premiseNumber = ServletRequestUtils.getStringParameter(request, "PremiseNumber");
        String accountIdentifier = ServletRequestUtils.getStringParameter(request, "Account_Identifier");

        if (premiseNumber == null || accountIdentifier == null) return false;
        logger.debug("Premise number and Account Identifier found");
        
        String secret = config.getString(SECRET_PROPERTY_KEY, "");
        if (secret.equals("")) return false;
        logger.debug("Secret found");

        String input = premiseNumber + loginToken + secret;
        String hash = simpleHasher.hash(input);

        if (!hash.equalsIgnoreCase(accountIdentifier)) return false;
        logger.debug("Hash validation successful");

        LiteYukonUser user = yukonUserDao.getLiteYukonUser(premiseNumber);
        if (user == null) return false;
        logger.debug("User found for premise");

        loginService.createSession(request, user);
        return true;
    }
    
    public void setSimpleHasher(SimpleMessageDigestHasher simpleHasher) {
        this.simpleHasher = simpleHasher;
    }
    
}


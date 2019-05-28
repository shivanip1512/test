package com.cannontech.web.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.token.TokenHelper;

public class APIRequestHelper {
    
    private final static String authToken = "authToken";

    public static HttpHeaders getHttpHeaders(YukonUserContext userContext, HttpServletRequest request ) {
        Integer userId = userContext.getYukonUser().getUserID();
        HttpHeaders newheaders = new HttpHeaders();
        newheaders.setContentType(MediaType.APPLICATION_JSON);

        HttpSession session = request.getSession(false);
        String token = (String) session.getAttribute(authToken);

        if (token == null || TokenHelper.checkExpiredJwt(token)) {
            token = TokenHelper.createToken(userId);
            session.setAttribute(authToken, token);
        }

        newheaders.set("Authorization", "Bearer " + token);

        return newheaders;
    }

}

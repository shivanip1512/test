package com.cannontech.web.login;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.LiteYukonUser;

public interface LoginService {

    public boolean login(HttpServletRequest request, HttpServletResponse response, Cookie cookie) throws Exception;
    
    public boolean login(HttpServletRequest request, HttpServletResponse response, String username, String password, 
            boolean createRememberMeCookie) throws Exception;
    
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    public void clientLogin(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    public void outboundVoiceLogin(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    public void inboundVoiceLogin(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
    public LiteYukonUser internalLogin(HttpServletRequest request, HttpSession session, String username, boolean saveCurrentUser);
    
}

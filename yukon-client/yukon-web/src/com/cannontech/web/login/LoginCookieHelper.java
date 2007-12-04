package com.cannontech.web.login;

import java.security.GeneralSecurityException;

public interface LoginCookieHelper {
    
    public String createCookieValue(final String username, final String password) throws GeneralSecurityException;
    
    public UserPasswordHolder decodeCookieValue(String cryptValue) throws GeneralSecurityException;
    
}

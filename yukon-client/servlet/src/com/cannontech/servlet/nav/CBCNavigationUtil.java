package com.cannontech.servlet.nav;

import javax.servlet.http.HttpSession;

import com.cannontech.web.navigation.CtiNavObject;

public class CBCNavigationUtil {
    
    public CBCNavigationUtil() {
        super();
    }

    public void bookmarkLocation(String redirectURL, HttpSession session) {
        CtiNavObject navObject = getNavObject(session);
        navObject.getHistory().push(navObject.getCurrentPage());
        navObject.setNavigation(redirectURL);
    }
                
    private CtiNavObject getNavObject(HttpSession session) {
            return (CtiNavObject) session.getAttribute("CtiNavObject");
    }
        
    public String goBack(HttpSession session) {        
        if (getNavObject(session).getHistory().size() >= 1)
            return (String) getNavObject(session).getHistory().pop();
        else
            return "";  
    }  

}

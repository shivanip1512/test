package com.cannontech.servlet.nav;

import javax.servlet.http.HttpSession;

import com.cannontech.web.navigation.CtiNavObject;

public class CBCNavigationUtil {
    
    public CBCNavigationUtil() {
        super();
    }

    public static void bookmarkLocation(String redirectURL, HttpSession session) {
        CtiNavObject navObject = (CtiNavObject) session.getAttribute("CtiNavObject");
        navObject.getHistory().push(navObject.getCurrentPage());
        navObject.setNavigation(redirectURL);
    }
                        
    public static String goBack(HttpSession session) {        
        CtiNavObject navObject = (CtiNavObject) session.getAttribute("CtiNavObject");        
        if (navObject.getHistory().size() >= 1)
            return (String) navObject.getHistory().pop();
        else
            return "";  
    }  

}

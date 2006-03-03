package com.cannontech.servlet.nav;

import javax.servlet.http.HttpSession;

import com.cannontech.web.navigation.CtiNavObject;

public class CBCNavigationUtil {
    private static HttpSession session;
    
    private CBCNavigationUtil(){};
    
    private CBCNavigationUtil(HttpSession s) {
        super();
        session = s;
    }

    public void bookmarkLocation(String redirectURL) {
        CtiNavObject navObject = getNavObject();
        navObject.getHistory().push(navObject.getCurrentPage());
        navObject.setNavigation(redirectURL);
    }
        
        

    private static CtiNavObject getNavObject() {
            return (CtiNavObject) session.getAttribute("CtiNavObject");
    }
    
    public static CBCNavigationUtil getInstanceOf(HttpSession s) throws Exception {
        if (s != null)
           return new CBCNavigationUtil(s);
        else
            throw new Exception ("Session is null");
    }
    
    public  static String goBack() {        
        if (getNavObject().getHistory().size() >= 1)
            return (String) getNavObject().getHistory().pop();
        else
            return "";  
    }  

}

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
            return parseRedirect( (String) navObject.getHistory().pop());
        else
            return "";  
    }

	/*
	 * method that filters out the url strings that are bumped into the stack
	 * every time the navigation is done from the 3-tier view.
	 * From navigation point of view these pages are "intermediary"
	 * therefore they need to be skipped 
	 * */
    private static String parseRedirect(String string) {
		if (string.indexOf("capBankCmd") != -1) {
			return string.replaceAll("capBankCmd", "feeders");
		}
		if (string.indexOf("feederCmd") != -1) {
			return string.replaceAll("feederCmd", "feeders");
		}
		if (string.indexOf("subCmd") != -1) {
			return string.replaceAll("subCmd", "feeders");
		}		
		return string;
	}  

}

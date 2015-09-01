package com.cannontech.servlet.nav;

import javax.servlet.http.HttpSession;

import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;

public class CBCNavigationUtil {
    
	private static final String [] PAGES_TO_SKIP = {
													"bankMove.jsp",
													"cbcPointTimestamps.jsp",
													"cbcWizBase.jsf"
													};
    public CBCNavigationUtil() {
        super();
    }

    public static void bookmarkLocationAndRedirect(String redirectURL, HttpSession session) {
        CtiNavObject navObject = (CtiNavObject) session.getAttribute(ServletUtil.NAVIGATE);
        navObject.getHistory().push(navObject.getCurrentPage());
        navObject.setNavigation(redirectURL);
    }
    
    public static void redirect(String redirectURL, HttpSession session) {
        CtiNavObject navObject = (CtiNavObject) session.getAttribute(ServletUtil.NAVIGATE);
		if (navObject != null)
        navObject.setNavigation(redirectURL);
    }
    
    public static void bookmarkThisLocation(HttpSession session) {
        CtiNavObject navObject = (CtiNavObject) session.getAttribute(ServletUtil.NAVIGATE);
        navObject.getHistory().push(navObject.getCurrentPage());
    }
    
    public static void setNavigation(String url, HttpSession session) {
        CtiNavObject navObject = (CtiNavObject) session.getAttribute(ServletUtil.NAVIGATE);
        navObject.setNavigation(url);
    }
    
    public static String goBack(HttpSession session) {        
        CtiNavObject navObject = (CtiNavObject) session.getAttribute(ServletUtil.NAVIGATE);        
        String str = null;
        
        int size = navObject.getHistory().size();
        for (int i = 0; i < size; ++i) {
        	str = parseRedirect( navObject.getHistory().pop(), session);
        	if (str != null) {
            	break;
            }
        }
        
        if (str == null) {
        	str = "";
        }
        
        return str;
    }

	/*
	 * method that filters out the url strings that are bumped into the stack
	 * every time the navigation is done from the 3-tier view.
	 * From navigation point of view these pages are "intermediary"
	 * therefore they need to be skipped 
	 * */
    private static String parseRedirect(String string, HttpSession session) {
    	for (int i = 0; i < PAGES_TO_SKIP.length; i++) {
			String pageToSkip = PAGES_TO_SKIP[i];
			if (string.indexOf(pageToSkip) != -1) {
				return null;
			}
		}    		
		return string;
	} 
}

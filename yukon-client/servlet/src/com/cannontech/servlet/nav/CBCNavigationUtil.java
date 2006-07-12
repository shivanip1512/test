package com.cannontech.servlet.nav;

import javax.servlet.http.HttpSession;

import com.cannontech.web.navigation.CtiNavObject;

public class CBCNavigationUtil {
    
	private static final String [] PAGES_TO_SKIP = {"capBankCmd.jsp", 
													"feederCmd.jsp",
													"subCmd.jsp",
													"charts.jsp",
													"capBankMove.jsp",
													"tempmove.jsp",
													"cbcPointTimestamps.jsp"
													};
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
            return parseRedirect( (String) navObject.getHistory().pop(), session);
        else
            return "";  
    }

	/*
	 * method that filters out the url strings that are bumped into the stack
	 * every time the navigation is done from the 3-tier view.
	 * From navigation point of view these pages are "intermediary"
	 * therefore they need to be skipped 
	 * */
    private static String parseRedirect(String string, HttpSession session) {
    	CtiNavObject navObject = (CtiNavObject) session.getAttribute("CtiNavObject");	
    	for (int i = 0; i < PAGES_TO_SKIP.length; i++) {
			String pageToSkip = PAGES_TO_SKIP[i];
			if (string.indexOf(pageToSkip) != -1) {
				return navObject.getModuleExitPage();
			}
		}    		
		return string;
	} 
}

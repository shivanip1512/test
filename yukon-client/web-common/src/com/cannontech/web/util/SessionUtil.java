package com.cannontech.web.util;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import com.cannontech.common.constants.LoginController;
import com.cannontech.common.util.Pair;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;

public class SessionUtil {

    /**
     * Will return the userId of the parent login
     * 
     * If there is no parent login found in session, defaultUserId will be returned.
     * 
     * New logging requirements from Xcel indicate that we need to track the parent login in case 
     * this was from an internal login through the member management interface.
     * 
     * @param session
     * @return userId 
     */
    public static int getParentLoginUserId(HttpSession session, int defaultUserId) {
        int userId = defaultUserId;
        if (session != null) {
            Pair<?,?> p = (Pair<?,?>) session.getAttribute(LoginController.SAVED_YUKON_USERS);
            if (p != null) {
                Properties oldContext = (Properties) p.getFirst();
                Enumeration<?> attNames = oldContext.propertyNames();
                while (attNames.hasMoreElements()) {
                    String attName = (String) attNames.nextElement();
                    if(attName.compareTo( ServletUtils.ATT_STARS_YUKON_USER ) == 0) {
                        userId = ((StarsYukonUser) oldContext.get(attName)).getUserID();
                        break;
                    }
                }
            }
        }
        return userId;
    }
}

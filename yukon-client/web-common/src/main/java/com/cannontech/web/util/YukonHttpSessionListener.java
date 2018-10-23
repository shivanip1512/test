package com.cannontech.web.util;

import java.util.Date;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

@WebListener
public class YukonHttpSessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    private static final Logger log = YukonLogManager.getLogger(YukonHttpSessionListener.class);

    public void sessionCreated(HttpSessionEvent event) {
        log.debug("Session created.");
        logSessionDetails(event.getSession());
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        log.debug("Session destroyed.");
        logSessionDetails(event.getSession());
    }

    public void attributeRemoved(HttpSessionBindingEvent event) {
        log.debug("Session attirbute removed.");
        log.debug("Attribute name: " + event.getName() + " Attribute Value: " + event.getValue());
    }

    public void attributeAdded(HttpSessionBindingEvent event) {
        log.debug("Session attirbute added.");
        log.debug("Attribute name: " + event.getName() + " Attribute Value: " + event.getValue());
    }

    public void attributeReplaced(HttpSessionBindingEvent event) {
        log.debug("Session attirbute replaced.");
        log.debug("Attribute name: " + event.getName() + " Attribute Value: " + event.getValue());
    }

    private void logSessionDetails(HttpSession session) {
        Date sessionCreationTime = new Date(session.getCreationTime());
        Date sessionLastAccessedTime = new Date(session.getLastAccessedTime());
        log.debug("Session id: " + session.getId());
        log.debug("Create time: " + sessionCreationTime);
        log.debug("Last access: " + sessionLastAccessedTime);
        log.debug("Max inactive interval: " + session.getMaxInactiveInterval());
        if (session.getAttribute("YUKON_USER") != null) {
            LiteYukonUser user = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
            log.debug("User Name: " + user.getUsername());
            log.debug("User Id: " + user.getUserID());
        }
    }

}

package com.cannontech.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.cannontech.clientutils.CTILogger;


/**
 * The purpose of this listener is to make a simple log call
 * that will cause our logging system to initialize itself. Third-party software
 * that uses commom.log4j loggin will then be directed to the correct locations.
 */
public class KickStartLoggingListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        CTILogger.info("Context Event: " + event);
    }

    public void contextDestroyed(ServletContextEvent event) {
    }

}

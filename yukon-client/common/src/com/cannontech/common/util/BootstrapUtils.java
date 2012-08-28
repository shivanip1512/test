package com.cannontech.common.util;

import java.io.File;
import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;

/**
 * Must not be dependent on any other part of yukon
 * This includes masterconfig, logging
 * Do not use CTIUtilities here
 */
public class BootstrapUtils {

    private final static String KEYS_DIRECTORY = "/Server/Config/Keys/";
    
    /**
     * Returns the base/home directory where yukon is installed.
     * From here we can assume the canonical yukon directory
     * structure.
     * @return
     */
    public final static String getYukonBase(boolean debug) {

        // try a JNDI context
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            String jndiYukonBase = (String) env.lookup("yukon.base");
            File file = new File(jndiYukonBase);
            if (file.exists()) {
                return file.getCanonicalPath();
            }
        } catch (Exception e) {
            if (debug) {
                System.out.println("Debug: Unable to use JNDI context for yukon.base: " + e.getMessage());
            }
        }

        final String fs = System.getProperty("file.separator");

        // First try to use yukon.base
        String yukonBase = System.getProperty("yukon.base");
        if (yukonBase != null) {
            return yukonBase;
        }

        // Next try to use the environment variable that is now available (thanks Sun)
        String envYukonBase = System.getenv("YUKON_BASE");
        if (envYukonBase != null) {
            envYukonBase = StringUtils.remove(envYukonBase, "\"");
            return envYukonBase;
        }

        // That failed, so...
        // Are we running inside tomcat? assume we live in yukon/server/web then
        // and calculate yukon base accordingly
        final String catBase = System.getProperty("catalina.base");
        if (catBase != null) {
            try {
                File yb = new File(catBase + fs + ".." + fs + "..");
                return yb.getCanonicalPath();
            } catch (IOException ioe) {}

            // maybe we are in a development environment where we might not be running in a
            // good yukon directory structure?
            return fs + "yukon";
        }

        // So, we must be a client running from the command line?
        // assume we are running from the yukon/client/bin directory
        try {
            File yb = new File(".." + fs + "..");
            return yb.getCanonicalPath();
        } catch (IOException ioe) {}

        // Last resort, return the current directory
        try {
            return new File(".").getCanonicalPath();
        } catch (IOException ioe) {}

        // total failure, doh!
        return fs + "yukon";
    }
    
    public final static String getKeysFolder() {
        return getYukonBase(false) + KEYS_DIRECTORY;
    }
}
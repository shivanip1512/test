package com.cannontech.common.util;

import java.io.File;
import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;

/**
 * The purpose of this utility class is to limit dependencies on other classes.  This class should never depend
 * on anything more complicated than a simple utility class.  It should never have any dependencies on anything
 * Spring.
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

    /**
     * <p>
     * Get the directory for log files. Despite this being called the "Server" log directory, client
     * logs are stored here also. This is used both to know where to put log files (e.g. in
     * YukonFileAppender) and to know where to find log files (e.g. LogController.java).
     * </p>
     * 
     * <p>
     * It will:
     * <ol>
     * <li>Check the system property yukon.logDir and use that if it's set. I don't see this ever
     * being used except perhaps by a developer. It's mostly there to be consistent with
     * {@link #getYukonBase(boolean)}.</li>
     * <li>
     * Check the YUKON_LOG_DIR environment variable. This allows customers to put the log directory
     * outside the Yukon install directory. It should almost never (if not actually never) be
     * necessary. It is NOT necessary to set this just because you install Yukon in a place other
     * than C:\Yukon.
     * </li>
     * <li>
     * Finally, the normal "Logs" under &lt;yukon base&gt; is used if neither of the previous
     * values has been set.  This is the normal case.
     * </li>
     * </ol>
     * </p>
     */
    public final static String getServerLogDir() {
        String serverLogDir = System.getProperty("yukon.logDir");
        if (!StringUtils.isBlank(serverLogDir)) {
            return serverLogDir;
        }

        serverLogDir = System.getenv("YUKON_LOG_DIR");
        if (!StringUtils.isBlank(serverLogDir)) {
            return serverLogDir;
        }

        // This is the normal case for 99 if not 100% of all customers.
        return CtiUtilities.getYukonBase() + "/Server/Log/";
    }

    public final static String getKeysFolder() {
        return getYukonBase(false) + KEYS_DIRECTORY;
    }
}

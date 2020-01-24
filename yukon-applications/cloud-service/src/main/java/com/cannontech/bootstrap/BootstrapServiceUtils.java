package com.cannontech.bootstrap;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class BootstrapServiceUtils {
    private static String serviceStartTime = StringUtils.EMPTY;
    private static String logFolder = File.separator + "Log" + File.separator;

    public final static String getLogPath() {
        String path = getYukonBase();
        if (path != null)
            return path + File.separator + "Server" + logFolder;
        else {
            path = System.getProperty("user.dir");
            return path.substring(0, path.lastIndexOf(File.separator)) + logFolder;
        }
    }

    /**
     * Return default header text for log files.
     */
    public static String getSystemInfoString() {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        out.println("USER_TIMEZONE: " + SystemUtils.USER_TIMEZONE);
        out.println("USER_COUNTRY: " + SystemUtils.USER_COUNTRY);
        out.println("OS_ARCH: " + SystemUtils.OS_ARCH);
        out.println("OS_NAME: " + SystemUtils.OS_NAME);
        out.println("OS_VERSION: " + SystemUtils.OS_VERSION);
        out.println("JAVA_HOME: " + SystemUtils.JAVA_HOME);
        out.println("JAVA_VERSION: " + SystemUtils.JAVA_VERSION);
        out.println("JAVA_CLASS_PATH: " + SystemUtils.JAVA_CLASS_PATH);
        out.println("JAVA_LIBRARY_PATH: " + SystemUtils.JAVA_LIBRARY_PATH);
        out.println("JAVA_EXT_DIRS: " + SystemUtils.JAVA_EXT_DIRS);
        out.println("JAVA_ENDORSED_DIRS: " + SystemUtils.JAVA_ENDORSED_DIRS);
        out.println("JAVA_VM_NAME: " + SystemUtils.JAVA_VM_NAME);
        return sw.toString();
    }

    /**
     * Returns service start date and time.
     */
    public static String getServiceStartTime() {
        if (serviceStartTime.isEmpty()) {
            Date currentDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            serviceStartTime = formatter.format(currentDate);
        }
        return serviceStartTime;
    }

    /**
     * Returns the base/home directory where yukon is installed.
     * From here we can assume the canonical yukon directory
     * structure.
     * 
     * @return
     */
    public final static String getYukonBase() {

        // try a JNDI context
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            String jndiYukonBase = (String) env.lookup("yukon.base");
            File file = new File(jndiYukonBase);
            if (file.exists()) {
                return file.getCanonicalPath();
            }
        } catch (Exception e) {
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
            } catch (IOException ioe) {
            }

            // maybe we are in a development environment where we might not be running in a
            // good yukon directory structure?
            return fs + "yukon";
        }

        // So, we must be a client running from the command line?
        // assume we are running from the yukon/client/bin directory
        try {
            File yb = new File(".." + fs + "..");
            return yb.getCanonicalPath();
        } catch (IOException ioe) {
        }

        // Last resort, return the current directory
        try {
            return new File(".").getCanonicalPath();
        } catch (IOException ioe) {
        }

        // total failure, doh!
        return fs + "yukon";
    }
}

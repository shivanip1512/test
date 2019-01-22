package com.cannontech.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.catalina.loader.ParallelWebappClassLoader;
import org.apache.commons.lang3.StringUtils;

import com.cannontech.spring.YukonSpringHook;

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

    /**
     * Do not call this method until the keys folder is actually used. In other words, do not call this method
     * and store as a class member. The reason is this cannot be called on webstarted clients since this
     * directory is never expected to exist.
     * 
     * @return directory used to store encryption keys. Directory will be created if it doesn't exist.
     */
    public final static Path getKeysFolder() {
        Path keyFolder = Paths.get(getYukonBase(false) + KEYS_DIRECTORY);
        if (!Files.exists(keyFolder, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createDirectory(keyFolder);
            } catch (IOException e) {
                throw new RuntimeException("Cannot create keys directory. This is required for Yukon.", e);
            }
        }
        return keyFolder;
    }

    /**
     * Set the name of the application. In many cases, this does not need to be called. See
     * {@link #getApplicationName()}.  If this is called, it must be called before Spring starts up
     * (i.e. before any calls to {@link YukonSpringHook}).
     */
    public final static void setApplicationName(ApplicationId defaultName) {
        String existingName = CtiUtilities.getCtiAppName();
        if (existingName == null) {
            CtiUtilities.setCtiAppName(defaultName);
        }
    }

    /**
     * This method will return the name of this application. If no name is found, a dummy string is
     * returned.
     * <p>
     * 
     * The "application name" is a short string defining naming the application. It's used by the
     * logger for part of the filename and by JMX to as a suffix to domain ("com.cannontech.yukon").
     * <p>
     * 
     * Different applications set this in different ways:
     * <ul>
     * <li>The main web application essentially hard-codes it here as "Webserver".</li>
     * <li>Most Java applications (e.g. the swing clients or Yukon Service Manager call
     * {@link #setApplicationName(ApplicationId)} to set it on start-up.</li>
     * <li>In a web application like the EIM server, it should be set in web.xml.</li>
     * </ul>
     */
    public final static String getApplicationName() {
        String appName = CtiUtilities.getCtiAppName();
        if (appName != null) {
            return appName;
        }
        //TODO : Remove sysouts
        ApplicationId defaultAppName = ApplicationId.UNKNOWN;
        if (CtiUtilities.isRunningAsWebApplication()) {
            System.out.println("Running as Webapplication");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader instanceof ParallelWebappClassLoader) {
                System.out.println("type : ParallelWebappClassLoader");
                String contextName = ((ParallelWebappClassLoader) classLoader).getContextName();
                System.out.println("context name : " + contextName);
                if ("api".equals(contextName)) {
                    defaultAppName = ApplicationId.WEB_SERVICES;
                } else if ("yukon".equals(contextName)) {
                    defaultAppName = ApplicationId.WEBSERVER;
                }
            }
        }
        CtiUtilities.setCtiAppName(defaultAppName);
        return defaultAppName.getApplicationName();
    }

    public final static ApplicationId getApplicationId() throws ApplicationIdUnknownException {
        String appName = getApplicationName();

        ApplicationId id = ApplicationId.getByName(appName);

        if (id == ApplicationId.UNKNOWN) {
            throw new ApplicationIdUnknownException(appName);
        }

        return id;
    }

    public static boolean isWebStartClient() {
        return StringUtils.isNotBlank(System.getProperty("jnlp.yukon.host"));
    }
}

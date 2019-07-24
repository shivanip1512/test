package com.cannontech.rest.api.utilities;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {

    // Initialize log4j logs

    private static Logger log = LogManager.getLogger(Log.class);

    static {
        PropertyConfigurator.configure(BasicConfiguration.getInstance().getlog4jPath());
    }

    // This is to print log for the beginning of the test case, as we usually run so many test cases as a test
    // suite

    public static void startTestCase(String sTestCaseName) {

        log.info("****************************************************************************************");

        log.info("****************************************************************************************");

        log.info("$$$$$$$$$$$$$$$$$$$$$         " + sTestCaseName + " Started     $$$$$$$$$$$$$$$$$$$$$$$$$");

        log.info("****************************************************************************************");

        log.info("****************************************************************************************");

    }

    // This is to print log for the ending of the test case

    public static void endTestCase(String sTestCaseName) {

        log.info("****************************************************************************************");

        log.info("****************************************************************************************");

        log.info("$$$$$$$$$$$$$$$$$$$$$      " + sTestCaseName + " E--N--D      $$$$$$$$$$$$$$$$$$$$$$$$$");

        log.info("****************************************************************************************");

        log.info("****************************************************************************************");
    }

    // Need to create these methods, so that they can be called

    public static void info(String message) {
        log.info(message);
    }

    public static void warn(String message) {
        log.warn(message);
    }

    public static void error(String message) {
        log.error(message);
    }

    public static void fatal(String message) {
        log.fatal(message);
    }

    public static void debug(String message) {
        log.debug(message);
    }

    public static void info(Object message) {
        log.info(message);
    }
}

package com.cannontech.tools;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.SystemUtils;

public class TimeZoneTest {
    static Date now = new Date();
    static PrintStream out;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            out = System.out;
            out.println("Running Java Time Zone tester");
            out.println();

            out.println("Java home: " + SystemUtils.getJavaHome());
            out.println("Java version: " + SystemUtils.JAVA_VERSION);

            out.println("Current time: " + now);
            out.println();

            //showAll();

            out.println("Default Time Zone");
            TimeZone defaultTz = TimeZone.getDefault();

            doMarchTest(defaultTz);

            if (args.length > 0) {
                String argTzStr = args[0];
                TimeZone argTz = TimeZone.getTimeZone(argTzStr);
                out.println(argTzStr + " Time Zone");
                doMarchTest(argTz);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void doMarchTest(TimeZone defaultTz) throws ParseException {
        boolean inDst;
        out.println("  toString: " + defaultTz);
        out.println("  getDisplayName: " + defaultTz.getDisplayName());
        out.println("  getID: " + defaultTz.getID());
        out.println("  Currently in DST: " + defaultTz.inDaylightTime(now));
        Date march2006 = DateFormat.getDateInstance().parse("March 13, 2006");
        inDst = defaultTz.inDaylightTime(march2006);
        out.println("  " + march2006 + " is in DST: " + inDst);
        Date march2007 = DateFormat.getDateInstance().parse("March 12, 2007");
        inDst = defaultTz.inDaylightTime(march2007);
        out.println("  " + march2007 + " is in DST: " + inDst);
        out.println();
    }

    private static void showAll() {
        out.println("Available Time Zones:");
        String[] availableIDs = TimeZone.getAvailableIDs();
        for (String tz : availableIDs) {
            out.println("   " + tz);
        }

        out.println();
    }

}

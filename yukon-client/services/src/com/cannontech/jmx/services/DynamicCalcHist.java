package com.cannontech.jmx.services;

import java.lang.reflect.Method;
import java.util.GregorianCalendar;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import com.cannontech.calchist.CalcHistorical;
import com.cannontech.clientutils.commonutils.ModifiedDate;

/**
 * Wrapper for the CalcHistorical to allow plugability into a JMX server
 */
@SuppressWarnings("ucd")
public class DynamicCalcHist {
    private CalcHistorical calcHist;

    /**
     * Main constructor
     */
    public DynamicCalcHist() {
        calcHist = new CalcHistorical();
    }

    /**
     * Starts the CalcHist Service
     */
    public void start() {
        calcHist.start();
    }

    /**
     * Stops the CalcHist Service
     */
    public void stop() {
        calcHist.stop();
    }

    /**
     * Gets the running state
     */
    public boolean isRunning() {
        return calcHist.isRunning();
    }

    /**
     * Next time to run
     */
    public String getNextCalcTime() {
        try {
            Class<? extends CalcHistorical> cls = calcHist.getClass();
            Method method = cls.getDeclaredMethod("getNextCalcTime", new Class[0]);
            method.setAccessible(true);
            GregorianCalendar result = (GregorianCalendar) method.invoke(calcHist, new Object[0]);

            return new ModifiedDate(result.getTime().getTime()).toString();
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return "Unknown";
        }
    }

    //
    // JMX Specific Part
    //
    protected MBeanAttributeInfo[] createMBeanAttributeInfo() {
        return new MBeanAttributeInfo[] {
            new MBeanAttributeInfo("Running", "boolean", "The running status of the Service", true, false, true),
            new MBeanAttributeInfo("NextCalcTime", GregorianCalendar.class.getName(),
                "The next time the Service will calculate the values", true, false, false) };
    }

    protected MBeanOperationInfo[] createMBeanOperationInfo() {
        return new MBeanOperationInfo[] {
            new MBeanOperationInfo("start", "Start the CalcHistorical Service", new MBeanParameterInfo[0], "void",
                MBeanOperationInfo.ACTION),

            new MBeanOperationInfo("stop", "Stop the CalcHistorical Service", new MBeanParameterInfo[0], "void",
                MBeanOperationInfo.ACTION) };
    }

}

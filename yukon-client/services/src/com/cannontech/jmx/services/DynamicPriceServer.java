package com.cannontech.jmx.services;

import java.lang.reflect.Method;
import java.util.GregorianCalendar;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.custom.pss2ws.PriceServer;

/**
 * Wrapper for the PriceServer to allow plugability into a JMX server
 */
@SuppressWarnings("ucd")
public class DynamicPriceServer {
    private PriceServer priceServer;

    public DynamicPriceServer() {
        priceServer = new PriceServer();
    }

    /**
     * Starts the PriceServer Service
     */
    public void start() {
        priceServer.start();
    }

    /**
     * Stops the PriceServer Service
     */
    public void stop() {
        priceServer.stop();
    }

    /**
     * Gets the running state
     */
    public boolean isRunning() {
        return priceServer.isRunning();
    }

    /**
     * Next time to run
     */
    public String getNextRunTime() {
        try {
            Class<? extends PriceServer> cls = priceServer.getClass();
            Method method = cls.getDeclaredMethod("getNextRunTime", new Class[0]);
            method.setAccessible(true);
            GregorianCalendar result = (GregorianCalendar) method.invoke(priceServer, new Object[0]);

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
            new MBeanAttributeInfo("NextRunTime", GregorianCalendar.class.getName(),
                "The next time the Service will load the price point", true, false, false) };
    }

    protected MBeanOperationInfo[] createMBeanOperationInfo() {
        return new MBeanOperationInfo[] {
            new MBeanOperationInfo("start", "Start the Price Server", new MBeanParameterInfo[0], "void",
                MBeanOperationInfo.ACTION),

            new MBeanOperationInfo("stop", "Stop the Price Service", new MBeanParameterInfo[0], "void",
                MBeanOperationInfo.ACTION) };
    }

}

package com.cannontech.jmx.services;

import java.lang.reflect.Method;
import java.util.GregorianCalendar;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.integration.crs.YukonCRSIntegrator;

/**
 * Wrapper for the CRS Integration Tasks (for Xcel PMSI Replacement Project) to allow plugability into a JMX
 * server
 */
@SuppressWarnings("ucd")
public class DynamicCRSIntegrator {
    private YukonCRSIntegrator integrator;

    public DynamicCRSIntegrator() {
        integrator = new YukonCRSIntegrator();
    }

    /**
     * Starts the CRSIntegrator service
     */
    public void start() {
        integrator.start();
    }

    /**
     * Stops the service
     */
    public void stop() {
        integrator.stop();
    }

    /**
     * Gets the running state
     */
    public boolean isRunning() {
        return integrator.isRunning();
    }

    /**
     * Next time to run
     */
    public String getNextImportTime() {
        try {
            Class<? extends YukonCRSIntegrator> cls = integrator.getClass();
            Method method = cls.getDeclaredMethod("getNextImportTime", new Class[0]);
            method.setAccessible(true);
            GregorianCalendar result = (GregorianCalendar) method.invoke(integrator, new Object[0]);

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
            new MBeanAttributeInfo("NextImportTime", GregorianCalendar.class.getName(),
                "The next time the Service will import values", true, false, false) };
    }

    protected MBeanOperationInfo[] createMBeanOperationInfo() {
        return new MBeanOperationInfo[] {
            new MBeanOperationInfo("start", "Start the CRS Integration Service", new MBeanParameterInfo[0], "void",
                MBeanOperationInfo.ACTION),

            new MBeanOperationInfo("stop", "Stop the CRS Integration Service", new MBeanParameterInfo[0], "void",
                MBeanOperationInfo.ACTION) };
    }

}

package com.cannontech.customer.wpsc;

import java.io.FileOutputStream;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LogWriter;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.util.Message;
import com.cannontech.yukon.conns.ConnPool;

/**
 * This is the entry point for Wisconsin Public Service Co's custom app It has two main functions: 
 *  1) Parse a file written by the wpcs mainframe containing switch configuration information 
 *     and send the appropriate config commands to porter. 
 *  2) Write out to a file in a format specific to the wpcs mainframe when certain events are received from Dispatch.
 * @see com.cannontech.customer.wpsc.CFDATA
 * @see com.cannontech.customer.wpsc.LDCNTSUM
 */
public class WPSCMain implements Runnable {
    public static boolean isService = true;
    static boolean DEBUG = true;

    private DispatchClientConnection dispatchConn = null;

    private CFDATA customFileDataInstance = null;
    private LDCNTSUM loadContSumInstance = null;

    private static Thread customFileDataThread = null;
    private static Thread loadContSumThread = null;

    public static LogWriter logger = null;
    private GregorianCalendar now = null;
    private int currentDate;

    private LogWriterThread logWriter = null;
    private String dataDir = "C:";
    private String logFilename = "/wpscustom";

    private class LogWriterThread extends Thread {
        @Override
        public void run() {
            while (true) {
                now = new GregorianCalendar();

                // Check for new day of month.
                if (now.get(Calendar.DATE) != WPSCMain.this.currentDate) {
                    WPSCMain.this.updateLogWriter();
                }

                try {
                    // interval rate is in seconds (* 1000 for millis)
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    return;
                }
            }
        }
    }

    public WPSCMain() {
    	super();
        dispatchConn = (DispatchClientConnection) ConnPool.getInstance().getDefDispatchConn();
    }

    /**
     * Create log director if doesn't already exist.
     */
    private void initDirectory() {
        dataDir = "../log";
        java.io.File file = new java.io.File(dataDir);
        file.mkdirs();
    }

    /**
     * Initialize timestamps. Creates a logger 
     */
    private void initialize() {
        now = new java.util.GregorianCalendar();
        currentDate = now.get(java.util.Calendar.DATE);

        try {
            synchronized (this) {
                try {
                    String filename = dataDir + logFilename + currentDate + ".log";
                    FileOutputStream out = new FileOutputStream(filename);
                    PrintWriter writer = new PrintWriter(out, true);
                    logger = new LogWriter("WPSCustom", LogWriter.DEBUG, writer);
                    logger.log("WPS Custom Starting up....", LogWriter.INFO);
                } catch (java.io.FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes msg to logger
     * @param prioritylevel int (com.cannontech.common.util.LogWriter.DEBUG, INFO, ERROR, NONE)
     */
    protected static void logMessage(String msg, int prioritylevel) {
        logger.log(msg, prioritylevel);
    }

    public static void main(String[] args) {
        System.setProperty("cti.app.name", "Custom");

        String cfDataDir;
        String cfDataFileExt;
        long cfDataCheckFreq;
        String vComRoute = null;
        String xComRoute = null;
        int xComProgAddr = 254;	//value 1=254 that is NOT a valid program address for WPS
        
        String LDCNTSUMOutputFile;

        // Arguments were specified, override defaults
        if (args.length == 8) {
            // Old Format, but Dispatch and Porter connection strings are no longer used/needed.
            // Usage:  WPSCMain DispatchHost DispatchPort PorterHost PorterPort CFDATADir CFDATAFileExt CFDataCheckFreq LDCNTSUMOutputFile

            // no need to load index 0 - 3 anymore 
            cfDataDir = args[4];
            cfDataFileExt = args[5];
            cfDataCheckFreq = Integer.parseInt(args[6]);

            LDCNTSUMOutputFile = args[7];
        } else if (args.length == 7) {
            cfDataDir = args[0];
            cfDataFileExt = args[1];
            cfDataCheckFreq = Integer.parseInt(args[2]);
            vComRoute = args[3];
            xComRoute = args[4];
            xComProgAddr = Integer.parseInt(args[5]);
            
            LDCNTSUMOutputFile = args[6];
        } else {
            CTILogger.info("Usage:  WPSCMain CFDATADir CFDATAFileExt CFDataCheckFreq LDCNTSUMOutputFile\n");
            CTILogger.info("Ex.     WPSCMain c:/cfdatadir .rcv 1000 '@Tap Term Comms' 'ALL FM ROUTES' 254 ldcntsum.snd");
            return;
        }

        WPSCMain instance = new WPSCMain();

        instance.customFileDataInstance = new CFDATA(cfDataDir, cfDataFileExt, vComRoute, xComRoute, xComProgAddr);
        instance.customFileDataInstance.setCheckFrequency(cfDataCheckFreq);

        instance.loadContSumInstance = new LDCNTSUM(instance.dispatchConn, LDCNTSUMOutputFile);
        
        instance.initDirectory();
        instance.initialize();

        // no reason to spawn a new thread
        instance.run();
    }

    @Override
    public void run() {
        try {
            // Build up a registration message to Dispatch
            Registration reg = new Registration();
            reg.setAppName(CtiUtilities.getAppRegistration());
            reg.setAppIsUnique(0);
            reg.setAppKnownPort(0);
            reg.setAppExpirationDelay(1000000);

            PointRegistration pointReg = new PointRegistration();
            pointReg.setRegFlags(PointRegistration.REG_EVENTS | PointRegistration.REG_ALARMS);

            Multi<Message> multiReg = new Multi<Message>();
            multiReg.getVector().addElement(reg);
            multiReg.getVector().addElement(pointReg);

            dispatchConn.setRegistrationMsg(multiReg);
            dispatchConn.connectWithoutWait();

            // Start LogWriter Thread (watches for date change for log file generation).
            logWriter = new LogWriterThread();
            logWriter.start();

            customFileDataThread = new Thread(customFileDataInstance);
            customFileDataThread.setDaemon(true);
            customFileDataThread.start();

            loadContSumThread = new Thread(loadContSumInstance);
            loadContSumThread.setDaemon(true);
            loadContSumThread.start();

            // Joins the Threads, the WPSCMain application will not continue from this point until all threads are killed.
            customFileDataThread.join();
            loadContSumThread.join();

        } catch (InterruptedException ie) {
            logMessage("Interrupted Exception in WPSCMain()",
                       com.cannontech.common.util.LogWriter.ERROR);
            ie.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    /**
     * Create new log file (for "next day").
     */
    private void updateLogWriter() {
        now = new GregorianCalendar();
        int oldFileDate = currentDate;
        currentDate = now.get(Calendar.DATE);

        try {
            synchronized (this) {
                try {
                    logMessage("...{continues logs in wpscustom" + currentDate + ".log file}", LogWriter.INFO);
                    logger.getPrintWriter().close();

                    String filename = dataDir + logFilename + currentDate + ".log";
                    FileOutputStream out = new FileOutputStream(filename);
                    PrintWriter writer = new PrintWriter(out, true);
                    logger = new LogWriter("WPCSCustom", LogWriter.DEBUG, writer);

                    logMessage("...{continued log file from wpcustom" + oldFileDate + ".log file}", LogWriter.INFO);
                } catch (java.io.FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

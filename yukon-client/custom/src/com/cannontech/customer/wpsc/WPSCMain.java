package com.cannontech.customer.wpsc;

/**
 * This is the entry point for Wisconsin Public Service Co's custom app
 * It has two main functions:
 * 1)	Parse a file written by the wpcs mainframe containing switch configuration
 *  	information and send the appropriate config commands to porter.
 *
 * 2)	Write out to a file in a format specific to the wpcs mainframe when
 *		certain events are received from Van Gogh(dispatch?).
 *		
 *		
 *		 
 * Creation date: (5/17/00 12:50:14 PM)
 * @author: Aaron Lauinger
 * @see com.cannontech.customer.wpsc.CFDATA
 * @see com.cannontech.customer.wpsc.LDCNTSUM
 */
public class WPSCMain implements Runnable
{
	public static boolean isService = true;
	private final String VERSION = "2.1.12";
	static boolean DEBUG = true;
	
	private com.cannontech.message.dispatch.ClientConnection dispatchConn = null;
	private com.cannontech.message.porter.ClientConnection  porterConn = null;

	private CFDATA CFDATAInstance = null;
	private LDCNTSUM LDCNTSUMInstance = null;

	private static Thread CFDATAThread = null;
	private static Thread LDCNTSUMThread = null;
	//private java.util.Date now = new java.util.Date();
	
	public static com.cannontech.common.util.LogWriter logger = null;
	private java.util.GregorianCalendar now = null;
	private int currentDate;

	private LogWriterThread logWriter = null;
	private String dataDir= "C:";
	private String logFilename = "/wpscustom";
		
	private class LogWriterThread extends Thread
	{
		//public boolean ignoreAutoUpdate = false;
		public void run()
		{
			while (true)
			{
				now = new java.util.GregorianCalendar();
				
				// Check for new day of month.
				if (now.get(java.util.Calendar.DATE) != WPSCMain.this.currentDate)
				{
					WPSCMain.this.updateLogWriter();
				}
				
				try
				{
					//interval rate is in seconds (* 1000 for millis)
					Thread.sleep(2000);
				}
				catch (InterruptedException ie)
				{
					return;
				}
			}
		}
	}

	
/**
 * WPSCMain constructor comment.
 */
public WPSCMain(String dispatchHost, int dispatchPort, String porterHost, int porterPort, String CFDATADir, String CFDATAFileExt, long CFDATACheckFreq, String outputFile) {
	super();
	
	dispatchConn = new com.cannontech.message.dispatch.ClientConnection( dispatchHost, dispatchPort );	
	porterConn = new com.cannontech.message.porter.ClientConnection( porterHost, porterPort );

	dispatchConn.setAutoReconnect(true);
	porterConn.setAutoReconnect(true);
	
	CFDATAInstance = new CFDATA( porterConn, CFDATADir, CFDATAFileExt );
	CFDATAInstance.setCheckFrequency(CFDATACheckFreq);

	LDCNTSUMInstance = new LDCNTSUM( dispatchConn, outputFile );
	
}
/**
 * Insert the method's description here.
 * Creation date: (6/6/2002 9:45:33 AM)
 */
public void exit()
{
	// Send a shutdown message to Dispatch
	try
	{
		com.cannontech.message.dispatch.message.Command comm = new com.cannontech.message.dispatch.message.Command();
		comm.setPriority(15);

		comm.setOperation( com.cannontech.message.dispatch.message.Command.CLIENT_APP_SHUTDOWN );
		dispatchConn.write( comm );
		dispatchConn.disconnect();
	}
	catch( java.io.IOException ioe)
	{
		ioe.printStackTrace();
	}
	finally
	{
		System.exit(0);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2001 1:00:25 PM)
 */
public void initDirectory()
{
	dataDir = "../log";
	java.io.File file = new java.io.File( dataDir );
	file.mkdirs();
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2001 11:05:36 AM)
 */
public void initialize()
{
	now = new java.util.GregorianCalendar();
	currentDate = now.get(java.util.Calendar.DATE);

	try
	{		
		synchronized (this)
		{		
			try
			{
				String filename = dataDir + logFilename + currentDate + ".log";
				java.io.FileOutputStream out = new java.io.FileOutputStream(filename);
				java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
				logger = new com.cannontech.common.util.LogWriter("WPCSCustom", com.cannontech.common.util.LogWriter.DEBUG, writer);
				logger.log("VERSION: " + VERSION + "   Starting up....", com.cannontech.common.util.LogWriter.INFO );
			}
			catch( java.io.FileNotFoundException e )
			{
				e.printStackTrace();
			}		
		}
	
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2001 1:22:49 PM)
 * @param msg java.lang.String
 * @param prioritylevel int (com.cannontech.common.util.LogWriter.DEBUG, INFO, ERROR, NONE)
 */
public static void logMessage(String msg, int prioritylevel)
{

	logger.log(msg, prioritylevel);
}
/**
 * Entry point.
 * Creation date: (5/17/00 1:03:14 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	System.setProperty("cti.app.name", "Custom");
	String vanGoghHost;
	int vanGoghPort;

	String porterHost;
	int porterPort;

	String CFDATADir;
	String CFDATAFileExt;
	long CFDATACheckFreq;
	String LDCNTSUMOutputFile;
		
	//Arguments were specified, override defaults
	if( args.length  == 8 )	
	{
		vanGoghHost = args[0];
		vanGoghPort = Integer.parseInt(args[1]);
		porterHost = args[2];
		porterPort = Integer.parseInt(args[3]);

		CFDATADir = args[4];
		CFDATAFileExt = args[5];
		CFDATACheckFreq = Integer.parseInt(args[6]);

		LDCNTSUMOutputFile = args[7];
	}
	else
	{
		com.cannontech.clientutils.CTILogger.info("Usage:  WPSCMain DispatchHost DispatchPort PorterHost PorterPort");
		com.cannontech.clientutils.CTILogger.info("         CFDATADir CFDATAFileExt CFDataCheckFreq LDCNTSUMOutputFile\n");
		com.cannontech.clientutils.CTILogger.info("Ex.		WPSCMain 127.0.0.1 1510 127.0.0.1 1540 c:/cfdatadir");
		com.cannontech.clientutils.CTILogger.info("         .snd 1000 ldcntsum.snd");
		return;
	}

	WPSCMain instance = new WPSCMain( vanGoghHost, vanGoghPort, porterHost, porterPort, CFDATADir, CFDATAFileExt, CFDATACheckFreq, LDCNTSUMOutputFile );

	instance.initDirectory();
	instance.initialize();
	
	//no reason to spawn a new thread
	instance.run();
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 1:08:06 PM)
 */
public void run() 
{
	try
	{
		//Build up a registration message to Dispatch
		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("WPSC Module");
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 1000000 );

		com.cannontech.message.dispatch.message.PointRegistration pointReg = new com.cannontech.message.dispatch.message.PointRegistration();
		pointReg.setRegFlags( com.cannontech.message.dispatch.message.PointRegistration.REG_EVENTS | com.cannontech.message.dispatch.message.PointRegistration.REG_ALARMS );

		com.cannontech.message.dispatch.message.Multi multiReg = new com.cannontech.message.dispatch.message.Multi();
		multiReg.getVector().addElement(reg);
		multiReg.getVector().addElement(pointReg);
		
		dispatchConn.setRegistrationMsg(multiReg);
		dispatchConn.connectWithoutWait();

		porterConn.connectWithoutWait();

		// Start LogWriter Thread (watches for date change for log file generation).
		logWriter = new LogWriterThread();
		logWriter.start();

		CFDATAThread = new Thread(CFDATAInstance);
		CFDATAThread.setDaemon(true);
		CFDATAThread.start();

		LDCNTSUMThread = new Thread(LDCNTSUMInstance);
		LDCNTSUMThread.setDaemon(true);
		LDCNTSUMThread.start();

		// Joins the Threads, the WPSCMain application will not continue from this point 
		// until all threads are killed.	
		CFDATAThread.join();
		LDCNTSUMThread.join();

	}
	catch( java.io.IOException ioe )
	{
		ioe.printStackTrace();
	}
	catch( InterruptedException ie )
	{
		logMessage("Interrupted Exception in WPSCMain()", com.cannontech.common.util.LogWriter.ERROR);
		ie.printStackTrace();
	}
	finally
	{
		exit();
	}
}
public static void stopApplication()
{
	isService = false;
	logger.log(" Exitting the service application", com.cannontech.common.util.LogWriter.INFO);
	System.gc();
	CFDATAThread.interrupt();
	LDCNTSUMThread.interrupt();
}
/**
 * Insert the method's description here.
 * Creation date: (12/21/2001 11:05:36 AM)
 */
public void updateLogWriter()
{
	now = new java.util.GregorianCalendar();
	int oldFileDate = currentDate;
	currentDate = now.get(java.util.Calendar.DATE);

	try
	{		
		synchronized (this)
		{		
			try
			{
				logMessage("...{continues logs in wpscustom" + currentDate + ".log file}", com.cannontech.common.util.LogWriter.INFO);
				logger.getPrintWriter().close();

				String filename = dataDir + logFilename + currentDate + ".log";
				java.io.FileOutputStream out = new java.io.FileOutputStream(filename);
				java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
				logger = new com.cannontech.common.util.LogWriter("WPCSCustom", com.cannontech.common.util.LogWriter.DEBUG, writer);
				
				logMessage("...{continued log file from wpcustom" +oldFileDate+ ".log file}", com.cannontech.common.util.LogWriter.INFO );
			}
			catch( java.io.FileNotFoundException e )
			{
				e.printStackTrace();
			}		
		}
	
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
}
}

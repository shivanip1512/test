package com.cannontech.export;

/**
 * Insert the type's description here.
 * Creation date: (4/8/2002 11:11:48 AM)
 * @author: 
 */

public abstract class ExportFormatBase //extends com.ms.service.Service
{
	private static final String VERSION = "2.1.18";	
	public static String formatType = null;

	private String directory = null;
	private static com.cannontech.common.util.LogWriter logger = null;

	private java.util.Vector recordVector = null;
	private static boolean isService = true;


	public java.util.GregorianCalendar nextRunTime = null;
	public Integer runTimeHour = null;

	private static Thread sleepThread = new Thread();
	private com.cannontech.database.db.notification.NotificationGroup emailGroup = null;
/**
 * ExportFormatBase constructor comment.
 */
public ExportFormatBase() {
	super();
}
public abstract String appendBatchFileParms(String batchString);
/**
 * Insert the method's description here.
 * Creation date: (5/18/00 3:50:38 PM)
 */
/* Return an instance of the ExportFormatBase specified from the command line */

public final static ExportFormatBase createFileFormat(String formatString)
{
	ExportFormatBase returnFormat = null;
	
	if( formatString.startsWith(ExportFormatTypes.formatTypeShortName[ExportFormatTypes.DBPURGE_FORMAT]))
	{
		returnFormat = new DBPurge();
	}
	else if( formatString.startsWith(ExportFormatTypes.formatTypeShortName[ExportFormatTypes.CSVBILLING_FORMAT]))
	{
		returnFormat = new CSVBillingFormat();
	}
	else
	{
		com.cannontech.clientutils.CTILogger.info("No file format found - Unrecognized file format type");
	}
	return returnFormat;
}
/**
 * Insert the method's description here.
 * Creation date: (12/4/2000 2:27:20 PM)
 */
public void figureNextRunTime()
{
	if( nextRunTime == null )
	{
		nextRunTime = new java.util.GregorianCalendar();

		//First time this will run is at 1:00 AM.  Taking a chance that the program
		// is not getting started up around this time.
		nextRunTime.set(java.util.GregorianCalendar.HOUR_OF_DAY, getRunTimeHour().intValue());
		nextRunTime.set(java.util.GregorianCalendar.MINUTE, 0);
		nextRunTime.set(java.util.GregorianCalendar.SECOND, 0);
	}
	else
	{
		long lastRunTime = getNextRunTime().getTime().getTime();
		nextRunTime.setTime(new java.util.Date( lastRunTime + 86400000));
	}	
	logEvent("...Next RunTime to occur at: " + nextRunTime.getTime()+ " ...", com.cannontech.common.util.LogWriter.INFO );
	logEvent("", com.cannontech.common.util.LogWriter.NONE);
}
	
/**
 * Insert the method's description here.
 * Creation date: (3/18/2002 4:07:56 PM)
 * @return java.lang.String
 */
public String getDirectory()
{
	if( directory == null)
	{
		try
		{
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
			directory = bundle.getString("dbpurge_file_directory");
			logEvent("  (config.prop)  File Directory to store exported files in is " + directory, com.cannontech.common.util.LogWriter.INFO);
		}
		catch( Exception e)
		{
			directory = "c:/yukon/dbpurge/";
			logEvent("  File Directory was NOT found, DEFAULTED TO " + directory, com.cannontech.common.util.LogWriter.ERROR);
			logEvent("  Add 'dbpurge_file_directory' to config.properties.", com.cannontech.common.util.LogWriter.INFO);
		}
		java.io.File file = new java.io.File( directory );
		file.mkdirs();
	}
	return directory;
}
private com.cannontech.database.db.notification.NotificationGroup getEmailGroup()
{
	return emailGroup;
}
public abstract String getFileName();
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 12:01:31 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getNextRunTime()
{
	logEvent("  Get next runtime " + directory, com.cannontech.common.util.LogWriter.INFO);	
	if( nextRunTime == null)
	{
		figureNextRunTime();
	}
	return nextRunTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/29/00)
 */
public java.util.Vector getRecordVector()
{
	if( recordVector == null )
		recordVector = new java.util.Vector();
		
	return recordVector;
}
/**
 * Insert the method's description here.
 * Creation date: (3/18/2002 3:25:01 PM)
 * @return int
 */
public Integer getRunTimeHour() 
{
	if( runTimeHour == null )
	{
		try
		{
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
			runTimeHour = new Integer(bundle.getString("dbpurge_runtime_hour"));
			logEvent("  (config.prop)  Hour of Day (0-23) to run is " + runTimeHour, com.cannontech.common.util.LogWriter.INFO);
		}
		catch( Exception e)
		{
			runTimeHour = new Integer(1);
			logEvent("  Hour of Day (0-23) was NOT found, DEFAULTED TO " + runTimeHour, com.cannontech.common.util.LogWriter.ERROR);
			logEvent("  Add 'dbpurge_runtime_hour' to config.properties.", com.cannontech.common.util.LogWriter.INFO);
		}
	}
	return runTimeHour;
}
public String getVersion()
{
	return VERSION;
}
private boolean isService()
{
	return isService;
}
public void logEvent(String event, int severity)
{
	if (logger == null)
	{
		try
		{
			String dataDir = "../log/";
			java.io.File file = new java.io.File( dataDir );
			file.mkdirs();

			int lastDot = this.getClass().getName().lastIndexOf(".");
			String className = this.getClass().getName().substring(lastDot + 1);
			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			String filename = dataDir + className + ".log";
			java.io.FileOutputStream out = new java.io.FileOutputStream(filename, true);
			java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
			logger = new com.cannontech.common.util.LogWriter(className, com.cannontech.common.util.LogWriter.DEBUG, writer);

			logger.log("Starting up " + className, com.cannontech.common.util.LogWriter.INFO );
			logger.log("Version: " + com.cannontech.common.version.VersionTools.getYUKON_VERSION() +VERSION+ ".", com.cannontech.common.util.LogWriter.INFO );

		}		
		catch( java.io.IOException e )
		{
			e.printStackTrace();
		}
	}
	logger.log( event, severity);
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 12:50:01 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	ExportFormatBase formatBase = null;

	for ( int i = 0; i < args.length; i++)
	{
		String argLowerCase = (String)args[i].toLowerCase();
	
		if( argLowerCase.startsWith("format"))
		{
			int startIndex = argLowerCase.indexOf("=") + 1;
			String subString = argLowerCase.substring(startIndex);

			setFormatType(subString);
		}
		else if( argLowerCase.startsWith("one") || argLowerCase.startsWith("once"))
		{
			if( formatBase != null)
				formatBase.setIsService	(false);
		}
	}
	if( formatType == null)
	{
		com.cannontech.clientutils.CTILogger.info("** Missing FORMAT=<format type> from commandline.");
		com.cannontech.clientutils.CTILogger.info("** No File Format Specified, Exporting process will exit...");
		System.exit(0);
	}
	else
	{
		formatBase = createFileFormat(formatType);
		formatBase.parseCommandLineArgs(args);
		
	}

	try
	{
		formatBase.sleepThread.sleep(1000);
	}
	catch (InterruptedException ie)
	{
		ie.printStackTrace();
	}

	//System.setProperty("system.method", "stopApplication");
	
	do 
	{
		java.util.Date now = new java.util.Date();

		if (formatBase.getNextRunTime().getTime().compareTo(now) <= 0)
		{
			formatBase.retrieveExportData();
			formatBase.writeToFile();
			formatBase.figureNextRunTime();
//			com.cannontech.message.dispatch.message.EmailMsg emailMsg = new com.cannontech.message.dispatch.message.EmailMsg();
//			emailMsg.setSender("snebben@cannontech.com");
		}
		else
		{
			try
			{
				//formatBase.sleepThread.sleep(900000);	//15 minutes
				formatBase.sleepThread.sleep(1000);	
				System.gc();
			}
			catch (InterruptedException ie)
			{
				com.cannontech.clientutils.CTILogger.info("Interrupted Exception!!!");
				return;
			}

		}
	}while (formatBase.isService());

	com.cannontech.clientutils.CTILogger.info("..Finished " + formatBase.getClass().getName() + " File Export.");
	formatBase.logEvent("..Finished " + formatBase.getClass().getName()+ " File Export.", com.cannontech.common.util.LogWriter.INFO);

	logger.getPrintWriter().close();
	logger = null;

	System.exit(0);
}
public abstract void parseCommandLineArgs(String [] args);
abstract public void retrieveExportData();
/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 11:18:37 AM)
 */
public static  void runMainWithGui(ExportFormatBase formatBase) 
{
	if( formatType == null)
	{
		com.cannontech.clientutils.CTILogger.info("** Missing FORMAT=<format type> from commandline.");
		com.cannontech.clientutils.CTILogger.info("** No File Format Specified, Exporting process will exit...");
		System.exit(0);
	}

	try
	{
		Thread.sleep(1000);
	}
	catch (InterruptedException ie)
	{
		ie.printStackTrace();
	}

	formatBase.retrieveExportData();
	formatBase.writeToFile();
	formatBase.figureNextRunTime();

	System.gc();

	logger.getPrintWriter().close();
	logger = null;
	com.cannontech.clientutils.CTILogger.info("..Finished " + formatBase.getClass().getName() + " File Export.");
	formatBase.logEvent("..Finished " + formatBase.getClass().getName()+ " File Export.", com.cannontech.common.util.LogWriter.INFO);
}
public abstract void setAdvancedProperties(AdvancedOptionsPanel advOptsPanel);
/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 3:21:56 PM)
 * @param newDirectory java.lang.String
 */
public void setDirectory(String newDirectory)
{
	directory = newDirectory;
	java.io.File file = new java.io.File( directory );
	file.mkdirs();
	
	logEvent("  ##  Files exported to directory: " + directory, com.cannontech.common.util.LogWriter.INFO);
}
private void setEmailGroup(com.cannontech.database.db.notification.NotificationGroup newEmailGroup)
{
	emailGroup = newEmailGroup;
}
public static void setFormatType (String format)
{
	formatType = format;
}
public void setIsService(boolean value)
{
	isService = value;
}
/**
 * Insert the method's description here.
 * Creation date: (3/18/2002 4:23:48 PM)
 */
public void writeToFile()
{
	if( recordVector != null)
	{
		
		StringBuffer returnBuffer = new StringBuffer();
		for (int i = 0; i < recordVector.size(); i++)
		{
			String dataString = (String)recordVector.get(i);
			if( dataString != null)
				returnBuffer.append(dataString);
		}

		try
		{
			java.io.FileWriter outputFileWriter = new java.io.FileWriter( getFileName() );
			outputFileWriter.write( returnBuffer.toString() );
			outputFileWriter.flush();
			outputFileWriter.close();
			logEvent("...Exported * "+ recordVector.size() + " * Records to file " + getFileName(), com.cannontech.common.util.LogWriter.INFO);		
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	else
	{
		logEvent("...Exported * 0 * Records.  No file generated.", com.cannontech.common.util.LogWriter.INFO);
	}
}
}

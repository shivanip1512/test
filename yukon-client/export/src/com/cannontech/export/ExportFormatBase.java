package com.cannontech.export;

/**
 * Insert the type's description here.
 * Creation date: (4/8/2002 11:11:48 AM)
 * @author: 
 */

public abstract class ExportFormatBase //extends com.ms.service.Service
{
	private com.cannontech.export.ExportPropertiesBase exportProperties = null;

	private String directory = null;
	private static com.cannontech.common.util.LogWriter logger = null;

	private java.util.Vector recordVector = null;
	private static boolean isService = true;
	public java.util.GregorianCalendar nextRunTime = null;

	private static Thread sleepThread = new Thread();
	private com.cannontech.database.db.notification.NotificationGroup emailGroup = null;
	
	private long runTimeIntervalInMillis = 86400000;
/**
 * ExportFormatBase constructor comment.
 */
public ExportFormatBase() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/18/00 3:50:38 PM)
 */
/* Return an instance of the ExportFormatBase specified from the command line */
public final static ExportFormatBase createFileFormat(int formatID)
{
	ExportFormatBase returnFormat = null;
	
	if(formatID  == ExportFormatTypes.DBPURGE_FORMAT)
	{
		returnFormat = new DBPurge();
	}
	else if( formatID == ExportFormatTypes.CSVBILLING_FORMAT)
	{
		returnFormat = new CSVBillingFormat();
	}
	else if( formatID == ExportFormatTypes.IONEVENTLOG_FORMAT)
	{
		returnFormat = new IONEventLogFormat();
	}
	else
	{
		com.cannontech.clientutils.CTILogger.info("Unrecognized file formatID " + formatID);
	}
	returnFormat.getExportProperties().setFormatID(formatID);
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
		nextRunTime.set(java.util.GregorianCalendar.HOUR_OF_DAY, getExportProperties().getRunTimeHour().intValue());
		nextRunTime.set(java.util.GregorianCalendar.MINUTE, 0);
		nextRunTime.set(java.util.GregorianCalendar.SECOND, 0);
	}
	else
	{
		long lastRunTime = getNextRunTime().getTime().getTime();
		nextRunTime.setTime(new java.util.Date( lastRunTime + getRunTimeIntervalInMillis()));
	}	
	logEvent("Next RunTime will occur at: " + nextRunTime.getTime(), com.cannontech.common.util.LogWriter.INFO );
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
			directory = bundle.getString("export_file_directory");
			logEvent("Export File Directory (config.prop): " + directory, com.cannontech.common.util.LogWriter.INFO);
		}
		catch( Exception e)
		{
			directory = com.cannontech.common.util.CtiUtilities.getExportDirPath();
			logEvent("Config.properties export_file_directory key NOT found.", com.cannontech.common.util.LogWriter.ERROR);
			logEvent("Export File Directory  default value: " + directory, com.cannontech.common.util.LogWriter.INFO);
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

public String getDatFileName()
{
	return  ExportFormatTypes.getFormatDatFileName(getExportProperties().getFormatID());
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 12:01:31 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getNextRunTime()
{
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
public String getVersion()
{
	return com.cannontech.common.version.VersionTools.getYUKON_VERSION();
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

			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			String filename = dataDir + this.toString() + ".log";
			java.io.FileOutputStream out = new java.io.FileOutputStream(filename, true);
			java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
			logger = new com.cannontech.common.util.LogWriter(this.toString(), com.cannontech.common.util.LogWriter.DEBUG, writer);

			logger.log("Starting up " + this.toString(), com.cannontech.common.util.LogWriter.INFO );
			logger.log("Version: " + com.cannontech.common.version.VersionTools.getYUKON_VERSION() +getVersion()+ ".", com.cannontech.common.util.LogWriter.INFO );

		}		
		catch( java.io.IOException e )
		{
			e.printStackTrace();
		}
	}
	if( severity == com.cannontech.common.util.LogWriter.INFO)
		com.cannontech.clientutils.CTILogger.info(event);
	else if( severity == com.cannontech.common.util.LogWriter.ERROR)
		com.cannontech.clientutils.CTILogger.error(event);
		
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
	int format = -1;

	for ( int i = 0; i < args.length; i++)
	{
		String argLowerCase = (String)args[i].toLowerCase();

		if( argLowerCase.startsWith("format"))
		{
			int startIndex = argLowerCase.indexOf("=") + 1;
			String subString = argLowerCase.substring(startIndex);
			format = Integer.valueOf(subString).intValue();
		}
		else if( argLowerCase.startsWith("one") || argLowerCase.startsWith("once"))
		{
			if( formatBase != null)
				formatBase.setIsService	(false);
		}
	}
	if( format < 0)
	{
		formatBase.logEvent("** Missing FORMAT=<format type> from commandline.", com.cannontech.common.util.LogWriter.INFO);
		formatBase.logEvent("** No File Format Specified, Exporting process will exit...", com.cannontech.common.util.LogWriter.INFO);
		System.exit(0);
	}
	else
	{
		formatBase = createFileFormat(format);
		formatBase.parseDatFile();
		
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
				ie.printStackTrace();
				return;
			}

		}
	}while (formatBase.isService());

	formatBase.logEvent("..Finished " + formatBase.getClass().getName()+ " File Export.", com.cannontech.common.util.LogWriter.INFO);
	logger.getPrintWriter().close();
	logger = null;

	System.exit(0);
}

/**
 * Insert the method's description here.
 * Creation date: (5/13/2002 9:53:47 AM)
 */
public abstract void parseDatFile();
	
abstract public void retrieveExportData();
/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 11:18:37 AM)
 */
public static void runMainWithGui(ExportFormatBase formatBase) 
{
	if( formatBase.getExportProperties().getFormatID() < 0)
	{
		formatBase.logEvent("** Missing format=<format type> from commandline.", com.cannontech.common.util.LogWriter.INFO);
		formatBase.logEvent("** No File Format Specified, Exporting process will exit...", com.cannontech.common.util.LogWriter.INFO);
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

	formatBase.logEvent("..Finished " + formatBase.getClass().getName() + " File Export.", com.cannontech.common.util.LogWriter.INFO);
	logger.getPrintWriter().close();
	logger = null;
}
//public abstract void setAdvancedProperties(AdvancedOptionsPanel advOptsPanel);
public void setExportProperties(com.cannontech.export.ExportPropertiesBase exportProps)
{
	exportProperties = exportProps;
}
public com.cannontech.export.ExportPropertiesBase getExportProperties()
{
	if( exportProperties == null)
		exportProperties = new com.cannontech.export.ExportPropertiesBase();
	return exportProperties;
}
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
			java.io.FileWriter outputFileWriter = new java.io.FileWriter( getDirectory() + getFileName() );
			outputFileWriter.write( returnBuffer.toString() );
			outputFileWriter.flush();
			outputFileWriter.close();
		}
		catch (java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally
		{
			//DISCARD ALL RECORD DATA SO WE CAN START OVER AGAIN.
			logEvent("...Exported * "+ recordVector.size() + " * Records to file " + getDirectory() + getFileName(), com.cannontech.common.util.LogWriter.INFO);			
			recordVector.clear();
		}
	}
	else
	{
		logEvent("...Exported * 0 * Records.  No file generated.", com.cannontech.common.util.LogWriter.INFO);
	}
}

	public abstract String[][] buildKeysAndValues();

	public void writeDatFile()
	{
		try
		{
			java.io.File file = new java.io.File( com.cannontech.common.util.CtiUtilities.getConfigDirPath());
			file.mkdirs();

			java.io.FileWriter writer = new java.io.FileWriter( file.getCanonicalFile() + getDatFileName());
			String[][] keysAndValues = buildKeysAndValues();
			String keys[] = keysAndValues[0];
			String values[] = keysAndValues[1];

			String endline = "\r\n";

			for (int i = 0; i < keys.length; i++)
			{
				writer.write(keys[i] + "=" + values[i] + endline);
			}
			writer.close();
		}
		catch ( java.io.IOException e )
		{
			System.out.print(" IOException in writeDatFile");
			e.printStackTrace();
		}
	}		
	/**
	 * Returns the runTimeInterval.
	 * @return long
	 */
	public long getRunTimeIntervalInMillis()
	{
		return runTimeIntervalInMillis;
	}

	/**
	 * Sets the runTimeInterval.
	 * @param runTimeInterval The runTimeInterval to set
	 */
	public void setRunTimeIntervalInMillis(long millis)
	{
		this.runTimeIntervalInMillis = millis;
	}

	public String toString()
	{
		return ExportFormatTypes.getFormatTypeName(getExportProperties().getFormatID());
	}
}

package com.cannontech.export;

/**
 * Base class for export file format types.
 * Creation date: (4/8/2002 11:11:48 AM)
 * @author: 
 */
public abstract class ExportFormatBase
{
	private com.cannontech.export.ExportPropertiesBase exportProperties = null;	//Export format properties
	private static com.cannontech.common.util.LogWriter logger = null;	//Log writer for export formats.
	private static Thread sleepThread = new Thread();		//sleeper thread for main do/while loop.
	private static boolean isService = true;		//is Export format being ran as a service.

	private java.util.Vector recordVector = null;	//contains the (String) elements to write to the export file.
	private String directory = null;	//directory for export file deployment.

	public java.util.GregorianCalendar nextRunTime = null;	//Next run time the application will create a file.
	private long runTimeIntervalInMillis = 86400000;	//Interval between run times.

	private com.cannontech.database.db.notification.NotificationGroup emailGroup = null;	//not implemented 2/20/03
	
	/**
	 * ExportFormatBase constructor comment.
	 */
	public ExportFormatBase() {
		super();
	}
	
	/**
	 * Return an instance of the ExportFormatBase based on formatID
	 * set ExportProperties.formatId with param.formatID
	 * @param formatID	com.cannontech.export.ExportFormatType.FORMATTYPE
	 * @return ExportFormatBase
	 */
	public final static ExportFormatBase createFileFormat(int formatID)
	{
		ExportFormatBase returnFormat = null;
		
		if(formatID  == ExportFormatTypes.DBPURGE_FORMAT)
		{
			returnFormat = new DBPurgeFormat();
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
	 * Compute nextRunTime.
	 * Using nextRunTime = nextRunTime + runTimeIntervalInMillis.
	 * Method figureNextRunTime.
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
		logEvent("Next RunTime will occur: " + nextRunTime.getTime(), com.cannontech.common.util.LogWriter.INFO );
	}
		
	/**
	 * Return directory.
	 * Set directory according to ResourceBundle "export_file_directory" when null
	 *  or default to CtiUtilities.getExportDirPath() when bundle not found.
	 * Method getDirectory.
	 * @return String
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
	
	/**
	 * Return emailGroup.
	 * @return NotificationGroup
	 */
	private com.cannontech.database.db.notification.NotificationGroup getEmailGroup()
	{
		return emailGroup;
	}
	
	/**
	 * Return fileName to write exportFormat to.
	 * @return String
	 */
	public abstract String getFileName();
	
	/**
	 * Return ExportFormatTypes.formatDatFileName according to exportProperites.getFormatID().
	 * @return String
	 */
	public String getDatFileName()
	{
		return  ExportFormatTypes.getFormatDatFileName(getExportProperties().getFormatID());
	}
	
	/**
	 * Return nextRunTime
	 * Next run time the application will create a file.
	 * set nextRunTime by figureNextRunTime() when null.
	 * @return GregorianCalendar
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
	 * Return recordVector which contains the (String) elements to write to the export file.
	 * Creates new Vector when null.
	 * @return Vector
	 */
	public java.util.Vector getRecordVector()
	{
		if( recordVector == null )
			recordVector = new java.util.Vector();
			
		return recordVector;
	}
	
	/**
	 * Return VersionTools.getYUKON_VERSION().
	 * @return String
	 */
	public String getVersion()
	{
		return com.cannontech.common.version.VersionTools.getYUKON_VERSION();
	}
	
	/**
	 * Return isService.
	 * True when Export format being ran as a service.
	 * @return boolean
	 */
	private boolean isService()
	{
		return isService;
	}
	
	/**
	 * Create new LogWriter if logger null.
	 * Write event to log file and CTILogger, describing event with severity.
	 * @param event
	 * @param severity
	 */
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
	 * args must contain FORMAT=ExportFormatType.FORMATID to run successfully.
	 * parseDatFile based on formatID.
	 * Runs once if isService=false, else runs while isService=true.
	 * @param args
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
					formatBase.sleepThread.sleep(1000);		//1 second
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
	 * Read dat file keys and values to populate exportProperties and/or local properties.
	 */
	public abstract void parseDatFile();
		
	/**
	 * Query the database for export Data.
	 * Populate recordVector for each valid record created.
	 */
	abstract public void retrieveExportData();
	
	/**
	 * Main method for use with a GUI implementation (such as com.cannontech.export.gui.ExportGUI.
	 * Retrieves the exportData, writes it to the export file and exits.
	 * @param formatBase
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
	
		formatBase.logEvent("Finished " + formatBase.getClass().getName() + " File Export.", com.cannontech.common.util.LogWriter.INFO);
		logger.getPrintWriter().close();
		logger = null;
	}

	/**
	 * Method setExportProperties.
	 * @param exportProps com.cannontech.export.ExportPropertiesBase
	 */
	public void setExportProperties(com.cannontech.export.ExportPropertiesBase exportProps)
	{
		exportProperties = exportProps;
	}

	/**
	 * Return exportProperties.
	 * Creates new ExportPropertiesBase when null.
	 * @return ExportPropertiesBase
	 */
	public com.cannontech.export.ExportPropertiesBase getExportProperties()
	{
		if( exportProperties == null)
			exportProperties = new com.cannontech.export.ExportPropertiesBase();
		return exportProperties;
	}
	
	/**
	 * Set directory, create directory if it does not already exist.
	 * @param newDirectory java.lang.String
	 */
	public void setDirectory(String newDirectory)
	{
		directory = newDirectory;
		java.io.File file = new java.io.File( directory );
		file.mkdirs();
		
		logEvent("Files exported to directory: " + directory, com.cannontech.common.util.LogWriter.INFO);
	}
	
	/**
	 * Method setEmailGroup.
	 * @param newEmailGroup com.cannontech.database.db.notification.NotificationGroup
	 */
	private void setEmailGroup(com.cannontech.database.db.notification.NotificationGroup newEmailGroup)
	{
		emailGroup = newEmailGroup;
	}
	
	/**
	 * Method setIsService.
	 * True when running as service or console application.
	 * @param value boolean
	 */
	public void setIsService(boolean value)
	{
		isService = value;
	}
	
	/**
	 * Write recordVector to getDirector()+getFileName().
	 * set recordVector = null when write to file is completed.
	 * Method writeToFile.
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
				recordVector = null;
			}
		}
		else
		{
			logEvent("...Exported * 0 * Records.  No file generated.", com.cannontech.common.util.LogWriter.INFO);
		}
	}

	/**
	 * Method buildKeysAndValues.
	 * @return String[][]
	 */
	public abstract String[][] buildKeysAndValues();

	/**
	 * Write Export format's properites to dat file for later reference.
	 * Used mainly for service installments.
	 * File in 'KEY=VALUE' format
	 */
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
	 * Return getRunTimeIntervalInMillis.
	 * Interval in which the application will continually run at.
	 * @return long
	 */
	public long getRunTimeIntervalInMillis()
	{
		return runTimeIntervalInMillis;
	}

	/**
	 * Set runTimeIntervalInMillis, the millisecond interval between run times.
	 * @param millis long
	 */
	public void setRunTimeIntervalInMillis(long millis)
	{
		this.runTimeIntervalInMillis = millis;
	}

	/**
	 * Return ExoprtFormatTypes.getFormatTypeName(formatID) as the string value.
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return ExportFormatTypes.getFormatTypeName(getExportProperties().getFormatID());
	}
}

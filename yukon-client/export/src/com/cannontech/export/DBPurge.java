package com.cannontech.export;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2002 9:03:19 AM)
 * @author: 
 */
//import com.ms.service.*;
public class DBPurge extends ExportFormatBase
{
	private String filePrefix = "lg";
	private String fileExtension = ".cvt";

	private String logText = null;
	private String severityLevel = " I ";

	private java.util.GregorianCalendar maxDateToPurge = null;
	
	private static java.text.SimpleDateFormat FILENAME_FORMAT = new java.text.SimpleDateFormat("yyMMdd");
	private static java.text.SimpleDateFormat LOGTEXT_DATE_FORMAT = new java.text.SimpleDateFormat(" M/d/yy hh:mm:ss a ");

/**
 * DBPurge constructor comment.
 */
public DBPurge()
{
	super();

}
public String appendBatchFileParms(String batchString)
{
	batchString += "com.cannontech.export.ExportFormatBase ";

	batchString += "FORMAT=" + ExportFormatTypes.DBPURGE_FORMAT + " ";
	
	batchString += "FILE="+ getDirectory() + " " ;

	batchString += "-d" + getExportProperties().getDaysToRetain() + " ";

	batchString += "-h" + getExportProperties().getRunTimeHour().intValue() + " ";

	if( !getExportProperties().isPurgeData() )
		batchString += "-NOPURGE ";

	return batchString;
}
/**
 * Insert the method's description here.
 * Creation date: (3/18/2002 4:13:26 PM)
 * @return java.lang.String
 */
public String getFileName()
{
	String name = new String();
	name += filePrefix;
	name += FILENAME_FORMAT.format(getMaxDateToPurge().getTime());
	name += fileExtension;

	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (3/18/2002 2:25:37 PM)
 * @return java.util.Date
 */
private java.util.GregorianCalendar getMaxDateToPurge() 
{
	if( maxDateToPurge == null)
		setMaxDateToPurge();
	return maxDateToPurge;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 2:38:29 PM)
 */
public void initialize()
{
}
/**
 * Insert the method's description here.
 * Creation date: (3/18/2002 3:36:13 PM)
 * @param args java.lang.String[]
 */
public void parseCommandLineArgs(String[] args)
{
	if( args.length > 0 )
	{
		for ( int i = 0; i < args.length; i++)
		{
			String argSubString = (String)args[i].substring(2);
			String argUpper = (String)args[i].toUpperCase();
			
			if( args[i].startsWith("-d") )//|| args[i].startsWith("-D")
			{
				getExportProperties().setDaysToRetain( Integer.parseInt(argSubString) );
				setMaxDateToPurge();
			}
			else if( argUpper.startsWith("FILE"))
			{
				int startIndex = argUpper.indexOf("=") + 1;
				String subString = argUpper.substring(startIndex);

				setDirectory(subString);
				java.io.File file = new java.io.File( getDirectory() );
				file.mkdirs();
			}
			else if( args[i].startsWith("-f") || args[i].startsWith("-F"))
			{
				setDirectory( argSubString );
				java.io.File file = new java.io.File( getDirectory() );
				file.mkdirs();
			}
			else if( args[i].startsWith("-h") || args[i].startsWith("-H"))
			{
				getExportProperties().setRunTimeHour(Integer.parseInt(argSubString));
			}
			else if( argUpper.startsWith("-NOPURGE"))
			{
				getExportProperties().setPurgeData(false);
			}

		}
	}
	else
	{
		logEvent("Usage:  format=<formatID> -dNumDaysToRetain -fFileDirectory -hrunTimeHour", com.cannontech.common.util.LogWriter.INFO);
		logEvent("Ex.		format=1 -d90 -fc:/yukon/dbpurge/ -h1 {optional: -NOPURGE}", com.cannontech.common.util.LogWriter.INFO);
		logEvent("** All parameters will be defaulted to the above if not specified", com.cannontech.common.util.LogWriter.INFO);
	}

}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2002 9:45:04 AM)
 */
public void purgeSystemLog()
{
	long timer = System.currentTimeMillis();	

	String deleteString = "DELETE FROM SYSTEMLOG WHERE DATETIME < ?";

	logEvent("...Purging database for Max Date = " + getMaxDateToPurge().getTime(), com.cannontech.common.util.LogWriter.INFO);
	java.sql.PreparedStatement pstmt = null;
	java.sql.Connection conn = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		pstmt = conn.prepareStatement( deleteString );
		pstmt.setTimestamp(1, new java.sql.Timestamp(getMaxDateToPurge().getTime().getTime()));
		pstmt.executeUpdate();
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		catch (java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}
	logEvent("...PURGING SYSTEMLOG DATA: Took " + (System.currentTimeMillis() - timer) + " millis.", com.cannontech.common.util.LogWriter.INFO);
}
/**
 * Insert the method's description here.
 * Creation date: (2/20/2002 9:45:04 AM)
 */
public void retrieveExportData()
{
	long timer = System.currentTimeMillis();	

	String sqlString =	"SELECT LOGID, SL.POINTID, DATETIME, TYPE, PRIORITY, ACTION, DESCRIPTION, P.POINTNAME, P.POINTTYPE "+
						"FROM SYSTEMLOG SL, POINT P WHERE DATETIME < ? " +
						"AND SL.POINTID = P.POINTID";

	java.sql.PreparedStatement pstmt = null;
	java.sql.Connection conn = null;
	java.sql.ResultSet rset = null;

	logEvent("DBPurge for for Max Date = " + getMaxDateToPurge().getTime(), com.cannontech.common.util.LogWriter.INFO);
	try
	{
		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		pstmt =	conn.prepareStatement( sqlString );

		pstmt.setTimestamp(1, new java.sql.Timestamp(getMaxDateToPurge().getTime().getTime()));
		rset = pstmt.executeQuery();
		
		logEvent(" *Start looping through return resultset", com.cannontech.common.util.LogWriter.INFO);		
		while (rset.next())
		{
			Integer logid = new Integer(rset.getInt(1));
			Integer pointID = new Integer(rset.getInt(2));
			java.util.GregorianCalendar datetime = new java.util.GregorianCalendar();
			datetime.setTime(rset.getTimestamp(3));
			Integer type = new Integer(rset.getInt(4));
			Integer priority = new Integer(rset.getString(5));
			String action = (String)(rset.getString(6));
			String description = (String)(rset.getString(7));
			String pointName = (String)(rset.getString(8));
			String pointType = (String)(rset.getString(9));

			com.cannontech.export.record.DBPurgeRecord dbPurgeRec = new com.cannontech.export.record.DBPurgeRecord();

			dbPurgeRec.setLogId(logid);
			dbPurgeRec.setDateTime(datetime);

			if( pointID.intValue() < 0)
				dbPurgeRec.setOriginator(pointName);
			else
				dbPurgeRec.setOriginator(pointType + "Pt");

			String logTypeText = null;
			if (priority.intValue() > 1)
			{
				dbPurgeRec.setLogType("Alarm");
				logTypeText = " A ";
			}
			else
			{
				dbPurgeRec.setLogType("Log");
				logTypeText = " L ";
			}
	
			dbPurgeRec.setSeverity(priority);
			
			String templogtext = logTypeText + LOGTEXT_DATE_FORMAT.format(datetime.getTime()) + " " + dbPurgeRec.getOriginator() ;
			if( action != null)
				templogtext += " "+ action;
			if(description != null)
			{
				String tempDesc = new String();
				int indexBeginQuote = description.indexOf("\"");
				int indexEndQuote = description.lastIndexOf("\"");
				if( indexBeginQuote >= 0 )
				{			
					tempDesc = description.substring(0, indexBeginQuote);
					tempDesc += description.substring(indexBeginQuote +1, indexEndQuote);
					tempDesc += description.substring(indexEndQuote + 1);

					description = tempDesc;
				}
				templogtext += " " + description;
			}
			dbPurgeRec.setLogText(templogtext);
				
			String dataString = dbPurgeRec.dataToString();
			getRecordVector().add(dataString);
		}

		if(getExportProperties().isPurgeData())
			purgeSystemLog();
	}
	catch (java.sql.SQLException e)
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if (pstmt != null)
				pstmt.close();
			if (rset != null)
				rset.close();
			if (conn != null)
				conn.close();
		}
		catch (java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}
	logEvent("@" + this.toString() +" Data Collection : Took " + (System.currentTimeMillis() - timer) + " millis", com.cannontech.common.util.LogWriter.INFO);
}
/**
 * Insert the method's description here.
 * Creation date: (3/18/2002 11:29:13 AM)
 * @param daysToRetain int
 */
private void setMaxDateToPurge()
{
	int DAY = 86400;
	int MILLI_CONVERT = 1000;
	
	long daysInMillis = (new Integer( DAY * getExportProperties().getDaysToRetain())).longValue() * MILLI_CONVERT;
	java.util.GregorianCalendar today = new java.util.GregorianCalendar();
	today.set(java.util.GregorianCalendar.HOUR_OF_DAY, 0);
	today.set(java.util.GregorianCalendar.MINUTE, 0);
	today.set(java.util.GregorianCalendar.SECOND, 0);

	long newTime = (today.getTime().getTime() - daysInMillis);
	today.setTime(new java.util.Date(newTime));

	maxDateToPurge = today;
}
}

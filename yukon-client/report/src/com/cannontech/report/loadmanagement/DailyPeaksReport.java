package com.cannontech.report.loadmanagement;

/**
 * Insert the type's description here.
 * Creation date: (3/21/2002 4:04:12 PM)
 * @author: 
 */
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.report.ReportBase;
import com.cannontech.report.ReportTypes;
public class DailyPeaksReport extends ReportBase
{
	private static final int MAX_NUMBER_OF_PEAK_VALUES = 12;

	class TempControlAreaObject
	{
		// holds just the info needed for this report for each control area
		private Integer controlAreaId = null;
		private String controlAreaName = null;
		private Integer defDailyStartTime = null;
		private Integer defDailyStopTime = null;
		private Double threshold = null;
		private Integer peakPointId = null;

		public Integer getControlAreaId()
		{
			return controlAreaId;
		}
		public String getControlAreaName()
		{
			return controlAreaName;
		}
		public Integer getDefDailyStartTime()
		{
			return defDailyStartTime;
		}
		public Integer getDefDailyStopTime()
		{
			return defDailyStopTime;
		}
		public Double getThreshold()
		{
			return threshold;
		}
		public Integer getPeakPointId()
		{
			return peakPointId;
		}
		public void setControlAreaId(Integer paoId)
		{
			controlAreaId = paoId;
		}
		public void setControlAreaName(String name)
		{
			controlAreaName = name;
		}
		public void setDefDailyStartTime(Integer defStart)
		{
			defDailyStartTime = defStart;
		}
		public void setDefDailyStopTime(Integer defStop)
		{
			defDailyStopTime = defStop;
		}
		public void setThreshold(Double thres)
		{
			threshold = thres;
		}
		public void setPeakPointId(Integer pid)
		{
			peakPointId = pid;
		}
	}	
/**
 * LoadsShedReport constructor comment.
 */
public DailyPeaksReport()
{
	super();
}


/**
 * Returns true if the line is a line from Group1Header
 * @param line
 * @return
 */
public boolean isGroup1HeaderLine(String line)
{
	if( line.startsWith("                   PEAK") )	//a keyword for the Group name info
	{
	    groupLineCount = 1;
	    return true;
	}
	else if( groupLineCount < 3  && groupLineCount > 0)
	{
	    groupLineCount++;
	    return true;
	}
	groupLineCount = 0;	//reset the group line count, until the next grouping is found
    return false;
}

public boolean isPageHeaderLine(String line)
{
    if( line.startsWith("Printed on:"))//a keyword from the Page header info
    {
        pageLineCount = 1;
        return true;
    }
    else if( pageLineCount < 2 && pageLineCount > 0)
    {
        pageLineCount++;
        return true;
    }
    pageLineCount = 0;
    return false;
}

/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveReportData(String dbAlias)
{
	boolean returnBoolean = false;
	
	//clear out all old values if retreiving data
	outputStringsVector = null;

	if( dbAlias == null )
	{
		dbAlias = CtiUtilities.getDatabaseAlias();
	}

	java.sql.Connection conn = null;
	try
	{
		conn = PoolManager.getInstance().getConnection(dbAlias);
		if( conn == null )
		{
			CTILogger.info(getClass() + ":  Error getting database connection.");
			return false;
		}

		java.util.Vector controlAreaVector = new java.util.Vector();
		{
			StringBuffer sqlString = new StringBuffer("select paobjectid, paoname, defdailystarttime, defdailystoptime, threshold, peakpointid " +
						"from yukonpaobject, lmcontrolarea ca, lmcontrolareatrigger trig " +
						"where paobjectid = ca.deviceid and ca.deviceid = trig.deviceid and peakpointid > 0 " +
						"order by paoname");

			java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlString.toString());
			java.sql.ResultSet rset = null;
			rset = pstmt.executeQuery();
			if( rset != null )
			{
				while(rset.next())
				{
					TempControlAreaObject tempObj = new TempControlAreaObject();
					tempObj.setControlAreaId(new Integer(rset.getInt(1)));
					tempObj.setControlAreaName(rset.getString(2));
					tempObj.setDefDailyStartTime(new Integer(rset.getInt(3)));
					tempObj.setDefDailyStopTime(new Integer(rset.getInt(4)));
					tempObj.setThreshold(new Double(rset.getDouble(5)));
					tempObj.setPeakPointId(new Integer(rset.getInt(6)));
					controlAreaVector.addElement(tempObj);
				}
			}
		}
		
		for(int i=0;i<controlAreaVector.size();i++)
		{
			StringBuffer sqlString2 = new StringBuffer("select changeid, pointid, value, quality, timestamp " +
						"from rawpointhistory where pointid = ? and timestamp > ? and timestamp <= ? " +
						"order by value desc");

			java.sql.PreparedStatement pstmt2 = conn.prepareStatement(sqlString2.toString());
			pstmt2.setInt(1,((TempControlAreaObject)controlAreaVector.get(i)).getPeakPointId().intValue());
			pstmt2.setObject(2,new java.sql.Date(getStartDate().getTime().getTime()));
			pstmt2.setObject(3,new java.sql.Date(getStopDate().getTime().getTime()));
			java.sql.ResultSet rset2 = null;

			{
				rset2 = pstmt2.executeQuery();
				if( rset2 != null )
				{
					java.util.Vector controlTimePeakVector = new java.util.Vector(MAX_NUMBER_OF_PEAK_VALUES);
					java.util.Vector nonControlTimePeakVector = new java.util.Vector(MAX_NUMBER_OF_PEAK_VALUES);

					int defDailyStartTime = ((TempControlAreaObject)controlAreaVector.get(i)).getDefDailyStartTime().intValue();
					int defDailyStopTime = ((TempControlAreaObject)controlAreaVector.get(i)).getDefDailyStopTime().intValue();

					if( defDailyStartTime < 0 )
					{
						defDailyStartTime = 0;
					}
					if( defDailyStopTime < 0 )
					{
						defDailyStopTime = 86400;
					}

					while( rset2.next() && (controlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES || nonControlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES) )
					{
						Integer changeId = new Integer(rset2.getInt(1));
						Integer pointId = new Integer(rset2.getInt(2));
						Double value = new Double(rset2.getDouble(3));
						Integer quality = new Integer(rset2.getInt(4));
						java.util.GregorianCalendar timestamp = new java.util.GregorianCalendar();
						timestamp.setTime( new java.util.Date((rset2.getTimestamp(5)).getTime()) );

						int timeInSeconds = (timestamp.get(Calendar.HOUR_OF_DAY) * 3600) + (timestamp.get(Calendar.MINUTE) * 60) + (timestamp.get(Calendar.SECOND));
						if( controlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES &&
								( ( defDailyStartTime < timeInSeconds && defDailyStopTime >= timeInSeconds ) ||
									( defDailyStopTime == 86400 && timeInSeconds == 0 ) ) )
						{
							controlTimePeakVector.add(new RawPointHistory( changeId, pointId, timestamp,	quality, value));
						}
						else if( nonControlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES &&
									( defDailyStartTime >= timeInSeconds || defDailyStopTime < timeInSeconds ) )
						{
							nonControlTimePeakVector.add(new RawPointHistory( changeId, pointId, timestamp,	quality, value));
						}
					}

					for(int j=0;j<controlTimePeakVector.size();j++)
					{
						DailyPeaksRecord dailyPeaksRec = null;
						if( j<nonControlTimePeakVector.size() )
						{
							dailyPeaksRec = new DailyPeaksRecord(((TempControlAreaObject)controlAreaVector.get(i)).getControlAreaName(),
															((RawPointHistory)controlTimePeakVector.get(j)).getValue(),
															((RawPointHistory)controlTimePeakVector.get(j)).getQuality(),
															((RawPointHistory)controlTimePeakVector.get(j)).getTimeStamp(),
															((RawPointHistory)nonControlTimePeakVector.get(j)).getValue(),
															((RawPointHistory)nonControlTimePeakVector.get(j)).getQuality(),
															((RawPointHistory)nonControlTimePeakVector.get(j)).getTimeStamp(),
															((TempControlAreaObject)controlAreaVector.get(i)).getThreshold());
						}
						else
						{
							dailyPeaksRec = new DailyPeaksRecord(((TempControlAreaObject)controlAreaVector.get(i)).getControlAreaName(),
															((RawPointHistory)controlTimePeakVector.get(j)).getValue(),
															((RawPointHistory)controlTimePeakVector.get(j)).getQuality(),
															((RawPointHistory)controlTimePeakVector.get(j)).getTimeStamp(),
															new Double(0.0),
															new Integer(PointQualities.UNINTIALIZED_QUALITY),
															new java.util.GregorianCalendar(1990,1,1),
															((TempControlAreaObject)controlAreaVector.get(i)).getThreshold());
						}

						if( dailyPeaksRec != null )
						{
							getRecordVector().addElement(dailyPeaksRec);
						}
					}

					if(controlTimePeakVector.size() == 0)
					{
						for(int j=0;j<nonControlTimePeakVector.size();j++)
						{
							DailyPeaksRecord dailyPeaksRec = null;
							{
								dailyPeaksRec = new DailyPeaksRecord(((TempControlAreaObject)controlAreaVector.get(i)).getControlAreaName(),
																new Double(0.0),
																new Integer(PointQualities.UNINTIALIZED_QUALITY),
																new java.util.GregorianCalendar(1990,1,1),
																((RawPointHistory)nonControlTimePeakVector.get(j)).getValue(),
																((RawPointHistory)nonControlTimePeakVector.get(j)).getQuality(),
																((RawPointHistory)nonControlTimePeakVector.get(j)).getTimeStamp(),
																((TempControlAreaObject)controlAreaVector.get(i)).getThreshold());
							}

							if( dailyPeaksRec != null )
							{
								getRecordVector().addElement(dailyPeaksRec);
							}
						}
					}
					returnBoolean = true;
				}
			}
		}
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//sometin is up
		}	
	}

	return returnBoolean;
}
/* (non-Javadoc)
 * @see com.cannontech.report.ReportBase#getOutputStringsVector()
 */
public Vector getOutputStringsVector()
{
    if(outputStringsVector == null)
    {
        outputStringsVector = new Vector();
    	DecimalFormat doubleFormatter = new DecimalFormat();
    	doubleFormatter.applyPattern("###,###,##0.00");

    	String previousControlAreaName = "(null)";
    	boolean printHeader = true;
    	int currentRank = 1;
    	double currentPeakValue = 0.0;
    	double currentThreshold = 0.0;
    	java.util.GregorianCalendar currentPeakTimestamp = null;
    	java.text.SimpleDateFormat dateTimeFormatter = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		//Get some lines anyway!!
		if( getRecordVector().isEmpty())
		{
		    DailyPeaksRecord emptyRecord = new DailyPeaksRecord();
			for(int j = 0; j < emptyRecord.getControlAreaHeaderVector().size(); j++)
			{
				outputStringsVector.add( (String)emptyRecord.getControlAreaHeaderVector().get(j) );
			}
		    outputStringsVector.add("\r\n");

		    for(int j = 0; j < emptyRecord.getPeakHeaderVector().size(); j++)
			{
				outputStringsVector.add( (String)emptyRecord.getPeakHeaderVector().get(j) );
			}
		    outputStringsVector.add("\r\n");
		    outputStringsVector.add(" *** NO DATA TO REPORT *** ");
			printHeader = false;
		}
		else
		{
	    	for(int i = 0; i < getRecordVector().size();i++)
	    	{
	    		DailyPeaksRecord currentRecord = (DailyPeaksRecord)getRecordVector().get(i);
	    		String currentControlAreaName = currentRecord.getControlAreaName();
	
	    		if( !currentControlAreaName.equalsIgnoreCase(previousControlAreaName) )
	    		{
	    			if( !previousControlAreaName.equalsIgnoreCase("(null)") )
	    			{
	    			    outputStringsVector.add("\r\n");
	    			    outputStringsVector.add("Target Threshold Value is: " + doubleFormatter.format( currentThreshold ) );
	    			    outputStringsVector.add("\r\n");
	
	    				outputStringsVector.add("Current Peak of " + doubleFormatter.format( currentPeakValue ) + " occurred at " + dateTimeFormatter.format(currentPeakTimestamp.getTime()) );
	    				outputStringsVector.add("\f");
	    				currentPeakValue = 0.0;
	    				currentRank = 1;
	    			}
	    			previousControlAreaName = currentControlAreaName;
	    			currentThreshold = currentRecord.getThreshold().doubleValue();
	    			printHeader = true;
	    		}
	
	    		if( printHeader )
	    		{
	    			for(int j = 0; j < currentRecord.getControlAreaHeaderVector().size(); j++)
	    			{
	    				outputStringsVector.add( (String)currentRecord.getControlAreaHeaderVector().get(j) );
	    			}
				    outputStringsVector.add("\r\n");
	
				    for(int j = 0; j <currentRecord.getPeakHeaderVector().size(); j++)
	    			{
	    				outputStringsVector.add( (String)currentRecord.getPeakHeaderVector().get(j) );
	    			}
				    outputStringsVector.add("\r\n");
	    			printHeader = false;
	    		}
	
	    		if( currentRecord.getPeakValue().doubleValue() > currentPeakValue )
	    		{
	    			currentPeakValue = currentRecord.getPeakValue().doubleValue();
	    			currentPeakTimestamp = currentRecord.getPeakTimestamp();
	    		}
	    		if( currentRecord.getOffPeakValue().doubleValue() > currentPeakValue )
	    		{
	    			currentPeakValue = currentRecord.getOffPeakValue().doubleValue();
	    			currentPeakTimestamp = currentRecord.getOffPeakTimestamp();
	    		}
	
	    		String dataString = currentRecord.dataToString();
	    		if( dataString != null)
	    		{
	    		    String rankString = String.valueOf(currentRank);
	    			String spaces = "";
	    			for(int k = 0; k < (3 - rankString.length()); k++)
	    			{
	    			    spaces += " ";
	    			}
	    			outputStringsVector.add(spaces + rankString + "." + dataString);
				    currentRank++;
	    		}
	    	}
		}
    	//this needs to be printed on the last page also
    	if( currentPeakTimestamp != null )
    	{
		    outputStringsVector.add("\r\n");
    		outputStringsVector.add("Target Threshold Value is: " + doubleFormatter.format( currentThreshold ) );
		    outputStringsVector.add("\r\n");
    		outputStringsVector.add("Current Peak of " + doubleFormatter.format( currentPeakValue ) + " occurred at " + dateTimeFormatter.format(currentPeakTimestamp.getTime()) );
    	}
    }
    return outputStringsVector;
}

public String getReportName()
{
    return ReportTypes.REPORT_DAILY_PEAKS;
}
}

package com.cannontech.report.loadmanagement;

/**
 * Insert the type's description here.
 * Creation date: (3/21/2002 4:04:12 PM)
 * @author: 
 */
import com.cannontech.report.*;
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
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public com.klg.jclass.page.JCFlow getFlow(com.klg.jclass.page.JCDocument doc)
{
	java.text.DecimalFormat doubleFormatter = new java.text.DecimalFormat();
	doubleFormatter.applyPattern("###,###,##0.00");

	com.klg.jclass.page.JCFlow returnFlow = new com.klg.jclass.page.JCFlow(doc);

	java.util.Vector records = getRecordVector();

	java.awt.Font controlAreaHeaderFont = new java.awt.Font( "Monospaced", java.awt.Font.BOLD, 12 );
	java.awt.Font peakHeaderFont = new java.awt.Font( "Monospaced", java.awt.Font.BOLD, 8 );
	java.awt.Font normalFont = new java.awt.Font( "Monospaced", java.awt.Font.PLAIN, 8 );

	com.klg.jclass.page.JCTextStyle currentTextStyle = new com.klg.jclass.page.JCTextStyle("Current");
	currentTextStyle.setParagraphSpacing(1);
	returnFlow.setCurrentTextStyle( currentTextStyle );

	String previousControlAreaName = "(null)";
	boolean printHeader = true;
	int currentRank = 1;
	double currentPeakValue = 0.0;
	double currentThreshold = 0.0;
	java.util.GregorianCalendar currentPeakTimestamp = null;
	java.text.SimpleDateFormat dateTimeFormatter = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	for(int i=0;i<records.size();i++)
	{
		DailyPeaksRecord currentRecord = (DailyPeaksRecord)records.get(i);
		String currentControlAreaName = currentRecord.getControlAreaName();

		if( !currentControlAreaName.equalsIgnoreCase(previousControlAreaName) )
		{
			if( !previousControlAreaName.equalsIgnoreCase("(null)") )
			{
				returnFlow.newLine();
				returnFlow.newLine();
				returnFlow.newLine();
				returnFlow.print( "Target Threshold Value is: " + doubleFormatter.format( currentThreshold ) );
				returnFlow.newLine();
				returnFlow.newLine();
				returnFlow.print( "Current Peak of " + doubleFormatter.format( currentPeakValue ) + " occurred at " + dateTimeFormatter.format(currentPeakTimestamp.getTime()) );
				returnFlow.newPage();
				currentPeakValue = 0.0;
				currentRank = 1;
			}
			previousControlAreaName = currentControlAreaName;
			currentThreshold = currentRecord.getThreshold().doubleValue();
			printHeader = true;
		}

		if( printHeader )
		{
			currentTextStyle.setFont(controlAreaHeaderFont);
			for(int j=0;j<currentRecord.getControlAreaHeaderVector().size();j++)
			{
				returnFlow.print( (String)currentRecord.getControlAreaHeaderVector().get(j) );
				returnFlow.newLine();
				returnFlow.newLine();
			}
			currentTextStyle.setFont(peakHeaderFont);
			returnFlow.newLine();
			for(int j=0;j<currentRecord.getPeakHeaderVector().size();j++)
			{
				returnFlow.print( (String)currentRecord.getPeakHeaderVector().get(j) );
				returnFlow.newLine();
			}
			returnFlow.newLine();
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
			currentTextStyle.setFont(normalFont);
			String rankString = Integer.toString(currentRank);
			returnFlow.print(rankString);
			for(int k=0;k<(5-rankString.length());k++)
			{
				returnFlow.print(" ");
			}
			returnFlow.print(dataString);
			returnFlow.newLine();
			currentRank++;
		}
	}
	//this needs to be printed on the last page also
	if( currentPeakTimestamp != null )
	{
		returnFlow.newLine();
		returnFlow.newLine();
		returnFlow.newLine();
		returnFlow.print( "Target Threshold Value is: " + doubleFormatter.format( currentThreshold ) );
		returnFlow.newLine();
		returnFlow.newLine();
		returnFlow.print( "Current Peak of " + doubleFormatter.format( currentPeakValue ) + " occurred at " + dateTimeFormatter.format(currentPeakTimestamp.getTime()) );
	}

	return returnFlow;
}
/**
 * Insert the method's description here.
 * Creation date: (11/29/00)
 */
public StringBuffer getOutputAsStringBuffer()
{
	StringBuffer returnBuffer = new StringBuffer("getOutputAsStringBuffer() ifs not implemented yet!!");

	/*java.util.Vector records = getRecordVector();

	String previousControlAreaName = "(null)";
	String previousGroupName = "(null)";
	int linesOnCurrentPage = 0;
	for(int i=0;i<records.size();i++)
	{
		LoadsShedRecord currentRecord = (LoadsShedRecord)records.get(i);
		String currentControlAreaName = currentRecord.getControlAreaName();
		String currentGroupName = currentRecord.getPaoName();

		if( linesOnCurrentPage >= 55 || !currentControlAreaName.equalsIgnoreCase(previousControlAreaName) )
		{
			if( !previousControlAreaName.equalsIgnoreCase("(null)") )
			{
				returnBuffer.append( (char)12 );//form feed
			}
			previousControlAreaName = currentControlAreaName;
			linesOnCurrentPage = 0;
		}

		if( linesOnCurrentPage == 0 )
		{
			previousGroupName = currentGroupName;
			for(int j=0;j<currentRecord.getControlAreaHeaderVector().size();j++)
			{
				returnBuffer.append( (String)currentRecord.getControlAreaHeaderVector().get(j) );
				returnBuffer.append("\r\n");
				returnBuffer.append("\r\n");
			}
			for(int j=0;j<currentRecord.getGroupHeaderVector().size();j++)
			{
				returnBuffer.append( (String)currentRecord.getGroupHeaderVector().get(j) );
				returnBuffer.append("\r\n");
			}
			returnBuffer.append("\r\n");
			linesOnCurrentPage += 5;
		}
		else if( !currentGroupName.equalsIgnoreCase(previousGroupName) )
		{
			previousGroupName = currentGroupName;
			returnBuffer.append("\r\n");
			for(int j=0;j<currentRecord.getGroupHeaderVector().size();j++)
			{
				returnBuffer.append( (String)currentRecord.getGroupHeaderVector().get(j) );
				returnBuffer.append("\r\n");
			}
			returnBuffer.append("\r\n");
			linesOnCurrentPage += 5;
		}

		String dataString = currentRecord.dataToString();
		if( dataString != null)
		{
			returnBuffer.append(dataString);
			returnBuffer.append("\r\n");
			linesOnCurrentPage++;
		}
	}*/

	return returnBuffer;
}
/**
 * Retrieves values from the database and inserts them in a FileFormatBase object
 * Creation date: (11/30/00)
 */
public boolean retrieveReportData(String dbAlias)
{
	boolean returnBoolean = false;

	if( dbAlias == null )
	{
		dbAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
	}

	java.sql.Connection conn = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		if( conn == null )
		{
			System.out.println(getClass() + ":  Error getting database connection.");
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
						defDailyStopTime = 0;
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

						int timeInSeconds = (timestamp.get(java.util.Calendar.HOUR_OF_DAY) * 3600) + (timestamp.get(java.util.Calendar.MINUTE) * 60) + (timestamp.get(java.util.Calendar.SECOND));
						if( controlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES &&
								( ( defDailyStartTime < timeInSeconds && defDailyStopTime >= timeInSeconds ) ||
									( defDailyStopTime == 86400 && timeInSeconds == 0 ) ) )
						{
							controlTimePeakVector.add(new com.cannontech.database.db.point.RawPointHistory( changeId, pointId, timestamp,	quality, value));
						}
						else if( nonControlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES )
						{
							nonControlTimePeakVector.add(new com.cannontech.database.db.point.RawPointHistory( changeId, pointId, timestamp,	quality, value));
						}
					}

					for(int j=0;j<controlTimePeakVector.size();j++)
					{
						DailyPeaksRecord dailyPeaksRec = null;
						if( j<nonControlTimePeakVector.size() )
						{
							dailyPeaksRec = new DailyPeaksRecord(((TempControlAreaObject)controlAreaVector.get(i)).getControlAreaName(),
															((com.cannontech.database.db.point.RawPointHistory)controlTimePeakVector.get(j)).getValue(),
															((com.cannontech.database.db.point.RawPointHistory)controlTimePeakVector.get(j)).getQuality(),
															((com.cannontech.database.db.point.RawPointHistory)controlTimePeakVector.get(j)).getTimeStamp(),
															((com.cannontech.database.db.point.RawPointHistory)nonControlTimePeakVector.get(j)).getValue(),
															((com.cannontech.database.db.point.RawPointHistory)nonControlTimePeakVector.get(j)).getQuality(),
															((com.cannontech.database.db.point.RawPointHistory)nonControlTimePeakVector.get(j)).getTimeStamp(),
															((TempControlAreaObject)controlAreaVector.get(i)).getThreshold());
						}
						else
						{
							dailyPeaksRec = new DailyPeaksRecord(((TempControlAreaObject)controlAreaVector.get(i)).getControlAreaName(),
															((com.cannontech.database.db.point.RawPointHistory)controlTimePeakVector.get(j)).getValue(),
															((com.cannontech.database.db.point.RawPointHistory)controlTimePeakVector.get(j)).getQuality(),
															((com.cannontech.database.db.point.RawPointHistory)controlTimePeakVector.get(j)).getTimeStamp(),
															new Double(0.0),
															new Integer(com.cannontech.database.data.point.PointQualities.UNINTIALIZED_QUALITY),
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
																new Integer(com.cannontech.database.data.point.PointQualities.UNINTIALIZED_QUALITY),
																new java.util.GregorianCalendar(1990,1,1),
																((com.cannontech.database.db.point.RawPointHistory)nonControlTimePeakVector.get(j)).getValue(),
																((com.cannontech.database.db.point.RawPointHistory)nonControlTimePeakVector.get(j)).getQuality(),
																((com.cannontech.database.db.point.RawPointHistory)nonControlTimePeakVector.get(j)).getTimeStamp(),
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
}

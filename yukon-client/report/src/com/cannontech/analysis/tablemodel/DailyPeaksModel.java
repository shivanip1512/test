/*
 * Created on Feb 10, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.tablemodel;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.lm.DailyPeak;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.point.CTIPointQuailtyException;
import com.cannontech.database.data.point.PointQualities;
import com.cannontech.database.db.device.lm.LMControlArea;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.cannontech.database.db.point.RawPointHistory;
/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DailyPeaksModel extends ReportModelBase
{
	private static final int MAX_NUMBER_OF_PEAK_VALUES = 12;
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 13;

	/** Enum values for column representation */
	public final static int CONTROL_ARAEA_COLUMN = 0;
	public final static int PEAK_TITLE_COLUMN = 1;
	public final static int OFF_PEAK_TITLE_COLUMN = 2;
	public final static int RANK_COLUMN = 3;
	public final static int PEAK_VALUE_COLUMN = 4;
	public final static int PEAK_QUALITY_COLUMN = 5;
	public final static int PEAK_TIME_COLUMN = 6;
	public final static int OFF_PEAK_VALUE_COLUMN = 7;
	public final static int OFF_PEAK_QUALITY_COLUMN = 8;
	public final static int OFF_PEAK_TIME_COLUMN = 9;
	public final static int THRESHOLD_COLUMN = 10;
	public final static int CURRENT_PEAK_VALUE_COLUMN = 11;
	public final static int CURRENT_PEAK_TIMESTAMP_COLUMN = 12;

	/** String values for column representation */
	public final static String CONTROL_AREA_STRING = "Control Area";
	public final static String PEAK_TIME_TITLE_STRING = "PEAK";
	public final static String OFF_PEAK_TITLE_STRING = "OFF PEAK";
	public final static String RANK_STRING = "Rank";
	public final static String PEAK_VALUE_STRING = "Peak Value";
	public final static String PEAK_QUALITY_STRING = "Peak Quality";
	public final static String PEAK_TIME_STRING= "Peak Time";
	public final static String OFF_PEAK_VALUE_STRING = "Off Peak Value";
	public final static String OFF_PEAK_QUALITY_STRING = "Off Peak Quality";
	public final static String OFF_PEAK_TIME_STRING = "Off Peak Time";
	public final static String THRESHOLD_STRING = "Target Threshold Value: ";
	public final static String CURRENT_PEAK_VALUE_STRING = "Current Peak: ";
	public final static String CURRENT_PEAK_TIMESTAMP_STRING = "Ocurred on: ";

	/** A string for the title of the data */
	private static String title = "Daily Peaks Report";

	/** Array of IDs (of controlAreas paobjectIDs)*/
	private int controlAreas[] = null;
	
	private Double currentPeakValue = null;
	private GregorianCalendar currentPeakTimestamp = null;
	
	public static Comparator lmControlAreaPAONameComparator = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			LMControlArea object1 = ((TempControlAreaObject)o1).getLMControlArea();
			LMControlArea object2 = ((TempControlAreaObject)o2).getLMControlArea();
			String thisVal = PAOFuncs.getYukonPAOName(object1.getDeviceID().intValue());
			String anotherVal = PAOFuncs.getYukonPAOName(object2.getDeviceID().intValue());
			return ( thisVal.compareToIgnoreCase(anotherVal));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	class TempControlAreaObject
	{
		// holds just the info needed for this report for each control area
		private LMControlArea lmca = null;
		private LMControlAreaTrigger lmcat = null; 

		public LMControlArea getLMControlArea()
		{
			return lmca;
		}

		public LMControlAreaTrigger getLMControlAreaTrigger()
		{
			return lmcat;
		}

		public void setLMControlArea(LMControlArea area)
		{
			lmca = area;
		}

		public void setLMControlAreaTrigger(LMControlAreaTrigger trigger)
		{
			lmcat = trigger;
		}

	}	
	public DailyPeaksModel()
	{
		super();
	}	

	/**
	 * Constructor class
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTime
	 * 
	 * A null loadGroup is specified, which means ALL Load Groups!
	 */
	public DailyPeaksModel(long startTime_, long stopTime_)
	{
		this(null, startTime_, stopTime_);
	}	
	
	/**
	 * Constructor class
	 * @param loadGroups_ (Array of)YukonPaobject.paobjectID (of single load group)
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTime
	 */
	public DailyPeaksModel( int[] paoIDs_,long startTime_, long stopTime_)
	{
		super(startTime_, stopTime_);
		setPaoIDs(paoIDs_);
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		java.sql.Connection conn = null;
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			java.sql.ResultSet rset = null;
			if( conn == null )
			{
				CTILogger.info(getClass() + ":  Error getting database connection.");
				return;
			}

			java.util.Vector controlAreaVector = new java.util.Vector();
			StringBuffer sqlString = new StringBuffer("select ca.deviceid, defdailystarttime, defdailystoptime, threshold, peakpointid " +
							"from lmcontrolarea ca, lmcontrolareatrigger trig " +
							"where ca.deviceid = trig.deviceid and peakpointid > 0");

			java.sql.PreparedStatement pstmt = conn.prepareStatement(sqlString.toString());
			rset = pstmt.executeQuery();
			
			if( rset != null )
			{
				while(rset.next())
				{
					LMControlArea lmca = new LMControlArea();
					Integer deviceID = new Integer(rset.getInt(1)); 
					lmca.setDeviceID(deviceID);
					lmca.setDefDailyStartTime(new Integer(rset.getInt(2)));
					lmca.setDefDailyStopTime(new Integer(rset.getInt(3)));
					LMControlAreaTrigger lmcat = new LMControlAreaTrigger();
					lmcat.setDeviceID(deviceID);
					lmcat.setThreshold(new Double(rset.getDouble(4)));
					lmcat.setPeakPointID(new Integer(rset.getInt(5)));
					TempControlAreaObject tempLM = new TempControlAreaObject();
					tempLM.setLMControlArea(lmca);
					tempLM.setLMControlAreaTrigger(lmcat);
					controlAreaVector.add(tempLM);
				}
			}
			if( !controlAreaVector.isEmpty())
				Collections.sort(controlAreaVector, lmControlAreaPAONameComparator);
			rset = null;	//clean it out
			for(int i=0;i<controlAreaVector.size();i++)
			{
				StringBuffer sqlString2 = new StringBuffer("select changeid, pointid, value, quality, timestamp " +
							"from rawpointhistory where pointid = ? and timestamp > ? and timestamp <= ? " +
							"order by value desc");

				java.sql.PreparedStatement pstmt2 = conn.prepareStatement(sqlString2.toString());
				pstmt2.setInt(1,((TempControlAreaObject)controlAreaVector.get(i)).getLMControlAreaTrigger().getPeakPointID().intValue());
				pstmt2.setObject(2, new Timestamp(getStartTime()));
				pstmt2.setObject(3,new Timestamp(getStopTime()));

				rset = pstmt2.executeQuery();
				if( rset != null )
				{
					Vector controlTimePeakVector = new java.util.Vector(MAX_NUMBER_OF_PEAK_VALUES);
					Vector nonControlTimePeakVector = new java.util.Vector(MAX_NUMBER_OF_PEAK_VALUES);

					int defDailyStartTime = ((TempControlAreaObject)controlAreaVector.get(i)).getLMControlArea().getDefDailyStartTime().intValue();
					int defDailyStopTime = ((TempControlAreaObject)controlAreaVector.get(i)).getLMControlArea().getDefDailyStopTime().intValue();

					if( defDailyStartTime < 0 )
						defDailyStartTime = 0;

					if( defDailyStopTime < 0 )
						defDailyStopTime = 86400;

					while( rset.next() && ( controlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES || nonControlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES) )
					{
						Integer changeId = new Integer(rset.getInt(1));
						Integer pointId = new Integer(rset.getInt(2));
						Double value = new Double(rset.getDouble(3));
						Integer quality = new Integer(rset.getInt(4));
						GregorianCalendar timestamp = new GregorianCalendar();
						timestamp.setTime( new Date((rset.getTimestamp(5)).getTime()) );
						RawPointHistory rph = new RawPointHistory(changeId, pointId, timestamp, quality, value );

						int timeInSeconds = (timestamp.get(Calendar.HOUR_OF_DAY) * 3600) + (timestamp.get(Calendar.MINUTE) * 60) + (timestamp.get(Calendar.SECOND));
						if( controlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES &&
								( ( defDailyStartTime < timeInSeconds && defDailyStopTime >= timeInSeconds ) ||
									( defDailyStopTime == 86400 && timeInSeconds == 0 ) ) )
						{
							controlTimePeakVector.add(rph);
						}
						else if( nonControlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES &&
									( defDailyStartTime >= timeInSeconds || defDailyStopTime < timeInSeconds ) )
						{
							nonControlTimePeakVector.add(rph);
						}
					}

					int rank = 1;
					for(int j = 0; j <controlTimePeakVector.size();j++)
					{
						DailyPeak dailyPeak = null;
						if( j < nonControlTimePeakVector.size() )
						{
							dailyPeak= new DailyPeak( ((TempControlAreaObject)controlAreaVector.get(i)).getLMControlArea().getDeviceID(),
													((RawPointHistory)controlTimePeakVector.get(j)).getValue(),
													((RawPointHistory)controlTimePeakVector.get(j)).getQuality(),
													((RawPointHistory)controlTimePeakVector.get(j)).getTimeStamp(),
													((RawPointHistory)nonControlTimePeakVector.get(j)).getValue(),
													((RawPointHistory)nonControlTimePeakVector.get(j)).getQuality(),
													((RawPointHistory)nonControlTimePeakVector.get(j)).getTimeStamp(),
													((TempControlAreaObject)controlAreaVector.get(i)).getLMControlAreaTrigger().getThreshold(),
													new Integer(rank));
						}
						else
						{
							dailyPeak = new DailyPeak(((TempControlAreaObject)controlAreaVector.get(i)).getLMControlArea().getDeviceID(),
													((RawPointHistory)controlTimePeakVector.get(j)).getValue(),
													((RawPointHistory)controlTimePeakVector.get(j)).getQuality(),
													((RawPointHistory)controlTimePeakVector.get(j)).getTimeStamp(),
													new Double(0.0), null, null,
													((TempControlAreaObject)controlAreaVector.get(i)).getLMControlAreaTrigger().getThreshold(),
													new Integer(rank));
						}

						getData().add(dailyPeak);
						rank++;
					}

					if(controlTimePeakVector.size() == 0)
					{
						for(int j = 0; j < nonControlTimePeakVector.size(); j++)
						{
							DailyPeak dailyPeak = new DailyPeak(((TempControlAreaObject)controlAreaVector.get(i)).getLMControlArea().getDeviceID(),
												new Double(0.0), null, null,
												((RawPointHistory)nonControlTimePeakVector.get(j)).getValue(),
												((RawPointHistory)nonControlTimePeakVector.get(j)).getQuality(),
												((RawPointHistory)nonControlTimePeakVector.get(j)).getTimeStamp(),
												((TempControlAreaObject)controlAreaVector.get(i)).getLMControlAreaTrigger().getThreshold(),
												new Integer(rank));
							
							getData().add(dailyPeak);
							rank++;
						}
					}
				}
			}
			retrieveCurrentPeakData();
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

		
	}
	/**
	 * 
	 */
	private void retrieveCurrentPeakData()
	{
		if( !getData().isEmpty())
		{
			//init the current data values with the first data item we have
			DailyPeak dp = (DailyPeak)getData().get(0);
			setCurrentPeakValue(dp.getPeakValue());
			setCurrentPeakTimestamp((GregorianCalendar)dp.getPeakTimestamp().clone());
			for(int i = 0; i < getData().size(); i++)
			{
				dp = (DailyPeak)getData().get(i);
				//Nasty way to collect the current Peak Value from the data array
				if( dp.getPeakValue().doubleValue() > currentPeakValue.doubleValue() )
				{
					currentPeakValue = dp.getPeakValue();
					currentPeakTimestamp = (GregorianCalendar)dp.getPeakTimestamp().clone();
				}
				if( dp.getOffPeakValue().doubleValue() > currentPeakValue.doubleValue() )
				{
					currentPeakValue = dp.getOffPeakValue();
					currentPeakTimestamp = (GregorianCalendar)dp.getOffPeakTimestamp().clone();
				}
			}
		}		
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof DailyPeak)
		{
			DailyPeak dp = ((DailyPeak)o);
			switch( columnIndex)
			{
				case CONTROL_ARAEA_COLUMN:
					return PAOFuncs.getYukonPAOName(dp.getControlAreaID().intValue());
				case PEAK_TITLE_COLUMN:
					return PEAK_TIME_TITLE_STRING;
				case OFF_PEAK_TITLE_COLUMN:
					return OFF_PEAK_TITLE_STRING;
				case RANK_COLUMN:
					return dp.getRank();
				case PEAK_VALUE_COLUMN:
					return dp.getPeakValue();
				case PEAK_QUALITY_COLUMN:
				{
					if( dp.getPeakDataQuality() != null)
					{
						try{
							return PointQualities.getQuality(dp.getPeakDataQuality().intValue());
						}catch( CTIPointQuailtyException ex ){}						
					}
					return dp.getPeakDataQuality();
				}
				case PEAK_TIME_COLUMN:
				{
					if( dp.getPeakTimestamp() != null)
						return dp.getPeakTimestamp().getTime();
					return dp.getPeakTimestamp();
				}
				case OFF_PEAK_VALUE_COLUMN:
					return dp.getOffPeakValue();
				case OFF_PEAK_QUALITY_COLUMN:
				{
					if( dp.getOffPeakDataQuality() != null)
						try{
							return PointQualities.getQuality(dp.getOffPeakDataQuality().intValue());
						}catch( CTIPointQuailtyException ex ){}
					return dp.getOffPeakDataQuality();
				}				
				case OFF_PEAK_TIME_COLUMN:
				{
					if( dp.getOffPeakTimestamp() != null)
						return dp.getOffPeakTimestamp().getTime();
					return dp.getOffPeakTimestamp();
				}
				case THRESHOLD_COLUMN:
					return dp.getThreshold();
				case CURRENT_PEAK_VALUE_COLUMN:
					return this.getCurrentPeakValue();
				case CURRENT_PEAK_TIMESTAMP_COLUMN:
					return this.getCurrentPeakTimestamp().getTime();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[]{
				CONTROL_AREA_STRING,
				PEAK_TIME_TITLE_STRING,
				OFF_PEAK_TITLE_STRING,
				RANK_STRING,
				PEAK_VALUE_STRING,
				PEAK_QUALITY_STRING,
				PEAK_TIME_STRING,
				OFF_PEAK_VALUE_STRING,
				OFF_PEAK_QUALITY_STRING,
				OFF_PEAK_TIME_STRING,
				THRESHOLD_STRING,
				CURRENT_PEAK_VALUE_STRING,
				CURRENT_PEAK_TIMESTAMP_STRING
			};
		}
		return columnNames;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[]{
				String.class,
				String.class,
				String.class,
				Integer.class,
				Double.class,
				String.class,
				Date.class,
				Double.class,
				String.class,
				Date.class,
				Double.class,
				Double.class,
				Date.class
			};
		}
		return columnTypes;	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 500, null),
				new ColumnProperties(0, 1, 375, null),
				new ColumnProperties(375, 1, 375, null),
				new ColumnProperties(0, 1, 50, "#."),
				new ColumnProperties(50, 1, 60, "0.00"),
				new ColumnProperties(150, 1, 100, null),
				new ColumnProperties(250, 1, 100, "MM/dd/yyyy HH:mm:ss"),
				new ColumnProperties(400, 1, 100, "0.00"),
				new ColumnProperties(500, 1, 100, null),
				new ColumnProperties(600, 1, 100, "MM/dd/yyyy HH:mm:ss"),
				new ColumnProperties(0, 1, 250, "Target Threshold Value is:  0.00" ),
				new ColumnProperties(0, 1, 150, "Current Peak of  #0.00" ),
				new ColumnProperties(250, 1, 150, "  'occurred at'  MM/dd/yyyy HH:mm:ss")
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}

	/**
	 * @return
	 */
	public GregorianCalendar getCurrentPeakTimestamp()
	{
		return currentPeakTimestamp;
	}

	/**
	 * @return
	 */
	public Double getCurrentPeakValue()
	{
		return currentPeakValue;
	}

	/**
	 * @param calendar
	 */
	public void setCurrentPeakTimestamp(GregorianCalendar calendar)
	{
		currentPeakTimestamp = calendar;
	}

	/**
	 * @param double1
	 */
	public void setCurrentPeakValue(Double double1)
	{
		currentPeakValue = double1;
	}
}

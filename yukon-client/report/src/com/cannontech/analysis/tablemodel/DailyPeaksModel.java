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
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.lm.DailyPeak;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
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
	private Double currentPeakValue = null;
	private GregorianCalendar currentPeakTimestamp = null;
	
	public static Comparator<TempControlAreaObject> lmControlAreaPAONameComparator = new Comparator<TempControlAreaObject>()
	{
		public int compare(TempControlAreaObject o1, TempControlAreaObject o2)
		{
            String object1 = o1.getControlAreaName();
            String object2 = o2.getControlAreaName();
			String thisVal = object1;
			String anotherVal = object2;
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
        private String controlAreaName = null;
		private LMControlArea lmca = null;
		private LMControlAreaTrigger lmcat = null; 

		public LMControlArea getLMControlArea() {
			return lmca;
		}

		public LMControlAreaTrigger getLMControlAreaTrigger() {
			return lmcat;
		}

		public void setLMControlArea(LMControlArea area) {
			lmca = area;
		}

		public void setLMControlAreaTrigger(LMControlAreaTrigger trigger) {
			lmcat = trigger;
		}
		
        public String getControlAreaName() {
            return controlAreaName;
        }
        
        public void setControlAreaName(String controlAreaName) {
            this.controlAreaName = controlAreaName;
        }
	}

	public DailyPeaksModel() {
		this(null, null);
	}	

	/**
	 * Constructor class
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTime
	 * 
	 * A null loadGroup is specified, which means ALL Load Groups!
	 */
	public DailyPeaksModel(Date start_, Date stop_) {
		this(null, start_, stop_);
	}	
	
	/**
	 * Constructor class
	 * @param loadGroups_ (Array of)YukonPaobject.paobjectID (of single load group)
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTime
	 */
	public DailyPeaksModel( int[] paoIDs_,Date start_, Date stop_) {
		super(start_, stop_);
		setPaoIDs(paoIDs_);
		setFilterModelTypes(new ReportFilter[]{ 
    			ReportFilter.LMCONTROLAREA}
				);
		}
	
	@Override
	@SuppressWarnings("unchecked")
    public void collectData() {
		//Reset all objects, new data being collected!
		setData(null);
		
		java.sql.Connection conn = null;
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			java.sql.ResultSet rset = null;
			if( conn == null ) {
				CTILogger.info(getClass() + ":  Error getting database connection.");
				return;
			}

			Vector<TempControlAreaObject> controlAreaVector = new Vector<TempControlAreaObject>();
			StringBuffer sqlString = new StringBuffer("select ca.deviceid, defdailystarttime, defdailystoptime, threshold, peakpointid, paoname " +
							" from lmcontrolarea ca, lmcontrolareatrigger trig, yukonpaobject pao " +
							" where ca.deviceid = trig.deviceid " +
                            " and ca.deviceid = pao.paobjectid " +
                            " and peakpointid > 0");
				if( getPaoIDs() != null)
				{
					sqlString.append(" and ca.deviceid in (" + getPaoIDs()[0]);
					for (int i = 1; i < getPaoIDs().length; i++)
					{
						sqlString.append(", " + getPaoIDs()[i]);
					}
					sqlString.append(") ");
				}

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
                    String name = rset.getString(6);
                    tempLM.setControlAreaName(name);
					controlAreaVector.add(tempLM);
				}
			}
			if( !controlAreaVector.isEmpty())
				Collections.sort(controlAreaVector, lmControlAreaPAONameComparator);
			rset = null;	//clean it out
//			for(int i=0;i<controlAreaVector.size();i++)

            for (TempControlAreaObject controlAreaObject : controlAreaVector) {

                StringBuffer sqlString2 = new StringBuffer("select changeid, pointid, value, quality, timestamp " +
							"from rawpointhistory where pointid = ? and timestamp > ? and timestamp <= ? " +
							"order by value desc");

				java.sql.PreparedStatement pstmt2 = conn.prepareStatement(sqlString2.toString());
				pstmt2.setInt(1, controlAreaObject.getLMControlAreaTrigger().getPeakPointID().intValue());
				pstmt2.setObject(2, new Timestamp(getStartDate().getTime()));
				pstmt2.setObject(3, new Timestamp(getStopDate().getTime()));

				rset = pstmt2.executeQuery();
				if( rset != null )
				{
					Vector controlTimePeakVector = new java.util.Vector(MAX_NUMBER_OF_PEAK_VALUES);
					Vector nonControlTimePeakVector = new java.util.Vector(MAX_NUMBER_OF_PEAK_VALUES);

					int defDailyStartTime = controlAreaObject.getLMControlArea().getDefDailyStartTime().intValue();
					int defDailyStopTime = controlAreaObject.getLMControlArea().getDefDailyStopTime().intValue();

					if( defDailyStartTime < 0 )
						defDailyStartTime = 0;

					if( defDailyStopTime < 0 )
						defDailyStopTime = 86400;

					while( rset.next() && ( controlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES || nonControlTimePeakVector.size() < MAX_NUMBER_OF_PEAK_VALUES) )
					{
						Long changeId = new Long(rset.getLong(1));
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
                    String controlAreaName = controlAreaObject.getControlAreaName();
                    Integer controlAreaID = controlAreaObject.getLMControlArea().getDeviceID();
                    Double threshold = controlAreaObject.getLMControlAreaTrigger().getThreshold();
                    
                    for(int j = 0; j < Math.max(controlTimePeakVector.size(), nonControlTimePeakVector.size());j++)
                    {
                        DailyPeak dailyPeak = null;
                        Double peakValue = new Double(0.0);
                        Integer peakDataQuality = null;
                        java.util.GregorianCalendar peakTimestamp = null;
                        if( j < controlTimePeakVector.size()){
                            RawPointHistory rph = (RawPointHistory)controlTimePeakVector.get(j);
                            peakValue = rph.getValue();
                            peakDataQuality = rph.getQuality();
                            peakTimestamp = rph.getTimeStamp();
                        }

                        Double offPeakValue = new Double(0.0);
                        Integer offPeakDataQuality = null;
                        java.util.GregorianCalendar offPeakTimestamp = null;
                        if( j < nonControlTimePeakVector.size()){
                            RawPointHistory rph = (RawPointHistory)nonControlTimePeakVector.get(j);
                            offPeakValue = rph.getValue();
                            offPeakDataQuality = rph.getQuality();
                            offPeakTimestamp = rph.getTimeStamp();
                        }
                        
                        dailyPeak= new DailyPeak( controlAreaName, controlAreaID, peakValue, peakDataQuality, peakTimestamp, 
                                                  offPeakValue, offPeakDataQuality, offPeakTimestamp, threshold, new Integer(rank));

                        getData().add(dailyPeak);
                        rank++;
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
					return dp.getControlAreaName();
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
							return PointQuality.getPointQuality(dp.getPeakDataQuality().intValue()).getDescription(); 
						}catch( IllegalArgumentException ex ){}						
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
							return PointQuality.getPointQuality(dp.getOffPeakDataQuality().intValue()).getDescription(); 
						}catch( IllegalArgumentException ex ){}
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
				new ColumnProperties(50, 1, 60, "0.00####"),
				new ColumnProperties(150, 1, 100, null),
				new ColumnProperties(250, 1, 100, "MM/dd/yyyy HH:mm:ss"),
				new ColumnProperties(400, 1, 100, "0.00####"),
				new ColumnProperties(500, 1, 100, null),
				new ColumnProperties(600, 1, 100, "MM/dd/yyyy HH:mm:ss"),
				new ColumnProperties(0, 1, 250, "Target Threshold Value is:  0.00" ),
				new ColumnProperties(0, 1, 200, "Current Peak of  #0.00####" ),
				new ColumnProperties(300, 1, 200, "  '  occurred at'  MM/dd/yyyy HH:mm:ss")
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString() {
		return title;
	}

	public GregorianCalendar getCurrentPeakTimestamp() {
		return currentPeakTimestamp;
	}

	public Double getCurrentPeakValue() {
		return currentPeakValue;
	}

	public void setCurrentPeakTimestamp(GregorianCalendar calendar) {
		currentPeakTimestamp = calendar;
	}

	public void setCurrentPeakValue(Double double1) {
		currentPeakValue = double1;
	}
}

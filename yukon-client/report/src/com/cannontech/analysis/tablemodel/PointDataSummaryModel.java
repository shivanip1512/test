package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.Carrier;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteRawPointHistory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.device.DeviceMeterGroup;

/**
 * Created on Dec 15, 2003
 * @author snebben
 * DatabaseModel TableModel object
 * Innerclass object for row data is CarrierData:
 *  String paoName			- YukonPaobject.paoName (device)
 *  String paoType			- YukonPaobject.type
 *  String address			- DeviceCarrierSettings.address
 *  String routeName		- YukonPaobject.paoName (route)
 *  String collGroup		- DeviceMeterGroup.collectionGroup
 *  String testCollGroup	- DeviceMeterGroup.testCollectionGroup
 */
public class PointDataSummaryModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 35;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
	public final static int POINT_NAME_COLUMN = 1;
	public final static int TIME_COLUMN = 2;
	public final static int VALUE_COLUMN = 3; 

	public final static int START_DATE_COLUMN = 4;
	public final static int DAILY_HIGH_TIME_COLUMN = 5;
	public final static int DAILY_HIGH_COLUMN = 6;
	public final static int DAILY_AVERAGE_COLUMN = 7;
	public final static int DAILY_LOW_TIME_COLUMN = 8;
	public final static int DAILY_LOW_COLUMN = 9;
	public final static int TOTAL_KWH_COLUMN = 10;	
	public final static int SUMMARY_TITLE_COLUMN = 11;
	public final static int PEAKS_TITLE_COLUMN = 12;
	public final static int LOWS_TITLE_COLUMN = 13;

//	These are columns that could be eliminated with the use of subreporting!!!
	public final static int PEAK_TIMESTAMP_0_COLUMN = 14;
	public final static int PEAK_VALUE_0_COLUMN = 15;
	public final static int LOW_TIMESTAMP_0_COLUMN = 16;
	public final static int LOW_VALUE_0_COLUMN = 17;

	public final static int PEAK_TIMESTAMP_1_COLUMN = 18;
	public final static int PEAK_VALUE_1_COLUMN = 19;
	public final static int LOW_TIMESTAMP_1_COLUMN = 20;
	public final static int LOW_VALUE_1_COLUMN = 21;

	public final static int PEAK_TIMESTAMP_2_COLUMN = 22;
	public final static int PEAK_VALUE_2_COLUMN = 23;
	public final static int LOW_TIMESTAMP_2_COLUMN = 24;
	public final static int LOW_VALUE_2_COLUMN = 25;

	public final static int PEAK_TIMESTAMP_3_COLUMN = 26;
	public final static int PEAK_VALUE_3_COLUMN = 27;
	public final static int LOW_TIMESTAMP_3_COLUMN = 28;
	public final static int LOW_VALUE_3_COLUMN = 29;

	public final static int PEAK_TIMESTAMP_4_COLUMN = 30;
	public final static int PEAK_VALUE_4_COLUMN = 31;
	public final static int LOW_TIMESTAMP_4_COLUMN = 32;
	public final static int LOW_VALUE_4_COLUMN = 33;

// end of subreport columns
	
	public final static int POINT_TOTAL_KWH_COLUMN = 34;

	
	/** String values for column representation */
	public final static String PAO_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String START_DATE_STRING  = "Date";
	public final static String TIME_STRING = "Time";
	public final static String VALUE_STRING = "Value";
	public final static String DAILY_HIGH_STRING = "High";
	public final static String DAILY_HIGH_TIME_STRING = "High Time";
	public final static String DAILY_AVERAGE_STRING = "Average";
	public final static String DAILY_LOW_STRING = "Low";
	public final static String DAILY_LOW_TIME_STRING = "Low Time";
	public final static String TOTAL_KWH_STRING = "Total kWh";
	public final static String SUMMARY_TITLE_STRING = "Summary";
	public final static String PEAKS_TITLE_STRING = "PEAKS";
	public final static String LOWS_TITLE_STRING = "LOWS";

	public final static String PEAK_TIMESTAMP_0_STRING = "Peak Timestamp";
	public final static String PEAK_VALUE_0_STRING = "Peak Value";
	public final static String LOW_TIMESTAMP_0_STRING = "Low Timestamp";
	public final static String LOW_VALUE_0_STRING = "Low Value";

	public final static String PEAK_TIMESTAMP_1_STRING = "Peak Timestamp1";
	public final static String PEAK_VALUE_1_STRING = "Peak Value1";
	public final static String LOW_TIMESTAMP_1_STRING = "Low Timestamp1";
	public final static String LOW_VALUE_1_STRING = "Low Value1";

	public final static String PEAK_TIMESTAMP_2_STRING = "Peak Timestamp2";
	public final static String PEAK_VALUE_2_STRING = "Peak Value2";
	public final static String LOW_TIMESTAMP_2_STRING = "Low Timestamp2";
	public final static String LOW_VALUE_2_STRING = "Low Value2";

	public final static String PEAK_TIMESTAMP_3_STRING = "Peak Timestamp3";
	public final static String PEAK_VALUE_3_STRING = "Peak Value3";
	public final static String LOW_TIMESTAMP_3_STRING = "Low Timestamp3";
	public final static String LOW_VALUE_3_STRING = "Low Value3";

	public final static String PEAK_TIMESTAMP_4_STRING = "Peak Timestamp4";
	public final static String PEAK_VALUE_4_STRING = "Peak Value4";
	public final static String LOW_TIMESTAMP_4_STRING = "Low Timestamp4";
	public final static String LOW_VALUE_4_STRING = "Low Value4";

	public final static String POINT_TOTAL_KWH_STRING = "Point Total kWh";

	//contain values of Integer (pointID ) to RawPointHistory object vlaues
	private HashMap allRPHPeaks = new HashMap();
	private HashMap allRPHLows = new HashMap();
	
	/** A string for the title of the data */
	private static String title = "Load Profile Summary Report";

	//Extensions of PointTypes
	public final static int LOAD_PROFILE_POINT_TYPE = 101;	//some "unused" PointType int
	public final static int CALC_POINT_TYPE = PointTypes.CALCULATED_POINT;
	public final static int ANALOG_POINT_TYPE = PointTypes.ANALOG_POINT;
	public final static int DEMAND_ACC_POINT_TYPE = PointTypes.DEMAND_ACCUMULATOR_POINT;
	public final static int STATUS_POINT_TYPE = PointTypes.STATUS_POINT;
	
	public final static String LOAD_PROFILE_POINT_TYPE_STRING = "All Load Profile";	//some "unused" PointType int
	public final static String CALC_POINT_TYPE_STRING = "All Calculated";
	public final static String ANALOG_POINT_TYPE_STRING = "All Analog";
	public final static String DEMAND_ACC_POINT_TYPE_STRING = "All Demand Accumulator";
	public final static String STATUS_POINT_TYPE_STRING = "All Status";
	
	private int pointType = LOAD_PROFILE_POINT_TYPE;	//default to Load Profile PointTypes
	
	private final static int[] ALL_POINT_TYPES = new int[]
	{
		LOAD_PROFILE_POINT_TYPE,
		CALC_POINT_TYPE,
		ANALOG_POINT_TYPE,
		DEMAND_ACC_POINT_TYPE,
		STATUS_POINT_TYPE	
	};

	//servlet attributes/parameter strings
	private static final String ATT_POINT_TYPE = "pointType";

	public static Comparator rphValueComparator = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			double thisVal = ((LiteRawPointHistory)o1).getValue();
			double anotherVal = ((LiteRawPointHistory)o2).getValue();
			return ( thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	/**
	 * Default Constructor
	 */
	public PointDataSummaryModel()
	{
		super();
	}
	/**
	 * Constructor class
	 * @param startTime_ rph.timestamp
	 * @param stopTime_ rph.timestamp
	 */
	public PointDataSummaryModel(Date start_, Date stop_, int summaryPointType)
	{
		super(start_, stop_);
		setPointType(summaryPointType);
	}
	/**
	 * Constructor class
	 * @param startTime_ rph.timestamp
	 * @param stopTime_ rph.timestamp
	 */
	public PointDataSummaryModel(Date start_, Date stop_)
	{
		this(start_, stop_, LOAD_PROFILE_POINT_TYPE);
	}
	/**
	 * Add CarrierData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			String paoName = rset.getString(1);
			String paoType = rset.getString(2);
			String address = rset.getString(3);			
			String routeName = rset.getString(4);
			String collGroup = rset.getString(5);
			String testCollGroup = rset.getString(6);
			String meterNumber = null;
			if( getBillingGroups() != null && getBillingGroups().length > 0 ) //Have a BILLING Group, can limit query to only meters.
				meterNumber = rset.getString(7);
					
			Carrier carrier = new Carrier(paoName, paoType, meterNumber, address, routeName, collGroup, testCollGroup);
			getData().add(carrier);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve DatabaseModel data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT CHANGEID, RPH.POINTID, TIMESTAMP, QUALITY, VALUE, PAO.PAONAME ");
		
		if( getBillingGroups() != null && getBillingGroups().length > 0 ) //Have a BILLING Group, can limit query to meters.
			sql.append(", METERNUMBER ");
		
		sql.append(" FROM RAWPOINTHISTORY RPH, POINT P, YUKONPAOBJECT PAO");
		
		if( getBillingGroups() != null && getBillingGroups().length > 0 ) //Have a BILLING Group, can limit query to meters.
			sql.append(", DEVICEMETERGROUP DMG ");
			
			sql.append(" WHERE P.POINTID = RPH.POINTID " +
			" AND P.PAOBJECTID = PAO.PAOBJECTID ");	//Use PAO for ordering
			//TODO add collectiongroup, ect selection criteria 
			//Use paoIDs in query if they exist
			
			sql.append(" AND TIMESTAMP > ? AND TIMESTAMP <= ? ");
			if(getPointType() == LOAD_PROFILE_POINT_TYPE )
			{
				sql.append(" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.DEMAND_ACCUMULATOR_POINT) + "' " + 
					" AND (P.POINTOFFSET >= " + PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND + " AND P.POINTOFFSET <= " + PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND + ") ");
			}
			else if ( getPointType() == STATUS_POINT_TYPE )
			{
				sql.append(" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.STATUS_POINT) + "' ");
			}
			else if ( getPointType() == DEMAND_ACC_POINT_TYPE)
			{
				sql.append(" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.DEMAND_ACCUMULATOR_POINT) + "' ");
			}
			else if ( getPointType() == ANALOG_POINT_TYPE )
			{
				sql.append(" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.ANALOG_POINT) + "' ");
			}
			else if ( getPointType() == CALC_POINT_TYPE)
			{
				sql.append(" AND (P.POINTTYPE = '" + PointTypes.getType(PointTypes.CALCULATED_POINT) + "' " +
					" OR P.POINTTYPE = '" + PointTypes.getType(PointTypes.CALCULATED_STATUS_POINT) + "' )");
			}
			//Use paoIDs in query if they exist
			if( getBillingGroups() != null && getBillingGroups().length > 0)
			{
				sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID ");
				sql.append(" AND " + DeviceMeterGroup.getValidBillGroupTypeStrings()[getBillingGroupType()] + " IN ( '" + getBillingGroups()[0]);
				for (int i = 1; i < getBillingGroups().length; i++)
					sql.append("', '" + getBillingGroups()[i]);
				sql.append("') ");
			}
			if( getPaoIDs() != null && getPaoIDs().length > 0)
			{
				sql.append(" AND P.PAOBJECTID IN (" + getPaoIDs()[0]);
				for (int i = 1; i < getPaoIDs().length; i++)
					sql.append(", " + getPaoIDs()[i]);
				sql.append(") ");
			}
			
			sql.append(" ORDER BY PAO.PAONAME, RPH.POINTID, TIMESTAMP" );
		return sql;
	}
		
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
		allRPHLows = new HashMap();
		allRPHPeaks = new HashMap();		
		
		int rowCount = 0;
		StringBuffer sql = buildSQLStatement();
		CTILogger.info(sql.toString());	
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null )
			{
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()));
				CTILogger.info("START DATE > " + getStartDate() + "  -  STOP DATE <= " + getStopDate());
				rset = pstmt.executeQuery();
				
				int currentPointid = -1;
				Vector tempRPHVector = new Vector();
				while( rset.next())
				{
					int changeID = rset.getInt(1);
					int pointID = rset.getInt(2);
					Timestamp ts = rset.getTimestamp(3);
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTimeInMillis(ts.getTime());
					int quality = rset.getInt(4);
					double value = rset.getDouble(5);
					
					if( pointID != currentPointid)	//enter all
					{
						if( currentPointid != -1)	//not the first time
						{
							loadSummaryData(new Integer(currentPointid), tempRPHVector);
							tempRPHVector = new Vector();  //get ready for the next PointID
						}
					}
					LiteRawPointHistory lrph = new LiteRawPointHistory(changeID, pointID, cal.getTimeInMillis(), quality, value);
					getData().add(lrph);
					tempRPHVector.add(lrph);
					currentPointid = pointID;
				}
				//Add last PointID's summary data
				loadSummaryData(new Integer(currentPointid), tempRPHVector);
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
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
	
	/**
	 * @param currentPointid
	 * @param allRPHPerPointIDVector
	 */
	private void loadSummaryData(Integer pointID, Vector allRPH)
	{
		Collections.sort(allRPH, rphValueComparator);
	
		if( allRPH.size() < 5)
		{
			allRPHLows.put(pointID, allRPH);
			allRPHPeaks.put(pointID, allRPH);
		}	
		else
		{
			allRPHLows.put(pointID, allRPH.subList(0, 5));
			allRPHPeaks.put(pointID, allRPH.subList(allRPH.size()-5, allRPH.size()));
		}
	}
	/**
	 * @param cal
	 * @return
	 */
	private GregorianCalendar getBeginingOfDay(GregorianCalendar cal)
	{
		GregorianCalendar tempCal = new GregorianCalendar();
		tempCal.setTimeInMillis(cal.getTimeInMillis());
		tempCal.set(Calendar.HOUR_OF_DAY, 0);
		tempCal.set(Calendar.MINUTE, 0);
		tempCal.set(Calendar.SECOND, 0);
		tempCal.set(Calendar.MILLISECOND, 0);
		if ( cal.get(Calendar.HOUR_OF_DAY) == 0 &&
			cal.get(Calendar.MINUTE) == 0 &&
			cal.get(Calendar.SECOND) == 0 &&
			cal.get(Calendar.MILLISECOND) == 0)	//This is actually the previous day's LAST possible reading, return yesterday's date!
			tempCal.add(Calendar.DATE, -1);

		return tempCal;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof LiteRawPointHistory)
		{
			LiteRawPointHistory rphData = ((LiteRawPointHistory)o);
			LitePoint lp = PointFuncs.getLitePoint(rphData.getPointID());
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
				{
					if( lp != null)
						return PAOFuncs.getYukonPAOName(lp.getPaobjectID());
					return null;
				}
				case POINT_NAME_COLUMN:
				{
					if( lp != null)
						return lp.getPointName();
					return null;
				}
				case TIME_COLUMN:
				{
					GregorianCalendar tempCal = new GregorianCalendar();
					tempCal.setTimeInMillis(rphData.getTimeStamp());
					return tempCal.getTime();
				}
				case VALUE_COLUMN:
				{
					return new Double(rphData.getValue());
				}
				case START_DATE_COLUMN:
				{
					GregorianCalendar tempCal = new GregorianCalendar();
					tempCal.setTimeInMillis(rphData.getTimeStamp());
					return getBeginingOfDay(tempCal).getTime();
				}
//				case DAILY_HIGH_COLUMN:
//					return rphData.getMaxValue();
//				case DAILY_HIGH_TIME_COLUMN:
//					return rphData.getMaxTime().getTime();
//				case DAILY_AVERAGE_COLUMN:
//					return rphData.getAvgValue();
//				case DAILY_LOW_COLUMN:
//					return rphData.getMinValue();
//				case DAILY_LOW_TIME_COLUMN:
//					return rphData.getMinTime().getTime();
//				case TOTAL_KWH_COLUMN:
//					return rphData.getTotalValue();
				case SUMMARY_TITLE_COLUMN:
					return SUMMARY_TITLE_STRING;
				case PEAKS_TITLE_COLUMN:
					return PEAKS_TITLE_STRING;
				case LOWS_TITLE_COLUMN:
					return LOWS_TITLE_STRING;

				case PEAK_TIMESTAMP_0_COLUMN:
					return getPeakTimestamp(rphData.getPointID(), 4);
				case PEAK_VALUE_0_COLUMN:
					return getPeakValue(rphData.getPointID(), 4);
				case LOW_TIMESTAMP_0_COLUMN:
					return getLowTimestamp(rphData.getPointID(), 0);
				case LOW_VALUE_0_COLUMN:
					return getLowValue(rphData.getPointID(), 0);
				case PEAK_TIMESTAMP_1_COLUMN:
					return getPeakTimestamp(rphData.getPointID(), 3);
				case PEAK_VALUE_1_COLUMN:
					return getPeakValue(rphData.getPointID(), 3);
				case LOW_TIMESTAMP_1_COLUMN:
					return getLowTimestamp(rphData.getPointID(), 1);
				case LOW_VALUE_1_COLUMN:
					return getLowValue(rphData.getPointID(), 1);
				case PEAK_TIMESTAMP_2_COLUMN:
					return getPeakTimestamp(rphData.getPointID(), 2);
				case PEAK_VALUE_2_COLUMN:
					return getPeakValue(rphData.getPointID(), 2);
				case LOW_TIMESTAMP_2_COLUMN:
					return getLowTimestamp(rphData.getPointID(), 2);
				case LOW_VALUE_2_COLUMN:
					return getLowValue(rphData.getPointID(), 2);
				case PEAK_TIMESTAMP_3_COLUMN:
					return getPeakTimestamp(rphData.getPointID(), 1);
				case PEAK_VALUE_3_COLUMN:
					return getPeakValue(rphData.getPointID(), 1);
				case LOW_TIMESTAMP_3_COLUMN:
					return getLowTimestamp(rphData.getPointID(), 3);
				case LOW_VALUE_3_COLUMN:
					return getLowValue(rphData.getPointID(), 3);
				case PEAK_TIMESTAMP_4_COLUMN:
					return getPeakTimestamp(rphData.getPointID(), 0);
				case PEAK_VALUE_4_COLUMN:
					return getPeakValue(rphData.getPointID(), 0);
				case LOW_TIMESTAMP_4_COLUMN:
					return getLowTimestamp(rphData.getPointID(), 4);
				case LOW_VALUE_4_COLUMN:
					return getLowValue(rphData.getPointID(), 4);

				case POINT_TOTAL_KWH_COLUMN:
					return null;	//this value is handled else where (like by JFR expressions)
			}
		}
		return null;
	}

	/**
	 * @param i
	 * @return
	 */
	private Object getPeakTimestamp(int pointID, int i)
	{
		List peaks = ((List)allRPHPeaks.get(new Integer(pointID)));
		if( peaks != null && peaks.size() > i)
		{
			GregorianCalendar tempCal = new GregorianCalendar();
			tempCal.setTimeInMillis(((LiteRawPointHistory)peaks.get(i)).getTimeStamp());
			return tempCal.getTime();
		}
		return null;
	}
	/**
	 * @param i
	 * @return
	 */
	private Object getPeakValue(int pointID, int i)
	{
		List peaks = ((List)allRPHPeaks.get(new Integer(pointID)));
		if( peaks != null && peaks.size() > i)
			return new Double(((LiteRawPointHistory)peaks.get(i)).getValue());
		return null;
	}
	/**
	 * @param i
	 * @return
	 */
	private Object getLowTimestamp(int pointID, int i)
	{
		List lows = ((List)allRPHLows.get(new Integer(pointID)));
		if( lows != null && lows.size() > i)
		{
			GregorianCalendar tempCal = new GregorianCalendar();
			tempCal.setTimeInMillis(((LiteRawPointHistory)lows.get(i)).getTimeStamp());
			return tempCal.getTime();
		}
		return null;
	}
	/**
	 * @param i
	 * @return
	 */
	private Object getLowValue(int pointID, int i)
	{
		List lows = ((List)allRPHLows.get(new Integer(pointID)));
		if( lows != null && lows.size() > i)
			return new Double(((LiteRawPointHistory)lows.get(i)).getValue());
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
				PAO_NAME_STRING,
				POINT_NAME_STRING,
				TIME_STRING,
				VALUE_STRING,
				START_DATE_STRING,

				DAILY_HIGH_TIME_STRING,
				DAILY_HIGH_STRING,				
				DAILY_AVERAGE_STRING,
				DAILY_LOW_TIME_STRING,
				DAILY_LOW_STRING,
				TOTAL_KWH_STRING,
				
				SUMMARY_TITLE_STRING,
				PEAKS_TITLE_STRING,
				LOWS_TITLE_STRING,
				
				PEAK_TIMESTAMP_0_STRING,
				PEAK_VALUE_0_STRING,
				LOW_TIMESTAMP_0_STRING,
				LOW_VALUE_0_STRING,

				PEAK_TIMESTAMP_1_STRING,
				PEAK_VALUE_1_STRING,
				LOW_TIMESTAMP_1_STRING,
				LOW_VALUE_1_STRING,

				PEAK_TIMESTAMP_2_STRING,
				PEAK_VALUE_2_STRING,
				LOW_TIMESTAMP_2_STRING,
				LOW_VALUE_2_STRING,

				PEAK_TIMESTAMP_3_STRING,
				PEAK_VALUE_3_STRING,
				LOW_TIMESTAMP_3_STRING,
				LOW_VALUE_3_STRING,

				PEAK_TIMESTAMP_4_STRING,
				PEAK_VALUE_4_STRING,
				LOW_TIMESTAMP_4_STRING,
				LOW_VALUE_4_STRING,
				
				POINT_TOTAL_KWH_STRING
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
				Date.class,
				Double.class,
				Date.class,
								
				Date.class,
				Double.class,
				Double.class,
				Date.class,
				Double.class,
				Double.class,
				
				String.class,
				String.class,
				String.class,

				Date.class,
				Double.class,
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				Date.class,
				Double.class,
				
				Double.class
			};
		}
		return columnTypes;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
				new ColumnProperties(0, 1, 175, null),
				new ColumnProperties(0, 1, 175, null),
				new ColumnProperties(0, 1, 75, "HH:mm:ss"),	//NA?
				new ColumnProperties(75, 1, 75, "#,###,##0.00"),	//NA?
				
				new ColumnProperties(0, 1, 75, "MM-dd-yyyy"),
				new ColumnProperties(75, 1, 75, "HH:mm:ss"),
				new ColumnProperties(150, 1, 75, "#,###,##0.00"),
				new ColumnProperties(225, 1, 75, "#,###,##0.00"),
				new ColumnProperties(300, 1, 75, "HH:mm:ss"),
				new ColumnProperties(375, 1, 75, "#,###,##0.00"),
				new ColumnProperties(450, 1, 75, "#,###,###,##0.00"),

				new ColumnProperties(0, 1, 550, null),
				new ColumnProperties(50, 1, 225, null),
				new ColumnProperties(325, 1, 225, null),
				
				new ColumnProperties(50, 30, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(150, 30, 100, "#,###,##0.00"),
				new ColumnProperties(325, 30, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(425, 30, 100, "#,###,##0.00"),

				new ColumnProperties(50, 42, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(150, 42, 100, "#,###,##0.00"),
				new ColumnProperties(325, 42, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(425, 42, 100, "#,###,##0.00"),
				
				new ColumnProperties(50, 54, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(150, 54, 100, "#,###,##0.00"),
				new ColumnProperties(325, 54, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(425, 54, 100, "#,###,##0.00"),

				new ColumnProperties(50, 66, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(150, 66, 100, "#,###,##0.00"),
				new ColumnProperties(325, 66, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(425, 66, 100, "#,###,##0.00"),

				new ColumnProperties(50, 78, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(150, 78, 100, "0.00"),
				new ColumnProperties(325, 78, 100, "MM-dd-yyyy HH:mm:ss"),
				new ColumnProperties(425, 78, 100, "#,###,##0.00"),
				new ColumnProperties(450, 1, 100, "#,###,###,##0.0000")
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
	public int getPointType()
	{
		return pointType;
	}

	/**
	 * valid pointTYpes are listed in this classs, above
	 * @param i
	 */
	public void setPointType(int i)
	{
		pointType = i;
	}
	
	public String getPointTypeString(int pointTypeID)
	{
		switch (pointTypeID)
		{
			case LOAD_PROFILE_POINT_TYPE:
				return LOAD_PROFILE_POINT_TYPE_STRING;
			case CALC_POINT_TYPE:
				return CALC_POINT_TYPE_STRING;
			case ANALOG_POINT_TYPE:
				return ANALOG_POINT_TYPE_STRING;
			case DEMAND_ACC_POINT_TYPE:
				return DEMAND_ACC_POINT_TYPE_STRING;
			case STATUS_POINT_TYPE:
				return STATUS_POINT_TYPE_STRING;
		}
		return "UNKNOWN";
	}
	/**
	 * @return
	 */
	public static int[] getAllPointTypes()
	{
		return ALL_POINT_TYPES;
	}

	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td align='center'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>Point Type</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < getAllPointTypes().length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='" + ATT_POINT_TYPE +"' value='" + getAllPointTypes()[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getPointTypeString(getAllPointTypes()[i])+ LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}

	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
			String param = req.getParameter(ATT_POINT_TYPE);
			if( param != null)
				setPointType(Integer.valueOf(param).intValue());
			else
				setPointType(LOAD_PROFILE_POINT_TYPE);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#useBillingGroup()
	 */
	public boolean useBillingGroup()
	{
		return true;
	}
}

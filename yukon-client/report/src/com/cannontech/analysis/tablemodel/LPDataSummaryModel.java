package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.LPMeterData;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.model.ModelFactory;

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
public class LPDataSummaryModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 35;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
	public final static int METER_NUMBER_COLUMN = 1;
	public final static int PHYSICAL_ADDRESS_COLUMN = 2;
	public final static int PAO_TYPE_COLUMN = 3;	
	//The above enums make up the group by */
	
	public final static int POINT_NAME_COLUMN = 4;
	public final static int CHANNEL_NUMBER_COLUMN = 5;
	public final static int CHANNEL_INTERVAL_COLUMN = 6;
	public final static int MISSING_DATA_FLAG_COLUMN = 7;	
	
	public final static int PEAKS_TITLE_COLUMN = 8;
	public final static int LOWS_TITLE_COLUMN = 9;

//	These are columns that could be eliminated with the use of subreporting!!!
	public final static int PEAK_TIMESTAMP_0_COLUMN = 10;
	public final static int PEAK_VALUE_0_COLUMN = 11;
	public final static int LOW_TIMESTAMP_0_COLUMN = 12;
	public final static int LOW_VALUE_0_COLUMN = 13;

	public final static int PEAK_TIMESTAMP_1_COLUMN = 14;
	public final static int PEAK_VALUE_1_COLUMN = 15;
	public final static int LOW_TIMESTAMP_1_COLUMN = 16;
	public final static int LOW_VALUE_1_COLUMN = 17;

	public final static int PEAK_TIMESTAMP_2_COLUMN = 18;
	public final static int PEAK_VALUE_2_COLUMN = 19;
	public final static int LOW_TIMESTAMP_2_COLUMN = 20;
	public final static int LOW_VALUE_2_COLUMN = 21;

	public final static int PEAK_TIMESTAMP_3_COLUMN = 22;
	public final static int PEAK_VALUE_3_COLUMN = 23;
	public final static int LOW_TIMESTAMP_3_COLUMN = 24;
	public final static int LOW_VALUE_3_COLUMN = 25;

	public final static int PEAK_TIMESTAMP_4_COLUMN = 26;
	public final static int PEAK_VALUE_4_COLUMN = 27;
	public final static int LOW_TIMESTAMP_4_COLUMN = 28;
	public final static int LOW_VALUE_4_COLUMN = 29;

// end of subreport columns

	
	/** String values for column representation */
	public final static String PAO_NAME_STRING = "Device Name";
	public final static String PAO_TYPE_STRING = "Type";
	public final static String METER_NUMBER_STRING = "Meter #";
	public final static String PHYSICAL_ADDRESS_STRING = "Address";
	//The above enums make up the group by */
	
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String CHANNEL_NUMBER_STRING = "Channel";
	public final static String CHANNEL_INTERVAL_STRING = "Interval";
	public final static String MISSING_DATA_FLAG_STRING = "Missing Data";	

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


	//contain values of Integer (pointID ) to MeterAndPointData object vlaues
	private HashMap allMPDataPeaks = new HashMap();
	private HashMap allMPDataLows = new HashMap();
	
	/** A string for the title of the data */
	private static String title = "Load Profile Data Summary Report";

	//servlet attributes/parameter strings
	
	public static Comparator mpDataValueComparator = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			double thisVal = ((MeterAndPointData)o1).getValue().doubleValue();
			double anotherVal = ((MeterAndPointData)o2).getValue().doubleValue();
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
	public LPDataSummaryModel()
	{
		super();
		setFilterModelTypes(new int[]{ 
    			ModelFactory.COLLECTIONGROUP, 
    			ModelFactory.TESTCOLLECTIONGROUP, 
    			ModelFactory.BILLING_GROUP}
				);
	}
	/**
	 * Constructor class
	 * @param startTime_ rph.timestamp
	 * @param stopTime_ rph.timestamp
	 */
	public LPDataSummaryModel(Date start_, Date stop_)
	{
		super(start_, stop_);
	}
	/**
	 * Add CarrierData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
	    //NOT IMPLEMENTED, data rows are added in the collectData method 
	}

	/**
	 * Build the SQL statement to retrieve DatabaseModel data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT PAO.PAOBJECTID, RPH.POINTID, TIMESTAMP, VALUE " +
			", DLP.LOADPROFILEDEMANDRATE, DLP.VOLTAGEDMDRATE, P.POINTOFFSET " +
			" FROM RAWPOINTHISTORY RPH, POINT P, YUKONPAOBJECT PAO, DEVICELOADPROFILE DLP " +
			" WHERE P.POINTID = RPH.POINTID " +
			" AND P.PAOBJECTID = PAO.PAOBJECTID " +
			" AND PAO.PAOBJECTID = DLP.DEVICEID " +
			" AND TIMESTAMP > ? AND TIMESTAMP <= ? " +
			" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.DEMAND_ACCUMULATOR_POINT) + "' " + 
			" AND (P.POINTOFFSET >= " + PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND + 
			" AND P.POINTOFFSET <= " + PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND + ") ");
		
			//Use paoIDs in query if they exist
			if( getPaoIDs() != null && getPaoIDs().length > 0)
			{
				sql.append(" AND P.PAOBJECTID IN (" + getPaoIDs()[0]);
				for (int i = 1; i < getPaoIDs().length; i++)
					sql.append(", " + getPaoIDs()[i]);
				sql.append(") ");
			}
			
			sql.append(" ORDER BY PAO.PAONAME, P.POINTOFFSET, TIMESTAMP" );
		return sql;
	}
		
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
		allMPDataLows = new HashMap();
		allMPDataPeaks = new HashMap();		
		
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
				Vector tempMPDataVector = new Vector();	//Vector of MeterAndPointData
				while( rset.next())
				{
					Integer paobjectID = new Integer(rset.getInt(1));
					Integer pointID = new Integer(rset.getInt(2));
					Timestamp ts = rset.getTimestamp(3);
					Double value = new Double(rset.getDouble(4));
					String lpDemandRate = String.valueOf(rset.getInt(5));
					String voltageDemandRate = String.valueOf(rset.getInt(6));
					if( pointID.intValue() != currentPointid)	//enter all
					{
						if( currentPointid != -1)	//not the first time
						{
							loadSummaryData(new Integer(currentPointid), tempMPDataVector);
							tempMPDataVector = new Vector();  //get ready for the next PointID
						}
					}
					MeterAndPointData mpData = new MeterAndPointData(paobjectID, pointID, new Date(ts.getTime()), value);
					LPMeterData lpMeterData = new LPMeterData(mpData, null, null, lpDemandRate, voltageDemandRate);
					getData().add(lpMeterData);
					tempMPDataVector.add(mpData);
					currentPointid = pointID.intValue();
				}
				//Add last PointID's summary data
				loadSummaryData(new Integer(currentPointid), tempMPDataVector);
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
	 * @param pointid
	 * @param  allMPData - Vector of MeterAndPointData values
	 */
	private void loadSummaryData(Integer pointID, Vector allMPData)
	{
		Collections.sort(allMPData, mpDataValueComparator);
	
		if( allMPData.size() < 5)
		{
			allMPDataLows.put(pointID, allMPData);
			allMPDataPeaks.put(pointID, allMPData);
		}	
		else
		{
			allMPDataLows.put(pointID, allMPData.subList(0, 5));
			allMPDataPeaks.put(pointID, allMPData.subList(allMPData.size()-5, allMPData.size()));
		}
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof LPMeterData)
		{
		    LPMeterData lpMeterData = ((LPMeterData)o);
//			LiteRawPointHistory rphData = ((LiteRawPointHistory)o);
//			LitePoint lp = PointFuncs.getLitePoint(rphData.getPointID());
			switch( columnIndex)
			{			
				case PAO_NAME_COLUMN:
					return PAOFuncs.getYukonPAOName(lpMeterData.getMeterAndPointData().getPaobjectID().intValue());
				case PAO_TYPE_COLUMN:
				    return PAOGroups.getPAOTypeString(lpMeterData.getMeterAndPointData().getLitePaobject().getType());
				case METER_NUMBER_COLUMN:
				    return lpMeterData.getMeterAndPointData().getLiteDeviceMeterNumber().getMeterNumber();
				case PHYSICAL_ADDRESS_COLUMN:
				    return String.valueOf(lpMeterData.getMeterAndPointData().getLitePaobject().getAddress());
				case POINT_NAME_COLUMN:
				    return PointFuncs.getPointName(lpMeterData.getMeterAndPointData().getPointID().intValue());				    
				case CHANNEL_NUMBER_COLUMN:
				{
				    LitePoint lp = PointFuncs.getLitePoint(lpMeterData.getMeterAndPointData().getPointID().intValue());
				    if( lp != null)
				    {
				        if (lp.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND)
				            return "1";
				        else if (lp.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND)
				            return "4";
				    }
				    return "?";
				}
				case CHANNEL_INTERVAL_COLUMN:
				{
				    LitePoint lp = PointFuncs.getLitePoint(lpMeterData.getMeterAndPointData().getPointID().intValue());
				    if( lp != null)
				    {
				        if (lp.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND)
				            return lpMeterData.getDemandRate();
				        else if (lp.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND)
				            return lpMeterData.getVoltageDemandRate();
				    }
				    return "?";
				}
				case MISSING_DATA_FLAG_COLUMN:
				    return NULL_STRING;
				        
				case PEAKS_TITLE_COLUMN:
					return PEAKS_TITLE_STRING;
				case LOWS_TITLE_COLUMN:
					return LOWS_TITLE_STRING;

				case PEAK_TIMESTAMP_0_COLUMN:
					return getPeakTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 4);
				case PEAK_VALUE_0_COLUMN:
					return getPeakValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 4);
				case LOW_TIMESTAMP_0_COLUMN:
					return getLowTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 0);
				case LOW_VALUE_0_COLUMN:
					return getLowValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 0);
				case PEAK_TIMESTAMP_1_COLUMN:
					return getPeakTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 3);
				case PEAK_VALUE_1_COLUMN:
					return getPeakValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 3);
				case LOW_TIMESTAMP_1_COLUMN:
					return getLowTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 1);
				case LOW_VALUE_1_COLUMN:
					return getLowValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 1);
				case PEAK_TIMESTAMP_2_COLUMN:
					return getPeakTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 2);
				case PEAK_VALUE_2_COLUMN:
					return getPeakValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 2);
				case LOW_TIMESTAMP_2_COLUMN:
					return getLowTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 2);
				case LOW_VALUE_2_COLUMN:
					return getLowValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 2);
				case PEAK_TIMESTAMP_3_COLUMN:
					return getPeakTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 1);
				case PEAK_VALUE_3_COLUMN:
					return getPeakValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 1);
				case LOW_TIMESTAMP_3_COLUMN:
					return getLowTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 3);
				case LOW_VALUE_3_COLUMN:
					return getLowValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 3);
				case PEAK_TIMESTAMP_4_COLUMN:
					return getPeakTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 0);
				case PEAK_VALUE_4_COLUMN:
					return getPeakValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 0);
				case LOW_TIMESTAMP_4_COLUMN:
					return getLowTimestamp(lpMeterData.getMeterAndPointData().getPointID().intValue(), 4);
				case LOW_VALUE_4_COLUMN:
					return getLowValue(lpMeterData.getMeterAndPointData().getPointID().intValue(), 4);
			}
		}
		return null;
	}

	/**
	 * @param i
	 * @return
	 */
	private Date getPeakTimestamp(int pointID, int i)
	{
		List peaks = ((List)allMPDataPeaks.get(new Integer(pointID)));
		if( peaks != null && peaks.size() > i)
		{
			return ((MeterAndPointData)peaks.get(i)).getTimeStamp();
		}
		return null;
	}
	/**
	 * @param i
	 * @return
	 */
	private Double getPeakValue(int pointID, int i)
	{
		List peaks = ((List)allMPDataPeaks.get(new Integer(pointID)));
		if( peaks != null && peaks.size() > i)
			return ((MeterAndPointData)peaks.get(i)).getValue();
		return null;
	}
	/**
	 * @param i
	 * @return
	 */
	private Date getLowTimestamp(int pointID, int i)
	{
		List lows = ((List)allMPDataLows.get(new Integer(pointID)));
		if( lows != null && lows.size() > i)
		{
		    return ((MeterAndPointData)lows.get(i)).getTimeStamp();
		}
		return null;
	}
	/**
	 * @param i
	 * @return
	 */
	private Double getLowValue(int pointID, int i)
	{
		List lows = ((List)allMPDataLows.get(new Integer(pointID)));
		if( lows != null && lows.size() > i)
			return ((MeterAndPointData)lows.get(i)).getValue();
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
				METER_NUMBER_STRING,
				PHYSICAL_ADDRESS_STRING,
				PAO_TYPE_STRING,
				
				POINT_NAME_STRING,
				CHANNEL_NUMBER_STRING,
				CHANNEL_INTERVAL_STRING,				
				MISSING_DATA_FLAG_STRING,
								
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
				LOW_VALUE_4_STRING
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
				String.class,
				String.class,
								
				String.class,
				String.class,
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
				new ColumnProperties(175, 1, 100, null),
				new ColumnProperties(275, 1, 100, null),	//NA?
				new ColumnProperties(375, 1, 100, null),	//NA?
				
				new ColumnProperties(0, 1, 175, null),	//NA?
				new ColumnProperties(175, 1, 100, null),
				new ColumnProperties(275, 1, 100, null),
				new ColumnProperties(375, 1, 100, null),

				new ColumnProperties(50, 1, 225, null),
				new ColumnProperties(275, 1, 225, null),
				
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
				new ColumnProperties(425, 78, 100, "#,###,##0.00")
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

	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
//		html += "    <td align='center'>" + LINE_SEPARATOR;
//		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
//		html += "        <tr>" + LINE_SEPARATOR;
//		html += "          <td valign='top' class='TitleHeader'>Point Type</td>" +LINE_SEPARATOR;
//		html += "        </tr>" + LINE_SEPARATOR;
//		for (int i = 0; i < getAllPointTypes().length; i++)
//		{
//			html += "        <tr>" + LINE_SEPARATOR;
//			html += "          <td><input type='radio' name='" + ATT_POINT_TYPE +"' value='" + getAllPointTypes()[i] + "' " +  
//			 (i==0? "checked" : "") + ">" + getPointTypeString(getAllPointTypes()[i])+ LINE_SEPARATOR;
//			html += "          </td>" + LINE_SEPARATOR;
//			html += "        </tr>" + LINE_SEPARATOR;
//		}
//		html += "      </table>" + LINE_SEPARATOR;
//		html += "    </td>" + LINE_SEPARATOR;
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}

	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
//			String param = req.getParameter(ATT_POINT_TYPE);
//			if( param != null)
//				setPointType(Integer.valueOf(param).intValue());
//			else
//				setPointType(LOAD_PROFILE_POINT_TYPE);
		}
	}
}

package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.DeviceFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;

/**
 * Created on Feb 18, 2004
 * DisconnectReportDatabase TableModel object
 * Extending classes must implement:
 *   addDataRow(ResultSet)	- add a "row" object to the data Vector
 *   buildSQLStatement()	- Returns the sql query statment
 * 
 * Contains the start and stop times for query information.
 * Contains the devicemetergroup - YukonPaobject.paoname
 * 				deviceName - YukonPaobject.paoname
 * 				pointName - Point.pointname
 * @author bjonasson
 */
public class DisconnectModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 8;

	/** Enum values for column representation */
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int METER_NUMBER_COLUMN = 1;
	public final static int ADDRESS_COLUMN = 2;
	public final static int TYPE_COLUMN = 3;
	public final static int ROUTE_NAME_COLUMN = 4;
	public final static int COLL_GROUP_NAME_COLUMN = 5;
//	public final static int ALT_GROUP_NAME_COLUMN = 6;
//	public final static int BILL_GROUP_NAME_COLUMN = 7;
	public final static int TIMESTAMP_COLUMN = 6;
	public final static int STATE_COLUMN = 7;
	
	/** String values for column representation */
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String METER_NUMBER_STRING = "Meter #";
	public final static String ADDRESS_STRING = "Address";
	public final static String TYPE_STRING = "Type";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String COLL_GROUP_NAME_STRING = "Coll Grp";	
//	public final static String ALT_GROUP_NAME_STRING = "Alt Grp";
//	public final static String BILL_GROUP_NAME_STRING = "Bill Grp";
	public final static String TIMESTAMP_STRING = "Timestamp";
	public final static String STATE_STRING = "State";
	
	/** A string for the title of the data */ //DEFAULT
	private static String title = "Disconnect State";
	
	/** Rawpointhistory.value critera, null results in current disconnect state? */
	/** valid types are:  Disconnected | Connected | Intermediate | Invalid */
	public static String DISCONNECTED_STRING = "Disconnected";
	public static String CONNECTED_STRING = "Connected";
	public static String INTERMEDIATE_STRING = "Intermediate";
	public static String INVALID_STRING = "Invalid";
	
	public final static int DISCONNECTED_STATE = 0;
	public  final static int CONNECTED_STATE = 1;
	public final static int ALL_STATES = 2;
	private int disconnectState = ALL_STATES;

	public static final int ORDER_BY_DEVICE_NAME = 0;
	public static final int ORDER_BY_ROUTE_NAME = 1;
	public static final int ORDER_BY_METER_NUMBER = 2;
	public static final int ORDER_BY_COLL_GRP = 3;
	public static final int ORDER_BY_TIMESTAMP = 4;
	public static final int ORDER_BY_STATE = 5;
	private int orderBy = ORDER_BY_DEVICE_NAME;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_DEVICE_NAME, ORDER_BY_ROUTE_NAME, ORDER_BY_METER_NUMBER,
		ORDER_BY_COLL_GRP, ORDER_BY_TIMESTAMP, ORDER_BY_STATE
	};

	//flag for displaying the history of disconnect meters
	private boolean showHistory = false;
	
	private static final String ATT_DISCONNECT_STATE = "disconnectStat";
	private static final String ATT_SHOW_HISTORY = "history";
	private static final String ATT_ORDER_BY = "orderBy";

	public Comparator discComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2){
		    LiteYukonPAObject pao1 = PAOFuncs.getLiteYukonPAO( ((MeterAndPointData)o1).getPaobjectID().intValue());
		    LiteYukonPAObject pao2 = PAOFuncs.getLiteYukonPAO( ((MeterAndPointData)o2).getPaobjectID().intValue());
		    LiteDeviceMeterNumber ldmn1 = DeviceFuncs.getLiteDeviceMeterNumber( ((MeterAndPointData)o1).getPaobjectID().intValue());
		    LiteDeviceMeterNumber ldmn2 = DeviceFuncs.getLiteDeviceMeterNumber( ((MeterAndPointData)o2).getPaobjectID().intValue());
		    
		    String thisStrVal = pao1.getPaoName();
			String anotherStrVal = pao2.getPaoName();

		    if( getOrderBy() == ORDER_BY_ROUTE_NAME)
		    {
		        thisStrVal = PAOFuncs.getYukonPAOName(pao1.getRouteID());
				anotherStrVal = PAOFuncs.getYukonPAOName(pao2.getRouteID());
		    }
		    else if( getOrderBy() == ORDER_BY_METER_NUMBER)
		    {
		        thisStrVal = ldmn1.getMeterNumber();
				anotherStrVal = ldmn2.getMeterNumber();
		    }
		    else if( getOrderBy() == ORDER_BY_COLL_GRP)
		    {	//by collgroup then by paoName
		        thisStrVal = ldmn1.getCollGroup();
				anotherStrVal = ldmn2.getCollGroup();
				if( thisStrVal.equalsIgnoreCase(anotherStrVal))
				{
				    thisStrVal = pao1.getPaoName();
				    anotherStrVal = pao2.getPaoName();
				}
		    }
		    

		    else if( getOrderBy() == ORDER_BY_TIMESTAMP)
		    {
		        long thisLongVal = ((MeterAndPointData)o1).getTimeStamp().getTime();
				long anotherLongVal = ((MeterAndPointData)o2).getTimeStamp().getTime();
				return ( thisLongVal < anotherLongVal ? -1 : (thisLongVal ==anotherLongVal ? 0 : 1));				
		    }
		    if ( getOrderBy() == ORDER_BY_STATE)
		    {
		        double thisDoubleVal = ((MeterAndPointData)o1).getValue().doubleValue();
		        double anotherDoubleVal = ((MeterAndPointData)o2).getValue().doubleValue();
		        return ( thisDoubleVal <anotherDoubleVal ? -1 : (thisDoubleVal ==anotherDoubleVal ? 0 : 1));
		    }
			return ( thisStrVal.compareToIgnoreCase(anotherStrVal));
		}
		public boolean equals(Object obj){
			return false;
		}
	};



	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	
	public DisconnectModel()
	{
		this(false);		
	}

	/**
	 * When ShowHist is true, then disconnect/connected does NOT matter.
	 * They only matter when !showHist, or rather when showing only CURRENT  
	 * @param showHist
	 */
	public DisconnectModel(boolean showHist)
	{
		super();
		setShowHistory(showHist);
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
	 */
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
		
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
				if( isShowHistory())
				{
					pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ));
					pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime() ));
					CTILogger.info("START DATE > " + getStartDate());
					CTILogger.info("STOP DATE <= " + getStopDate());
				}
				rset = pstmt.executeQuery();
				
				while( rset.next())
				{
					addDataRow(rset);
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
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT DISTINCT PAO.PAOBJECTID, " +
				" P.POINTID, RPH1.TIMESTAMP, RPH1.VALUE " +
				" FROM YUKONPAOBJECT PAO, POINT P, RAWPOINTHISTORY RPH1 " +
				" WHERE PAO.PAOBJECTID = P.PAOBJECTID " +
				" AND PAO.PAOCLASS = '" + DeviceClasses.STRING_CLASS_CARRIER + "' "+
				" AND P.POINTID = RPH1.POINTID " +
				" AND P.POINTOFFSET = 1 " +
				" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.STATUS_POINT) + "' ");

		if( isShowHistory())
		{
			sql.append(" AND RPH1.TIMESTAMP > ? AND RPH1.TIMESTAMP <= ? " );
		}
		else		
		{
			if( getDisconnectState() != ALL_STATES)	//limit the RPH value accepted
			{
				if (getDisconnectState() == CONNECTED_STATE)
				{
					sql.append(" AND RPH1.VALUE = 1.0 ");
				}
				else if (getDisconnectState() == DISCONNECTED_STATE)
				{
					sql.append(" AND RPH1.VALUE = 0.0 ");
				}
			}
			sql.append(" AND RPH1.TIMESTAMP = ( SELECT MAX(RPH2.TIMESTAMP) FROM RAWPOINTHISTORY RPH2 " + 
							" WHERE RPH1.POINTID = RPH2.POINTID) " );
		}
		return sql;
	}

	/**
	 * Add <innerClass> objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
		    Integer paobjectID = new Integer(rset.getInt(1));
			Integer pointID = new Integer(rset.getInt(2));
			Timestamp timestamp = rset.getTimestamp(3);
			double value = rset.getDouble(4);
			
			MeterAndPointData disconnect = new MeterAndPointData(paobjectID, pointID, new Date(timestamp.getTime()), new Double(value));
			getData().add(disconnect);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	private String getRPHValueString(double value)
	{
		if(value == 0)
			return DISCONNECTED_STRING;
		else if(value == 1)
			return CONNECTED_STRING;	
		else if(value == 2)
			return INTERMEDIATE_STRING;	
		else //if (value == -1)
			return INVALID_STRING;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	 public String getDateRangeString()
	 {
	 	if( isShowHistory())
	 	{
			return (getDateFormat().format(getStartDate()) + " through " +
								(getDateFormat().format(getStopDate())));
	 	}
	 	else 
	 	{
			return ( getDateFormat().format(new Date()));	//use current date
	 	}
		 
	 }


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof MeterAndPointData)
		{
			MeterAndPointData disc = ((MeterAndPointData)o);
			LiteYukonPAObject lPao = PAOFuncs.getLiteYukonPAO(disc.getPaobjectID().intValue());
			LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber(disc.getPaobjectID().intValue());
			switch( columnIndex)
			{
				case DEVICE_NAME_COLUMN:
					return lPao.getPaoName();
	
				case TIMESTAMP_COLUMN:
					return disc.getTimeStamp();

				case METER_NUMBER_COLUMN:
				    return ldmn.getMeterNumber();
				    
				case ADDRESS_COLUMN:
				    return String.valueOf(lPao.getAddress());
				    
				case TYPE_COLUMN:
				    return PAOGroups.getPAOTypeString(lPao.getType());
				
				case STATE_COLUMN:
				{
				    if( disc.getValue() != null)
				        return getRPHValueString(disc.getValue().doubleValue());
				    else
				        return "UNKNOWN";
				}
				case ROUTE_NAME_COLUMN:
				    return PAOFuncs.getYukonPAOName(lPao.getRouteID());

				case COLL_GROUP_NAME_COLUMN:
					return ldmn.getCollGroup();

/*			 	case ALT_GROUP_NAME_COLUMN:
					return ldmn.getTestCollGroup();

				case BILL_GROUP_NAME_COLUMN:
					return ldmn.getBillGroup();
*/
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
			    DEVICE_NAME_STRING,
			    METER_NUMBER_STRING,
			    ADDRESS_STRING,
			    TYPE_STRING,
			    ROUTE_NAME_STRING,
			    COLL_GROUP_NAME_STRING,
//			    ALT_GROUP_NAME_STRING,
//			    BILL_GROUP_NAME_STRING,
			    TIMESTAMP_STRING,
			    STATE_STRING
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
//				String.class,
//				String.class,
				java.util.Date.class,				
				String.class
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
		    int offset = 0;
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(offset, 1, offset+=150, null),	//Device Name
				new ColumnProperties(offset, 1, offset+=70, null), //meternumber
				new ColumnProperties(offset, 1, offset+=70, null), //address
				new ColumnProperties(offset, 1, offset+=70, null), //type
				new ColumnProperties(offset, 1, offset+=100, null), //routeName
				new ColumnProperties(offset, 1, offset+=80, null), //collGroup
//				new ColumnProperties(offset, 1, offset+=80, null), //altGroup
//				new ColumnProperties(offset, 1, offset+=80, null), //billGroup
				new ColumnProperties(offset, 1, offset+=105, "MM/dd/yyyy HH:mm:ss"),   //Timestamp
				new ColumnProperties(offset, 1, offset+=65, null) //state
			};
		}
		return columnProperties;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		if( getDisconnectState() == ALL_STATES)
			title = "All Disconnect";
		else if (getDisconnectState() == CONNECTED_STATE)
			title = "Connected";
		else if (getDisconnectState() == DISCONNECTED_STATE)
			title = "Disconnected";

		if( isShowHistory())
			title += " - Meter History Report";
		else
			title += " - Current State Report";
			
		return title;
	}
	/**
	 * @return
	 */
	public boolean isShowHistory()
	{
		return showHistory;
	}

	/**
	 * @param b
	 */
	public void setShowHistory(boolean b)
	{
		showHistory = b;
	}
	
	/**
	 * @return
	 */
	public int getOrderBy()
	{
		return orderBy;
	}

	/**
	 * @param i
	 */
	public void setOrderBy(int i)
	{
		orderBy = i;
	}
	public String getOrderByString(int orderBy)
	{
		switch (orderBy)
		{
			case ORDER_BY_DEVICE_NAME:
				return "Device Name";
			case ORDER_BY_METER_NUMBER:
				return "Meter Number";
			case ORDER_BY_ROUTE_NAME:
			    return "Route Name";
			case ORDER_BY_COLL_GRP:
			    return "Collection Group";
			case ORDER_BY_TIMESTAMP:
			    return "Timestamp";
			case ORDER_BY_STATE:
			    return "Disconnect State";
/*			case ORDER_BY_ALT_GRP:
			    return "Alternate Group";
			case ORDER_BY_BILL_GRP:
			    return "Billing Group";
*/			    
		}
		return "UNKNOWN";
	}
	public static int[] getAllOrderBys()
	{
		return ALL_ORDER_BYS;
	}	
	
	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<script>" + LINE_SEPARATOR;
		html += "function enableCheckBox(value){" + LINE_SEPARATOR;
		html += "  if( value) {" + LINE_SEPARATOR;
		html += "    document.reportForm." + ATT_DISCONNECT_STATE + "[0].checked = !value;" + LINE_SEPARATOR;
		html += "    document.reportForm." + ATT_DISCONNECT_STATE + "[1].checked = !value;" + LINE_SEPARATOR;		
		html += "    document.reportForm." + ATT_DISCONNECT_STATE + "[2].checked = !value;" + LINE_SEPARATOR;
		html += "  } else {" + LINE_SEPARATOR;
		html += "    document.reportForm." + ATT_DISCONNECT_STATE + "[2].checked = !value;" + LINE_SEPARATOR; 		
		html += "  } " + LINE_SEPARATOR;
		html += "  document.reportForm." + ATT_DISCONNECT_STATE + "[0].disabled = value;" + LINE_SEPARATOR;
		html += "  document.reportForm." + ATT_DISCONNECT_STATE + "[1].disabled = value;" + LINE_SEPARATOR;
		html += "  document.reportForm." + ATT_DISCONNECT_STATE + "[2].disabled = value;" + LINE_SEPARATOR;
		html += "}" + LINE_SEPARATOR;
		
		//Run this method on load, it's NOT in a function!
		html += "enableDates(false);" + LINE_SEPARATOR;
		html += "</script>" + LINE_SEPARATOR;
		
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>Data Display</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' name='" + ATT_SHOW_HISTORY +"' value='history' onclick='enableDates(this.checked);enableCheckBox(this.checked)'>Historical" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Disconnect State</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='radio' name='" +ATT_DISCONNECT_STATE+ "' value='"+CONNECTED_STATE+"'>Show All Connected</td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;		
		html += "          <td><input type='radio' name='" +ATT_DISCONNECT_STATE+ "' value='"+DISCONNECTED_STATE+"'>Show All Disconnected</td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;		
		html += "          <td><input type='radio' name='" +ATT_DISCONNECT_STATE + "' value='"+ALL_STATES+"' checked>Show All</td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Order By</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < getAllOrderBys().length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='"+ATT_ORDER_BY+"' value='" + getAllOrderBys()[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getOrderByString(getAllOrderBys()[i])+ LINE_SEPARATOR;
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
			String param = req.getParameter(ATT_DISCONNECT_STATE);
			if (param != null)
			    setDisconnectState(Integer.valueOf(param).intValue());			
						
			param = req.getParameter(ATT_SHOW_HISTORY);
			setShowHistory(param != null);	//opposite boolean value, since wording for option is "backwards"
			
			param = req.getParameter(ATT_ORDER_BY);
			if( param != null)
				setOrderBy(Integer.valueOf(param).intValue());
			else
				setOrderBy(ORDER_BY_DEVICE_NAME);			
		}
	}
    /**
     * @return Returns the disconnectState.
     */
    public int getDisconnectState()
    {
        return disconnectState;
    }
    /**
     * @param The disconnectState to set.
     */
    public void setDisconnectState(int disconnectState)
    {
        this.disconnectState = disconnectState;
    }
}

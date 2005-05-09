package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.Disconnect;
import com.cannontech.analysis.data.device.MeterData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
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
	protected final int NUMBER_COLUMNS = 5;

	/** Enum values for column representation */
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int POINT_NAME_COLUMN = 1;
	public final static int TIMESTAMP_COLUMN = 2;
	public final static int METER_NUMBER_COLUMN = 3;
	public final static int ADDRESS_COLUMN = 4;
	public final static int TYPE_COLUMN = 5;
	public final static int STATUS_COLUMN = 6;
	public final static int COLL_GROUP_NAME_COLUMN = 7;
//	public final static int ALT_GROUP_NAME_COLUMN = 8;
//	public final static int BILL_GROUP_NAME_COLUMN = 9;

	/** String values for column representation */
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String TIMESTAMP_STRING = "Timestamp";
	public final static String METER_NUMBER_STRING = "Meter Number";
	public final static String ADDRESS_STRING = "Address";
	public final static String TYPE_STRING = "Type";
	public final static String STATUS_STRING = "Status";
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";	
//	public final static String ALT_GROUP_NAME_STRING = "Alternate Group";
//	public final static String BILL_GROUP_NAME_STRING = "Billing Group";

	/** A string for the title of the data */ //DEFAULT
	private static String title = "Disconnect Status";
	
	/** Rawpointhistory.value critera, null results in current disconnect state? */
	/** valid types are:  Disconnected | Connected | Intermediate | Invalid */
	public static String DISCONNECTED_STRING = "Disconnected";
	public static String CONNECTED_STRING = "Connected";
	public static String INTERMEDIATE_STRING = "Intermediate";
	public static String INVALID_STRING = "Invalid";
	 
	//flag for displaying all connected disconnect meters
	private boolean showConnected = true;
	//flag for displaying all disconnected disconnect meters
	private boolean showDisconnected = true;
	//flag for displaying the history of disconnect meters
	private boolean showHistory = false;
	
	private static final String ATT_SHOW_CONNECTED = "connected";
	private static final String ATT_SHOW_DISCONNECTED = "disconnected";
	private static final String ATT_SHOW_HISTORY = "history";


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
		StringBuffer sql = new StringBuffer("SELECT DISTINCT DMG.COLLECTIONGROUP, DMG.TESTCOLLECTIONGROUP, DMG.BILLINGGROUP, PAO.PAOName DeviceName, PAO2.PaoName RouteName, " +
				" P.POINTNAME, DMG.METERNUMBER, DCS.ADDRESS, PAO.TYPE, RPH1.TIMESTAMP, RPH1.VALUE " +
				" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, POINT P, RAWPOINTHISTORY RPH1, " +
				" DEVICECARRIERSETTINGS DCS, YUKONPAOBJECT PAO2, DEVICEROUTES DR " +
				" WHERE PAO.PAOBJECTID = DMG.DEVICEID " +
				" AND PAO.PAOBJECTID = DCS.DEVICEID " +
				" AND PAO.PAOBJECTID = P.PAOBJECTID " +
				" AND PAO.PAOBJECTID = DR.DEVICEID " + 
				" AND PAO2.PAOBJECTID = DR.ROUTEID " +
				" AND P.POINTID = RPH1.POINTID " +
				" AND P.POINTOFFSET = 1 " +
				" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.STATUS_POINT) + "' ");

		if( isShowHistory())
		{
			sql.append(" AND RPH1.TIMESTAMP > ? AND RPH1.TIMESTAMP <= ? ORDER BY PAO.PAONAME" );
		}
		else		
		{
			if( !isShowAll())	//limit the RPH value accepted
			{
				if (isShowConnected())
				{
					sql.append(" AND RPH1.VALUE = 1.0 ");
				}
				else if (isShowDisconnected())
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
			String collGrp = rset.getString(1);
			String altGrp = rset.getString(2);
			String billGrp = rset.getString(3);
			String paoName = rset.getString(4);
			String routeName = rset.getString(5);
			String pointName = rset.getString(6);
			String meterNum = rset.getString(7);
			String address = rset.getString(8);
			String type = rset.getString(9);
			Timestamp timestamp = rset.getTimestamp(10);
			double value = rset.getDouble(11);
			
			MeterData md = new MeterData(collGrp, altGrp, billGrp, paoName, meterNum, address, pointName, routeName);
			Disconnect disconnect = new Disconnect(md, type, new Date(timestamp.getTime()), new Double(value));
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
		if ( o instanceof Disconnect)
		{
			Disconnect disc = ((Disconnect)o); 
			switch( columnIndex)
			{
				case DEVICE_NAME_COLUMN:
					return disc.getMeterData().getDeviceName();
	
				case POINT_NAME_COLUMN:
					return disc.getMeterData().getPointName();
						
				case TIMESTAMP_COLUMN:
					return disc.getTimeStamp();

				case METER_NUMBER_COLUMN:
				    return disc.getMeterData().getMeterNumber();
				    
				case ADDRESS_COLUMN:
				    return disc.getMeterData().getAddress();
				    
				case TYPE_COLUMN:
				    return disc.getType();
				
				case STATUS_COLUMN:
				{
				    if( disc.getValue() != null)
				        return getRPHValueString(disc.getValue().doubleValue());
				    else
				        return "UNKNOWN";
				}
				case COLL_GROUP_NAME_COLUMN:
					return disc.getMeterData().getCollGroup();

//				case ALT_GROUP_NAME_COLUMN:
//					return disc.getMeterData().getCollGroup();
//
//				case BILL_GROUP_NAME_COLUMN:
//					return disc.getMeterData().getCollGroup();
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
			    POINT_NAME_STRING,
			    TIMESTAMP_STRING,
			    METER_NUMBER_STRING,
			    ADDRESS_STRING,
			    TYPE_STRING,
			    STATUS_STRING,
			    COLL_GROUP_NAME_STRING
//			    ,
//			    ALT_GROUP_NAME_STRING,
//			    BILL_GROUP_NAME_STRING
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
				java.util.Date.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
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
				new ColumnProperties(offset, 1, offset+=120, null),	//Point Name
				new ColumnProperties(offset, 1, offset+=80, "MM/dd/yyyy HH:mm:ss"),   //Timestamp
				new ColumnProperties(offset, 1, offset+=50, null), //meternumber
				new ColumnProperties(offset, 1, offset+=50, null), //address
				new ColumnProperties(offset, 1, offset+=50, null), //type
				new ColumnProperties(offset, 1, offset+=50, null), //status
				new ColumnProperties(offset, 1, offset+=50, null) //collGroup
			};
		}
		return columnProperties;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		if( isShowAll())
			title = "All Disconnect";
		else if (isShowConnected())
			title = "Connected";
		else if (isShowDisconnected())
			title = "Disconnected";

		if( isShowHistory())
			title += " - Meter History Report";
		else
			title += " - Current Status Report";
			
		return title;
	}
	/**
	 * @return
	 */
	public boolean isShowConnected()
	{
		return showConnected;
	}

	/**
	 * @return
	 */
	public boolean isShowAll()
	{
		//Return true if CONNECTED AND DISCONNECTED are True OR if both are False
		return !(isShowConnected() ^ isShowDisconnected()); 
	}

	/**
	 * Only used when showHistory is false.
	 * Valid only with "current" data
	 * @return
	 */
	public boolean isShowDisconnected()
	{
		return showDisconnected;
	}

	/**
	 * @return
	 */
	public boolean isShowHistory()
	{
		return showHistory;
	}

	/**
	 * Only used when showHistory is false.
	 * Valid only with "current" data
	 * @param b
	 */
	public void setShowConnected(boolean b)
	{
		showConnected = b;
	}

	/**
	 * @param b
	 */
	public void setShowDisconnected(boolean b)
	{
		showDisconnected = b;
	}

	/**
	 * @param b
	 */
	public void setShowHistory(boolean b)
	{
		showHistory = b;
	}
	
	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<script>" + LINE_SEPARATOR;
		html += "function enableCheckBox(value){" + LINE_SEPARATOR;
		html += "  if( value) {" + LINE_SEPARATOR;
		html += "    document.reportForm.connected.checked = !value;" + LINE_SEPARATOR;
		html += "    document.reportForm.disconnected.checked = !value;" + LINE_SEPARATOR;		
		html += "  }" + LINE_SEPARATOR;
		html += "  document.reportForm.connected.disabled = value;" + LINE_SEPARATOR;
		html += "  document.reportForm.disconnected.disabled = value;" + LINE_SEPARATOR;
		html += "}" + LINE_SEPARATOR;
		html += "</script>" + LINE_SEPARATOR;
		
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>Data Display</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' name='" + ATT_SHOW_HISTORY +"' checked  value='history' onclick='enableDates(this.checked);enableCheckBox(this.checked)'>Historical" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Disconnect Status</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' name='" +ATT_SHOW_CONNECTED+ "' value='connnected' disabled >Show All Connected</td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;		
		html += "          <td><input type='checkbox' name='" +ATT_SHOW_DISCONNECTED + "' value='disconnected' disabled >Show All Disconnected</td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td" + LINE_SEPARATOR;
		
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}

	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
			String param = req.getParameter(ATT_SHOW_CONNECTED);
			setShowConnected(param != null);
			
			param = req.getParameter(ATT_SHOW_DISCONNECTED);
			setShowDisconnected(param != null);
			
			param = req.getParameter(ATT_SHOW_HISTORY);
			setShowHistory(param != null);	//opposite boolean value, since wording for option is "backwards"
			
		}
	}
}

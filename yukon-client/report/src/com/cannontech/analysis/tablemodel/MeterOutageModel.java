package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

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
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Created on Dec 15, 2003
 * MeterData TableModel object
 *  String collGroup	- DeviceMeterGroup.collectionGroup 
 *  String deviceName	- YukonPaobject.paoName
 *  String pointName	- Point.pointName
 *  Integer pointID		- Point.pointID
 *  String routeName  
 * @author snebben
 */
public class MeterOutageModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;
	
	/** Enum values for column representation */
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int DEVICE_TYPE_COLUMN = 1;
	public final static int METER_NUMBER_COLUMN = 2;
	public final static int PHYSICAL_ADDRESS_COLUMN = 3;
	public final static int ROUTE_NAME_COLUMN = 4;
	public final static int DATE_TIME_COLUMN = 5;
	public final static int DURATION = 6;
//	public final static int POINT_NAME_COLUMN = 7;

	/** String values for column representation */
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String DEVICE_TYPE_STRING = "Type";
	public final static String METER_NUMBER_STRING = "Meter Number";
	public final static String PHYSICAL_ADDRESS_STRING = "Address";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String DATE_TIME_STRING = "Date/Time";
	public final static String DURATION_STRING = "Duration";
	public final static String POINT_NAME_STRING = "Point Name";

	/** Class fields */
	private int minOutageSecs = 0;
	public static final int ORDER_BY_DEVICE_NAME = 0;
	public static final int ORDER_BY_TIMESTAMP = 1;
	public static final int ORDER_BY_DURATION = 2;

	private int orderBy = ORDER_BY_DEVICE_NAME;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_DEVICE_NAME, ORDER_BY_TIMESTAMP, ORDER_BY_DURATION
	};

	//servlet attributes/parameter strings
	private static final String ATT_ORDER_BY = "orderBy";
	private static final String ATT_MINIMUM_OUTAGE_SECS = "minOutageSecs";
	/**
	 * 
	 */
	public MeterOutageModel()
	{
		this(null);
	}
	/**
	 * 
	 */
	public MeterOutageModel(Date start_)
	{
		//Long.MIN_VALUE is the default (null) value for time
		super(start_, null);
	}
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
		    Integer paobjectID = new Integer(rset.getInt(1));
		    Integer pointID = new Integer(rset.getInt(2));
			Timestamp ts = rset.getTimestamp(3);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeInMillis(ts.getTime());
			Double value = new Double(rset.getDouble(4));

			MeterAndPointData meterAndPointData = new MeterAndPointData(paobjectID, pointID, new Date(cal.getTimeInMillis()), value);

			getData().add(meterAndPointData);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve MissedMeter data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT DISTINCT PAO.PAOBJECTID, P.POINTID, TIMESTAMP, VALUE, PAO.PAONAME " + 
			" FROM YUKONPAOBJECT PAO, POINT P, RAWPOINTHISTORY RPH " +
			" WHERE P.POINTOFFSET = 100 AND P.POINTTYPE = 'Analog' " +	// OUTAGE POINT OFFSET and POINTTYPE
			" AND P.POINTID = RPH.POINTID " +
			" AND P.PAOBJECTID = PAO.PAOBJECTID " +
			" AND VALUE >= " + getMinOutageSecs() + 
			" AND TIMESTAMP > ? AND TIMESTAMP <= ? ");
		
		sql.append(" ORDER BY ");	//TODO what to order by?
		if (getOrderBy() == ORDER_BY_DURATION)
			sql.append(" TIMESTAMP " );		
		else if (getOrderBy() == ORDER_BY_TIMESTAMP)
		    sql.append(" VALUE ");
		else //if (getOrderBy() == ORDER_BY_DEVICE_NAME) //default
			sql.append(" PAO.PAONAME " );

		if( getOrderBy() == DESCENDING)
		    sql.append(" DESC ");		    
		
		return sql;
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
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime() ));				
				CTILogger.info("START DATE >= " + getStartDate() + " - STOP DATE < " + getStopDate());
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

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof MeterAndPointData)
		{
			MeterAndPointData meterPD = ((MeterAndPointData)o);
			LiteYukonPAObject lPao = PAOFuncs.getLiteYukonPAO(meterPD.getPaobjectID().intValue());
			switch( columnIndex)
			{
				case DEVICE_NAME_COLUMN:
					return lPao.getPaoName();
					
				case DEVICE_TYPE_COLUMN:
				    return PAOGroups.getPAOTypeString(lPao.getType());
				    
				case METER_NUMBER_COLUMN:
				    LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber(meterPD.getPaobjectID().intValue());
				    return ( ldmn == null ? null : ldmn.getMeterNumber());
				    
				case PHYSICAL_ADDRESS_COLUMN:
				    return String.valueOf(lPao.getAddress());
				    
				case ROUTE_NAME_COLUMN:
					return PAOFuncs.getYukonPAOName(lPao.getRouteID());
					
				case DATE_TIME_COLUMN:
				    return meterPD.getTimeStamp();

				case DURATION:
				    return convertSecondsToTimeString(meterPD.getValue().intValue());
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
				DEVICE_TYPE_STRING,
				METER_NUMBER_STRING,
				PHYSICAL_ADDRESS_STRING,
				ROUTE_NAME_STRING,
				DATE_TIME_STRING,
				DURATION_STRING
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
				Date.class,
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
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 180, null),
				new ColumnProperties(180, 1, 80, null),
				new ColumnProperties(260, 1, 80, null),
				new ColumnProperties(340, 1, 80, null),
				new ColumnProperties(420, 1, 120, null),
				new ColumnProperties(540, 1, 100, "dd MMM yyyy  HH:mm:ss"),
				new ColumnProperties(640, 1, 80, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
	    String title = "Outage Report";
		return title;
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
			case ORDER_BY_TIMESTAMP:
				return "Timestamp";
			case ORDER_BY_DURATION:
			    return "Duration";
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
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Minimum Outage Duration</td>";
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='main'>";
		html += "            <input type='text' name='"+ATT_MINIMUM_OUTAGE_SECS +"' value='" + getMinOutageSecs() + "'>&nbsp;seconds";  
		html += "          </td>" + LINE_SEPARATOR;
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

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Sort Order</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		for (int i = 0; i < getAllSortOrders().length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='radio' name='" +ATT_SORT_ORDER + "' value='" + getAllSortOrders()[i] + "' " +  
			 (i==0? "checked" : "") + ">" + getSortOrderString(getAllSortOrders()[i])+ LINE_SEPARATOR;
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
			String param = req.getParameter(ATT_MINIMUM_OUTAGE_SECS);
			if( param != null)
				setMinOutageSecs(Integer.valueOf(param).intValue());
			else
				setMinOutageSecs(0);

			param = req.getParameter(ATT_ORDER_BY);
			if( param != null)
				setOrderBy(Integer.valueOf(param).intValue());
			else
				setOrderBy(ORDER_BY_DEVICE_NAME);
		}
	}

    /**
     * @return Returns the minOutageSecs.
     */
    public int getMinOutageSecs()
    {
        return minOutageSecs;
    }
    /**
     * @param minOutageSecs The minOutageSecs to set.
     */
    public void setMinOutageSecs(int minOutageSecs)
    {
        this.minOutageSecs = minOutageSecs;
    }
}
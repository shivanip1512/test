package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.LPMeterData;
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
 * @author snebben
 * LPSetupDBModel TableModel object
 * Innerclass object for row data is LPMeterData:
 *  String paoName			- YukonPaobject.paoName (device)
 *  String paoType			- YukonPaobject.type
 *  String address			- DeviceCarrierSettings.address
 *  String routeName		- YukonPaobject.paoName (route)
 */
public class LPSetupDBModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 9;
	
	/** Enum values for column representation */
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int DEVICE_TYPE_COLUMN = 1;
	public final static int METER_NUMBER_COLUMN = 2;
	public final static int ADDRESS_COLUMN = 3;
	public final static int ROUTE_NAME_COLUMN = 4;
	public final static int LAST_INTERVAL_DEMAND_COLUMN = 5;
	public final static int LAST_INTERVAL_VOLTAGE_COLUMN = 6;
	public final static int CHANNEL_1_DEMAND_RATE_COLUMN = 7;
	public final static int CHANNEL_4_VOLTAGE_DEMAND_RATE_COLUMN = 8;
	
	/** String values for column representation */
	public final static String DEVICE_NAME_STRING = "Meter Name";
	public final static String DEVICE_TYPE_STRING = "Type";
	public final static String METER_NUMBER_STRING = "Meter #";
	public final static String ADDRESS_STRING  = "Address";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String LAST_INTERVAL_DEMAND_STRING = "Demand\r\nInterval";
	public final static String LAST_INTERVAL_VOLTAGE_STRING = "Voltage\r\nInterval";
	public final static String CHANNEL_1_DEMAND_RATE_STRING = "Channel 1\r\nRate";
	public final static String CHANNEL_4_VOLTAGE_RATE_STRING = "Channel 4\r\nRate";
	
	public static final int ORDER_BY_DEVICE_NAME = 0;
	public static final int ORDER_BY_METER_NUMBER = 1;
	public static final int ORDER_BY_ROUTE_NAME = 2;
	private int orderBy = ORDER_BY_DEVICE_NAME;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_DEVICE_NAME, ORDER_BY_METER_NUMBER, ORDER_BY_ROUTE_NAME
	};

	private static final String ATT_ORDER_BY = "orderBy";
	
	/** A string for the title of the data */
	private static String title = "Database Report";
		
	public Comparator lpSetupComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2){

		    String thisVal = NULL_STRING;
		    String anotherVal = NULL_STRING;
		    LiteYukonPAObject pao1 = PAOFuncs.getLiteYukonPAO( ((LPMeterData)o1).getMeterAndPointData().getPaobjectID().intValue());
		    LiteYukonPAObject pao2 = PAOFuncs.getLiteYukonPAO( ((LPMeterData)o2).getMeterAndPointData().getPaobjectID().intValue());
		    
		    if( getOrderBy() == ORDER_BY_ROUTE_NAME)
		    {
		        thisVal = PAOFuncs.getYukonPAOName(pao1.getRouteID());
				anotherVal = PAOFuncs.getYukonPAOName(pao2.getRouteID());
		    }
		    else if( getOrderBy() == ORDER_BY_METER_NUMBER)
		    {
		        LiteDeviceMeterNumber ldmn1 = DeviceFuncs.getLiteDeviceMeterNumber( ((LPMeterData)o1).getMeterAndPointData().getPaobjectID().intValue());
			    LiteDeviceMeterNumber ldmn2 = DeviceFuncs.getLiteDeviceMeterNumber( ((LPMeterData)o2).getMeterAndPointData().getPaobjectID().intValue());
		        
		        thisVal = (ldmn1 == null ? NULL_STRING : ldmn1.getMeterNumber());
				anotherVal = (ldmn2 == null ? NULL_STRING : ldmn2.getMeterNumber());
		    }
		    if (getOrderBy() == ORDER_BY_DEVICE_NAME || thisVal.equalsIgnoreCase(anotherVal))
		    {
		        thisVal = PAOFuncs.getYukonPAOName(pao1.getYukonID());
		        anotherVal = PAOFuncs.getYukonPAOName(pao2.getYukonID());
		    }
			return (thisVal.compareToIgnoreCase(anotherVal));
		}
		public boolean equals(Object obj){
			return false;
		}
	};
	
	/**
	 * Default Constructor
	 */
	public LPSetupDBModel()
	{
		super();
	}

	/**
	 * Add LPMeterData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			Integer paobjectID = new Integer(rset.getInt(1));
			String lastIntDemand = String.valueOf(rset.getInt(9));
			String lastIntVoltage = String.valueOf(rset.getInt(9));
			String dmdRate = String.valueOf(rset.getInt(9));
			String voltageDmdRate = String.valueOf(rset.getInt(9));
				
			MeterAndPointData mpData = new MeterAndPointData(paobjectID, null, null, null);
			LPMeterData lpMeterData = new LPMeterData(mpData, lastIntDemand, lastIntVoltage, dmdRate, voltageDmdRate);
			getData().add(lpMeterData);
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
		StringBuffer sql = new StringBuffer	("SELECT PAO1.PAOBJECTID,  DLP.LASTINTERVALDEMANDRATE, VOLTAGEDMDINTERVAL, LOADPROFILEDEMANDRATE, VOLTAGEDMDRATE " +
			" FROM YUKONPAOBJECT PAO1, DEVICELOADPROFILE DLP "+
			" WHERE PAO1.PAOBJECTID = DMG.DEVICEID "+
			" AND PAO1.PAOBJECTID = DR.DEVICEID " + 
			" AND PAO1.PAOBJECTID = DLP.DEVICEID " +
			" AND PAO2.PAOBJECTID = DR.ROUTEID " +
			" AND PAO1.PAOBJECTID = DCS.DEVICEID ");

			sql.append(" ORDER BY ");
			if( getOrderBy() == ORDER_BY_DEVICE_NAME)
			{
			    sql.append(" PAO1.PAONAME ");
			}
			else if( getOrderBy() == ORDER_BY_METER_NUMBER)
			{
			    sql.append(" DMG.METERNUMBER ");
			}
			else if ( getOrderBy() == ORDER_BY_ROUTE_NAME)
			{
			    sql.append(" PAO2.PAONAME, PAO1.PAONAME ");
			}

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
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					addDataRow(rset);
				}
				if(getData() != null)
				{
//					Order the records
					Collections.sort(getData(), lpSetupComparator);
					if( getSortOrder() == DESCENDING)
					    Collections.reverse(getData());				
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
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	public String getDateRangeString()
	{
		//Use current date 
		return getDateFormat().format(new java.util.Date());
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof LPMeterData)
		{
			LPMeterData lpMeter = ((LPMeterData)o);
			switch( columnIndex)
			{
				case DEVICE_NAME_COLUMN:
					return lpMeter.getMeterAndPointData().getLitePaobject().getPaoName();
		
				case DEVICE_TYPE_COLUMN:
					return PAOGroups.getPAOTypeString(lpMeter.getMeterAndPointData().getLitePaobject().getType());

				case METER_NUMBER_COLUMN:
				    return ( lpMeter.getMeterAndPointData().getLiteDeviceMeterNumber() == null ? NULL_STRING : lpMeter.getMeterAndPointData().getLiteDeviceMeterNumber().getMeterNumber());
				case ADDRESS_COLUMN:
					return String.valueOf(lpMeter.getMeterAndPointData().getLitePaobject().getAddress());
	
				case ROUTE_NAME_COLUMN:
					return PAOFuncs.getYukonPAOName(lpMeter.getMeterAndPointData().getLitePaobject().getRouteID());
				
				case LAST_INTERVAL_DEMAND_COLUMN:
				    return lpMeter.getLastIntervalDemand();
				case LAST_INTERVAL_VOLTAGE_COLUMN:
				    return lpMeter.getLastIntervalVoltage();
				case CHANNEL_1_DEMAND_RATE_COLUMN:
				    return lpMeter.getDemandRate();
				case CHANNEL_4_VOLTAGE_DEMAND_RATE_COLUMN:
				    return lpMeter.getVoltageDemandRate();				
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
				ADDRESS_STRING,
				ROUTE_NAME_STRING,
				LAST_INTERVAL_DEMAND_STRING,
				LAST_INTERVAL_VOLTAGE_STRING,
				CHANNEL_1_DEMAND_RATE_STRING,
				CHANNEL_4_VOLTAGE_RATE_STRING
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
				new ColumnProperties(offset, 1, offset+=150, null),
				new ColumnProperties(offset, 1, offset+=75, null),
				new ColumnProperties(offset, 1, offset+=70, null),
				new ColumnProperties(offset, 1, offset+=70, null),
				new ColumnProperties(offset, 1, offset+=125, null),
				new ColumnProperties(offset, 1, offset+=50, null),
				new ColumnProperties(offset, 1, offset+=50, null),
				new ColumnProperties(offset, 1, offset+=60, null),
				new ColumnProperties(offset, 1, offset+=60, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title + " - LP Meter Data";
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#useStartDate()
	 */
	public boolean useStartDate()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#useStopDate()
	 */
	public boolean useStopDate()
	{
		return false;
	}

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
				return "Meter Name";
			case ORDER_BY_METER_NUMBER:
				return "Meter Number";
			case ORDER_BY_ROUTE_NAME:
			    return "Route Name";
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
			String param = req.getParameter(ATT_ORDER_BY);
			if( param != null)
				setOrderBy(Integer.valueOf(param).intValue());
			else
				setOrderBy(ORDER_BY_DEVICE_NAME);			
		}		
	}
}

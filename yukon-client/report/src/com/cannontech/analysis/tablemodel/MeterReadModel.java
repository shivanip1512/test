package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.MeterData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.device.DeviceMeterGroup;

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
public class MeterReadModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 8;
	
	/** Enum values for column representation */
	public final static int SORT_BY_GROUP_NAME_COLUMN = 0;
	public final static int DEVICE_NAME_COLUMN = 1;
	public final static int METER_NUMBER_COLUMN = 2;
	public final static int PHYSICAL_ADDRESS_COLUMN = 3;
	public final static int POINT_NAME_COLUMN = 4;
	public final static int ROUTE_NAME_COLUMN = 5;
	public final static int GROUP_NAME_1_COLUMN = 6;
	public final static int GROUP_NAME_2_COLUMN = 7;

	/** String values for column representation */
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String METER_NUMBER_STRING = "Meter Number";
	public final static String PHYSICAL_ADDRESS_STRING = "Address";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String ALT_GROUP_NAME_STRING = "Alternate Group";
	public final static String BILLING_GROUP_NAME_STRING = "Billing Group";
	/** Class fields */
	public final static int MISSED_METER_READ_TYPE = 2;
	public  final static int SUCCESS_METER_READ_TYPE = 1;
	private int meterReadType = MISSED_METER_READ_TYPE;
	
	public static final int ORDER_BY_DEVICE_NAME = 0;
	public static final int ORDER_BY_ROUTE_NAME = 1;
	public static final int ORDER_BY_METER_NUMBER = 2;
	private int orderBy = ORDER_BY_DEVICE_NAME;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_DEVICE_NAME, ORDER_BY_ROUTE_NAME, ORDER_BY_METER_NUMBER
	};

	//servlet attributes/parameter strings
	private static String ATT_METER_READ_TYPE = "meterReadType";
	private static final String ATT_ORDER_BY = "orderBy";
	/**
	 * 
	 */
	public MeterReadModel()
	{
		this(MISSED_METER_READ_TYPE);
	}
	
	/**
	 * Valid read types are MeterReadModel.SUCCESS_METER_READ_TYPE, MISSED_METER_READ_TYPE
	 */
	public MeterReadModel(int readType)
	{
		this(readType, null);
	}
	/**
	 * 
	 */
	public MeterReadModel(Date start_)
	{
		//Long.MIN_VALUE is the default (null) value for time
		this(MISSED_METER_READ_TYPE, null);
	}	
	/**
	 * 
	 */
	public MeterReadModel(int readType_, Date start_)
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
			String collGrp = rset.getString(1);
			String testGrp = rset.getString(2);
			String billGrp = rset.getString(3);
			String meterNum = rset.getString(4);
			String paoName = rset.getString(5);
			String pointName = rset.getString(6);					
			String routeName = rset.getString(7);
			String address = String.valueOf(rset.getInt(8));
			MeterData missedMeter = new MeterData(collGrp, testGrp, billGrp, paoName, meterNum, address, pointName, routeName);

			getData().add(missedMeter);
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
		StringBuffer sql = new StringBuffer	("SELECT DISTINCT DMG.COLLECTIONGROUP, DMG.TESTCOLLECTIONGROUP, DMG.BILLINGGROUP, DMG.METERNUMBER, PAO.PAONAME, P.POINTNAME, " + 
			" PAO2.PAONAME ROUTENAME, ADDRESS " +
			" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, POINT P, YUKONPAOBJECT PAO2, DEVICEROUTES DR, POINTUNIT PU, UNITMEASURE UM, DEVICECARRIERSETTINGS DCS " +
			" WHERE PAO.PAOBJECTID = DMG.DEVICEID " +
			" AND PAO.PAOBJECTID = DCS.DEVICEID " +
			" AND PAO.PAOBJECTID = DR.DEVICEID " +
			" AND P.POINTID = PU.POINTID " +
			" AND PU.UOMID = UM.UOMID " +
			" AND UM.FORMULA ='usage' " +
			" AND PAO2.PAOBJECTID = DR.ROUTEID " +
			" AND P.PAOBJECTID = PAO.PAOBJECTID " +
			" AND PAO.PAOCLASS = 'CARRIER' ");
			
		if( getBillingGroups() != null && getBillingGroups().length > 0)
		{
			sql.append(" AND " + DeviceMeterGroup.getValidBillGroupTypeStrings()[getBillingGroupType()] + " IN ( '" + getBillingGroups()[0]);
			for (int i = 1; i < getBillingGroups().length; i++)
				sql.append("', '" + getBillingGroups()[i]);
			sql.append("') ");
		}

	 
		sql.append(" AND P.POINTID " + getInclusiveSQLString() +
				" (SELECT DISTINCT POINTID FROM RAWPOINTHISTORY WHERE TIMESTAMP > ? AND TIMESTAMP <= ? )");
		
		if (getBillingGroupType() == DeviceMeterGroup.TEST_COLLECTION_GROUP)
			sql.append(" ORDER BY DMG.TESTCOLLECTIONGROUP");
		else if ( getBillingGroupType() == DeviceMeterGroup.BILLING_GROUP)
	    	sql.append(" ORDER BY DMG.BILLINGGROUP");
		else	//CollectionGroup
		    sql.append(" ORDER BY DMG.COLLECTIONGROUP");
		
		if (getOrderBy() == ORDER_BY_DEVICE_NAME)
			sql.append(", PAO.PAONAME, P.POINTNAME " );
		else if (getOrderBy() == ORDER_BY_ROUTE_NAME)
			sql.append(", PAO2.PAONAME " );		
		else if (getOrderBy() == ORDER_BY_METER_NUMBER)
		    sql.append(", DMG.METERNUMBER ");
		
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
	 * @see com.cannontech.analysis.data.ReportModelBase#getDateRangeString()
	 */
	 public String getDateRangeString()
	 {
		 return (getDateFormat().format(getStartDate()) + " through " +
					(getDateFormat().format(getStopDate())));
	 }
	 
	 private String getInclusiveSQLString()
	 {
	 	switch (getMeterReadType())
		{
			case SUCCESS_METER_READ_TYPE:
				return " IN ";
			case MISSED_METER_READ_TYPE:
			default :
				return " NOT IN";
		}
	 }

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof MeterData)
		{
			MeterData meter = ((MeterData)o);
			switch( columnIndex)
			{
				case SORT_BY_GROUP_NAME_COLUMN:
				{
				    if( getBillingGroupType() == DeviceMeterGroup.TEST_COLLECTION_GROUP)
				        return meter.getTestCollGroup();
				    else if( getBillingGroupType() == DeviceMeterGroup.BILLING_GROUP)
				        return meter.getBillingGroup();
				    else //if( getBillingGroupType() == DeviceMeterGroup.COLLECTION_GROUP)
				        return meter.getCollGroup();
				}
				case DEVICE_NAME_COLUMN:
					return meter.getDeviceName();
					
				case METER_NUMBER_COLUMN:
				    return meter.getMeterNumber();
				    
				case PHYSICAL_ADDRESS_COLUMN:
				    return meter.getAddress();
				    
				case POINT_NAME_COLUMN:
					return meter.getPointName();
	
				case ROUTE_NAME_COLUMN:
					return meter.getRouteName();
					
				case GROUP_NAME_1_COLUMN:
				{
				    if( getBillingGroupType() == DeviceMeterGroup.COLLECTION_GROUP)
				        return meter.getTestCollGroup();
				    else 
				        return meter.getCollGroup();
				}				    
				case GROUP_NAME_2_COLUMN:
				{
				    if( getBillingGroupType() == DeviceMeterGroup.BILLING_GROUP)
				        return meter.getTestCollGroup();
				    else 
				        return meter.getBillingGroup();
				}				    
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
		    if(getBillingGroupType() == DeviceMeterGroup.TEST_COLLECTION_GROUP)
		    {
				columnNames = new String[]{
					ALT_GROUP_NAME_STRING,
					DEVICE_NAME_STRING,
					METER_NUMBER_STRING,
					PHYSICAL_ADDRESS_STRING,
					POINT_NAME_STRING,
					ROUTE_NAME_STRING,
					COLL_GROUP_NAME_STRING,
					BILLING_GROUP_NAME_STRING
				};
		    }
		    else if(getBillingGroupType() == DeviceMeterGroup.BILLING_GROUP)
		    {
				columnNames = new String[]{
					BILLING_GROUP_NAME_STRING,
					DEVICE_NAME_STRING,
					METER_NUMBER_STRING,
					PHYSICAL_ADDRESS_STRING,
					POINT_NAME_STRING,
					ROUTE_NAME_STRING,
					COLL_GROUP_NAME_STRING,
					ALT_GROUP_NAME_STRING
				};
		    }
		    else //if(getBillingGroupType() == DeviceMeterGroup.COLLECTION_GROUP)
		    {
				columnNames = new String[]{
					COLL_GROUP_NAME_STRING,
					DEVICE_NAME_STRING,
					METER_NUMBER_STRING,
					PHYSICAL_ADDRESS_STRING,
					POINT_NAME_STRING,
					ROUTE_NAME_STRING,
					ALT_GROUP_NAME_STRING,
					BILLING_GROUP_NAME_STRING
				};
		    }
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
				new ColumnProperties(0, 1, 200, null),
				new ColumnProperties(0, 1, 180, null),
				new ColumnProperties(180, 1, 65, null),
				new ColumnProperties(245, 1, 60, null),
				new ColumnProperties(305, 1, 90, null),
				new ColumnProperties(395, 1, 165, null),
				new ColumnProperties(560, 1, 77, null),
				new ColumnProperties(637, 1, 75, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
	    String title = "";
	    if( getMeterReadType() == SUCCESS_METER_READ_TYPE)
			title += "Succesful ";
		else if( getMeterReadType() ==  MISSED_METER_READ_TYPE)
    	    title += "Missed ";
	    	    
		title += "Meter Data";
		if( getBillingGroupType() == DeviceMeterGroup.COLLECTION_GROUP)
		    title += " By Collection Group";
		else if( getBillingGroupType() == DeviceMeterGroup.TEST_COLLECTION_GROUP)
		    title += " By Alternate Group";
		else if( getBillingGroupType() == DeviceMeterGroup.BILLING_GROUP)
		    title += " By Billing Group";
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
				return "Order By Device Name";
			case ORDER_BY_METER_NUMBER:
				return "Order By Meter Number";
			case ORDER_BY_ROUTE_NAME:
			    return "Order By Route Name";
		}
		return "UNKNOWN";
	}
	public static int[] getAllOrderBys()
	{
		return ALL_ORDER_BYS;
	}	
	/**
	 * @return
	 */
	public int getMeterReadType()
	{
		return meterReadType;
	}

	/**
	 * @param i
	 */
	public void setMeterReadType(int i)
	{
		meterReadType = i;
	}

	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		html += "    <td align='center'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>Meter Read Status</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;

		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='radio' name='" + ATT_METER_READ_TYPE +"' value='" + MISSED_METER_READ_TYPE + "' checked>Missed Read" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='radio' name='" + ATT_METER_READ_TYPE +"' value='" + SUCCESS_METER_READ_TYPE + "' >Successful Read" + LINE_SEPARATOR;
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

		
		html += "  </tr>" + LINE_SEPARATOR;
		html += "</table>" + LINE_SEPARATOR;
		return html;
	}

	public void setParameters( HttpServletRequest req )
	{
		super.setParameters(req);
		if( req != null)
		{
			String param = req.getParameter(ATT_METER_READ_TYPE);
			if( param != null)
				setMeterReadType(Integer.valueOf(param).intValue());
			else
				setMeterReadType(MISSED_METER_READ_TYPE);
			
			param = req.getParameter(ATT_ORDER_BY);
			if( param != null)
				setOrderBy(Integer.valueOf(param).intValue());
			else
				setOrderBy(ORDER_BY_DEVICE_NAME);
							
		}
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.tablemodel.ReportModelBase#useBillingGroup()
	 */
	public boolean useBillingGroup()
	{
		return true;
	}

	/**
	 * Override ReportModelBase in order to reset the column headings.
	 * @param i
	 */
	public void setBillingGroupType(int billGroupType)
	{
		if( getBillingGroupType() != billGroupType)
			columnNames = null;
	    super.setBillingGroupType(billGroupType);
	}

}

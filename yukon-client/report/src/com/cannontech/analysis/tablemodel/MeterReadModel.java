package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.Collections;
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
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.database.model.ModelFactory;

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
	
	public Comparator meterReadComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2){
	        LiteDeviceMeterNumber ldmn1 = DeviceFuncs.getLiteDeviceMeterNumber( ((MeterAndPointData)o1).getPaobjectID().intValue());
		    LiteDeviceMeterNumber ldmn2 = DeviceFuncs.getLiteDeviceMeterNumber( ((MeterAndPointData)o2).getPaobjectID().intValue());

		    String thisVal = NULL_STRING;
		    String anotherVal = NULL_STRING;
		    //Always sort by group first
			if (getFilterModelType() == ModelFactory.TESTCOLLECTIONGROUP)
			{
			    thisVal = (ldmn1 == null ? NULL_STRING : ldmn1.getTestCollGroup());
			    anotherVal = (ldmn2 == null ? NULL_STRING : ldmn2.getTestCollGroup());
			}
			else if ( getFilterModelType() == ModelFactory.BILLING_GROUP)
			{
			    thisVal = (ldmn1 == null ? NULL_STRING : ldmn1.getBillGroup());
			    anotherVal = (ldmn2 == null ? NULL_STRING : ldmn2.getBillGroup());
			}
			else	//CollectionGroup
			{
			    thisVal = (ldmn1 == null ? NULL_STRING : ldmn1.getCollGroup());
			    anotherVal = (ldmn2 == null ? NULL_STRING : ldmn2.getCollGroup());
			}
			if( thisVal.equalsIgnoreCase(anotherVal))
			{
			    LiteYukonPAObject pao1 = PAOFuncs.getLiteYukonPAO( ((MeterAndPointData)o1).getPaobjectID().intValue());
			    LiteYukonPAObject pao2 = PAOFuncs.getLiteYukonPAO( ((MeterAndPointData)o2).getPaobjectID().intValue());
			    
			    if( getOrderBy() == ORDER_BY_ROUTE_NAME)
			    {
			        thisVal = PAOFuncs.getYukonPAOName(pao1.getRouteID());
					anotherVal = PAOFuncs.getYukonPAOName(pao2.getRouteID());
			    }
			    else if( getOrderBy() == ORDER_BY_METER_NUMBER)
			    {
			        thisVal = (ldmn1 == null ? NULL_STRING : ldmn1.getMeterNumber());
					anotherVal = (ldmn2 == null ? NULL_STRING : ldmn2.getMeterNumber());
			    }
			    if (getOrderBy() == ORDER_BY_DEVICE_NAME || thisVal.equalsIgnoreCase(anotherVal))
			    {
			        thisVal = PAOFuncs.getYukonPAOName(pao1.getYukonID());
			        anotherVal = PAOFuncs.getYukonPAOName(pao2.getYukonID());
			        if( thisVal.equalsIgnoreCase(anotherVal))
			        {
					    		            
				        thisVal = PointFuncs.getPointName( ((MeterAndPointData)o1).getPointID().intValue());
				        anotherVal = PointFuncs.getPointName( ((MeterAndPointData)o2).getPointID().intValue());
			        }
			    }
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}
		public boolean equals(Object obj){
			return false;
		}
	};

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
		setFilterModelTypes(new int[]{ 
    			ModelFactory.COLLECTIONGROUP, 
    			ModelFactory.TESTCOLLECTIONGROUP, 
    			ModelFactory.BILLING_GROUP}
				);
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
			MeterAndPointData mpData = new MeterAndPointData(paobjectID, pointID, null, null);

			getData().add(mpData);
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
		StringBuffer sql = new StringBuffer	("SELECT DISTINCT PAO.PAOBJECTID, P.POINTID " +
			" FROM YUKONPAOBJECT PAO, POINT P, POINTUNIT PU, UNITMEASURE UM ");
		
			if( getBillingGroups() != null && getBillingGroups().length > 0)
			    sql.append(", DEVICEMETERGROUP DMG ");
			
			sql.append(" WHERE PAO.PAOCLASS = '" + DeviceClasses.STRING_CLASS_CARRIER + "' " +
			" AND P.POINTID = PU.POINTID " +
			" AND PU.UOMID = UM.UOMID " +
			" AND UM.FORMULA ='usage' " +	//TODO - how to choose which points to show.
			" AND P.PAOBJECTID = PAO.PAOBJECTID ");
			
		if( getBillingGroups() != null && getBillingGroups().length > 0)
		{
			sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID " +
			        " AND " + getBillingGroupDatabaseString(getFilterModelType()) + " IN ( '" + getBillingGroups()[0]);			        
			for (int i = 1; i < getBillingGroups().length; i++)
				sql.append("', '" + getBillingGroups()[i]);
			sql.append("') ");
		}

	 
		sql.append(" AND P.POINTID " + getInclusiveSQLString() +
				" (SELECT DISTINCT POINTID FROM RAWPOINTHISTORY WHERE TIMESTAMP > ? AND TIMESTAMP <= ? )");
//		sql.append(" ORDER BY ");
//		if( getBillingGroups() != null && getBillingGroups().length > 0)
//		{
//			if (getBillingGroupType() == DeviceMeterGroup.TEST_COLLECTION_GROUP)
//				sql.append(" DMG.TESTCOLLECTIONGROUP, ");
//			else if ( getBillingGroupType() == DeviceMeterGroup.BILLING_GROUP)
//		    	sql.append(" DMG.BILLINGGROUP, ");
//			else	//CollectionGroup
//			    sql.append(" DMG.COLLECTIONGROUP, ");
//		}
//		
////		if (getOrderBy() == ORDER_BY_DEVICE_NAME)
//			sql.append(" PAO.PAONAME, P.POINTNAME " );

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
				if(getData() != null)
				{
//					Order the records
					Collections.sort(getData(), meterReadComparator);
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
		if ( o instanceof MeterAndPointData)
		{
			MeterAndPointData mpData = ((MeterAndPointData)o);
			LiteYukonPAObject lPao = PAOFuncs.getLiteYukonPAO(mpData.getPaobjectID().intValue());
			LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber(mpData.getPaobjectID().intValue());
			switch( columnIndex)
			{
				case SORT_BY_GROUP_NAME_COLUMN:
				{
				    if( ldmn == null)
				        return NULL_STRING;
				    if( getFilterModelType() == DeviceMeterGroup.TEST_COLLECTION_GROUP)
				        return ldmn.getTestCollGroup();
				    else if( getFilterModelType() == DeviceMeterGroup.BILLING_GROUP)
				        return ldmn.getBillGroup();
				    else //if( getFilterModelType() == DeviceMeterGroup.COLLECTION_GROUP)
				        return ldmn.getCollGroup();
				}
				case DEVICE_NAME_COLUMN:
					return lPao.getPaoName();
					
				case METER_NUMBER_COLUMN:
				    if( ldmn == null)
				        return NULL_STRING;
				    return ldmn.getMeterNumber();
				    
				case PHYSICAL_ADDRESS_COLUMN:
				    return String.valueOf(lPao.getAddress());
				    
				case POINT_NAME_COLUMN:
					return PointFuncs.getPointName(mpData.getPointID().intValue());
	
				case ROUTE_NAME_COLUMN:
					return PAOFuncs.getYukonPAOName(lPao.getRouteID());
					
				case GROUP_NAME_1_COLUMN:
				{
				    if( ldmn == null)
				        return NULL_STRING;
				    if( getFilterModelType() == DeviceMeterGroup.COLLECTION_GROUP)
				        return ldmn.getTestCollGroup();
				    else 
				        return ldmn.getCollGroup();
				}				    
				case GROUP_NAME_2_COLUMN:
				{
				    if( ldmn == null)
				        return NULL_STRING;
				    if( getFilterModelType() == DeviceMeterGroup.BILLING_GROUP)
				        return ldmn.getTestCollGroup();
				    else 
				        return ldmn.getBillGroup();
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
		    if(getFilterModelType() == DeviceMeterGroup.TEST_COLLECTION_GROUP)
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
		    else if(getFilterModelType() == DeviceMeterGroup.BILLING_GROUP)
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
		    else //if(getFilterModelType() == DeviceMeterGroup.COLLECTION_GROUP)
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
		if( getFilterModelType() == DeviceMeterGroup.COLLECTION_GROUP)
		    title += " By Collection Group";
		else if( getFilterModelType() == DeviceMeterGroup.TEST_COLLECTION_GROUP)
		    title += " By Alternate Group";
		else if( getFilterModelType() == DeviceMeterGroup.BILLING_GROUP)
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
	/**
	 * Override ReportModelBase in order to reset the column headings.
	 * @param i
	 */
	public void setBillingGroupType(int billGroupType)
	{
		if( getFilterModelType() != billGroupType)
			columnNames = null;
	    super.setFilterModelType(billGroupType);
	}

}

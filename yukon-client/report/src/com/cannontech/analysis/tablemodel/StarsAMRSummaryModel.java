package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.analysis.data.stars.StarsAMRDetail;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.DeviceFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
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
public class StarsAMRSummaryModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 11;
	
	/** Enum values for column representation */
	public final static int SORT_BY_COLUMN = 0;	//collgroup or route
	public final static int ACCOUNT_NUMBER_COLUMN = 1;
	public final static int CONTACT_NAME_COLUMN = 2;
	public final static int MAP_NUMBER_COLUMN = 3;
	public final static int DEVICE_NAME_COLUMN = 4;
	public final static int DEVICE_TYPE_COLUMN = 5;
	public final static int PHYSICAL_ADDRESS_COLUMN = 6;
	public final static int METER_NUMBER_COLUMN = 7;
	public final static int ROUTE_NAME_OR_COLL_GROUP_COLUMN = 8;	//the other one of the sort by column value
	public final static int LAST_KWH_READING_COLUMN = 9;
	public final static int DATE_TIME_COLUMN = 10;

	/** String values for column representation */
	public final static String COLLECTION_GROUP_STRING = "Collection Group";	//collgroup or route
	public final static String ACCOUNT_NUMBER_STRING = "Account #";
	public final static String CONTACT_NAME_STRING = "Contact";
	public final static String MAP_NUMBER_STRING = "Map #";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String DEVICE_TYPE_STRING = "Device Type";
	public final static String PHYSICAL_ADDRESS_STRING = "Address";
	public final static String METER_NUMBER_STRING = "Meter #";
	public final static String ROUTE_NAME_STRING = "Route Name";	//the other one of the sort by column value
	public final static String LAST_KWH_READING_STRING = "Last kWh";
	public final static String DATE_TIME_STRING = "Date/Time";

	/** Class fields */
	//Order by CollectionGroup, Route based on FILTER selection, and not the user's sort by option
	public static final int ORDER_BY_ACCOUNT_NUMBER = 0;
	public static final int ORDER_BY_LAST_NAME = 1;
	public static final int ORDER_BY_MAP_NUMBER = 2;
	public static final int ORDER_BY_PHYSICAL_ADDRESS = 3;
	public static final int ORDER_BY_DEVICE_NAME = 4;
	public static final int ORDER_BY_DEVICE_TYPE = 5;
	public static final int ORDER_BY_METER_NUMBER = 6;
	private int orderBy = ORDER_BY_ACCOUNT_NUMBER;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_ACCOUNT_NUMBER, ORDER_BY_LAST_NAME, ORDER_BY_MAP_NUMBER, ORDER_BY_PHYSICAL_ADDRESS, 
		ORDER_BY_DEVICE_NAME, ORDER_BY_DEVICE_TYPE, ORDER_BY_METER_NUMBER 
	};

	public Comparator starsAMRSummaryComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2){
		    int tempOrderBy = getOrderBy();
	        LiteDeviceMeterNumber ldmn1 = DeviceFuncs.getLiteDeviceMeterNumber( ((StarsAMRDetail)o1).getMeterPointData().getPaobjectID().intValue());
		    LiteDeviceMeterNumber ldmn2 = DeviceFuncs.getLiteDeviceMeterNumber( ((StarsAMRDetail)o2).getMeterPointData().getPaobjectID().intValue());
			
//			if (getOrderBy() == ORDER_BY_DEVICE_NAME)
//				sql.append(", PAO.PAONAME, P.POINTNAME " );
//			else if (getOrderBy() == ORDER_BY_ROUTE_NAME)
//				sql.append(", PAO2.PAONAME " );		
//			else if (getOrderBy() == ORDER_BY_METER_NUMBER)
//			    sql.append(", DMG.METERNUMBER ");

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
			if ( thisVal.equalsIgnoreCase(anotherVal) )
	        {
			    if( tempOrderBy == ORDER_BY_DEVICE_TYPE)
			    {
			        thisVal = PAOGroups.getPAOTypeString( ((StarsAMRDetail)o1).getLitePaobject().getType());
			        anotherVal = PAOGroups.getPAOTypeString( ((StarsAMRDetail)o2).getLitePaobject().getType());
			        if( thisVal.equalsIgnoreCase(anotherVal))
			            tempOrderBy = ORDER_BY_DEVICE_NAME;
			    }
			    if( tempOrderBy == ORDER_BY_DEVICE_NAME)
			    {
			        thisVal = ((StarsAMRDetail)o1).getLitePaobject().getPaoName();
			        anotherVal = ((StarsAMRDetail)o2).getLitePaobject().getPaoName();
			        if( thisVal.equalsIgnoreCase(anotherVal))
			            tempOrderBy = ORDER_BY_PHYSICAL_ADDRESS;
			    }
			    if( tempOrderBy == ORDER_BY_ACCOUNT_NUMBER)
			    {
			        thisVal = ((StarsAMRDetail)o1).getAccountNumber();
					anotherVal = ((StarsAMRDetail)o2).getAccountNumber();
					if (thisVal.equalsIgnoreCase(anotherVal))
					    tempOrderBy = ORDER_BY_LAST_NAME;	//Need to order by lastName
			    }
			    if( tempOrderBy == ORDER_BY_LAST_NAME )
			    {
			        thisVal = ((StarsAMRDetail)o1).getLitePrimaryContact().getContLastName();
					anotherVal = ((StarsAMRDetail)o2).getLitePrimaryContact().getContLastName();
					if ( thisVal.equalsIgnoreCase(anotherVal))
					{
					    thisVal = ((StarsAMRDetail)o1).getLitePrimaryContact().getContFirstName();
					    anotherVal = ((StarsAMRDetail)o2).getLitePrimaryContact().getContFirstName();
					    if (thisVal.equalsIgnoreCase(anotherVal))					    
					        tempOrderBy = ORDER_BY_PHYSICAL_ADDRESS;	//Need to order by physicalAddress
					}
			    }
			    if( tempOrderBy == ORDER_BY_PHYSICAL_ADDRESS)
			    {
			        thisVal = String.valueOf( ((StarsAMRDetail)o1).getLitePaobject().getAddress());
					anotherVal = String.valueOf( ((StarsAMRDetail)o2).getLitePaobject().getAddress());
			    }
			    if( tempOrderBy == ORDER_BY_MAP_NUMBER)
			    {
			        thisVal = ((StarsAMRDetail)o1).getMapNumber();
					anotherVal = ((StarsAMRDetail)o2).getMapNumber();
			    }
			    if( tempOrderBy == ORDER_BY_METER_NUMBER)
			    {
			        thisVal = ((StarsAMRDetail)o1).getLiteDeviceMeterNumber() == null ? NULL_STRING : ((StarsAMRDetail)o1).getLiteDeviceMeterNumber().getMeterNumber();
					anotherVal = ((StarsAMRDetail)o2).getLiteDeviceMeterNumber() == null ? NULL_STRING : ((StarsAMRDetail)o2).getLiteDeviceMeterNumber().getMeterNumber();
			    }
	        }
	        return ( thisVal.compareToIgnoreCase(anotherVal));
		}
	};
	//servlet attributes/parameter strings
	private static final String ATT_ORDER_BY = "orderBy";
	/**
	 * 
	 */
	public StarsAMRSummaryModel()
	{
		this(null, null);
	}
	
	/**
	 * 
	 */
	public StarsAMRSummaryModel(Date start_)
	{
		//Long.MIN_VALUE is the default (null) value for time
		this(start_, null);
	}	
	/**
	 * 
	 */
	public StarsAMRSummaryModel(Date start_, Date stop_)
	{
		//Long.MIN_VALUE is the default (null) value for time
		super(start_, stop_);
		setFilterModelTypes(new int[]{ 
    			ModelFactory.COLLECTIONGROUP,
    			ModelFactory.ROUTE}
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
//PAO1.PAOBJECTID, P.POINTID, CA.ACCOUNTNUMBER, SITE.SITENUMBER, RPH1.VALUE, RPH1.TIMESTAMP, CUST.CUSTOMERID 
			Integer paobjectID = new Integer(rset.getInt(1));
			Integer pointID = new Integer(rset.getInt(2));
			String accountNumber = rset.getString(3);
			String mapNumber = rset.getString(4);
			Double value = new Double(rset.getDouble(5));
			Timestamp dtTS = rset.getTimestamp(6);
			Date dateTime = new Date(dtTS.getTime());
			Integer customerID = new Integer(rset.getInt(7));
			MeterAndPointData mpData = new MeterAndPointData(paobjectID, pointID, dateTime, value );
			StarsAMRDetail detail = new StarsAMRDetail(mpData, accountNumber, customerID, mapNumber);

			getData().add(detail);
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
		StringBuffer sql = new StringBuffer	("SELECT DISTINCT PAO1.PAOBJECTID, P.POINTID, " +
				" CA.ACCOUNTNUMBER, SITE.SITENUMBER, RPH1.VALUE, RPH1.TIMESTAMP, CUST.CUSTOMERID " +
				" FROM YUKONPAOBJECT PAO1, CUSTOMERACCOUNT CA, ACCOUNTSITE SITE, CUSTOMER CUST, INVENTORYBASE IB, POINT P, RAWPOINTHISTORY RPH1 ");
			if( getBillingGroups() != null && getBillingGroups().length > 0)
			    sql.append(", DEVICEMETERGROUP DMG ");
		 		
				sql.append(" WHERE CUST.CUSTOMERID = CA.CUSTOMERID " +
				" AND CA.ACCOUNTSITEID = SITE.ACCOUNTSITEID " +
				" AND IB.ACCOUNTID = CA.ACCOUNTID " +
				" AND IB.DEVICEID > 0 " +
				" AND IB.DEVICEID = PAO1.PAOBJECTID " +
				" AND P.PAOBJECTID = PAO1.PAOBJECTID " +
				" AND P.POINTTYPE = '" + PointTypes.getType(PointTypes.PULSE_ACCUMULATOR_POINT) +"' " +
				" AND P.POINTOFFSET = 1 " +
				" AND P.POINTID = RPH1.POINTID " +
				" AND RPH1.TIMESTAMP = ( SELECT MAX(RPH2.TIMESTAMP) FROM RAWPOINTHISTORY RPH2 " +
				" WHERE RPH1.POINTID = RPH2.POINTID)");
				
//				TODO ??? sql.append(" AND P.POINTID " + getInclusiveSQLString() +
//				" (SELECT DISTINCT POINTID FROM RAWPOINTHISTORY WHERE TIMESTAMP > ? AND TIMESTAMP <= ? )");

		if( getBillingGroups() != null && getBillingGroups().length > 0)
		{
			sql.append(" AND " + getBillingGroupDatabaseString(getFilterModelType()) + " IN ( '" + getBillingGroups()[0]);
			for (int i = 1; i < getBillingGroups().length; i++)
				sql.append("', '" + getBillingGroups()[i]);
			sql.append("') ");
		
		if (getFilterModelType() == ModelFactory.TESTCOLLECTIONGROUP)
			sql.append(" ORDER BY DMG.TESTCOLLECTIONGROUP");
		else if ( getFilterModelType() == ModelFactory.BILLING_GROUP)
	    	sql.append(" ORDER BY DMG.BILLINGGROUP");
		else	//CollectionGroup
		    sql.append(" ORDER BY DMG.COLLECTIONGROUP");
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
//				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ));
//				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime() ));				
//				CTILogger.info("START DATE >= " + getStartDate() + " - STOP DATE < " + getStopDate());
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
		if ( o instanceof StarsAMRDetail)
		{
			StarsAMRDetail detail = ((StarsAMRDetail)o);
			switch( columnIndex)
			{
				case SORT_BY_COLUMN:	//TODO ADD route sort column
				{
				    if( getFilterModelType() == ModelFactory.TESTCOLLECTIONGROUP)
				        return (detail.getLiteDeviceMeterNumber() == null ? null : detail.getLiteDeviceMeterNumber().getTestCollGroup());
				    else if( getFilterModelType() == ModelFactory.BILLING_GROUP)
				        return (detail.getLiteDeviceMeterNumber() == null ? null : detail.getLiteDeviceMeterNumber().getBillGroup());
				    else //if( getFilterModelType() == ModelFactory.COLLECTIONGROUP)
				        return (detail.getLiteDeviceMeterNumber() == null ? null : detail.getLiteDeviceMeterNumber().getCollGroup());
				}
				case ACCOUNT_NUMBER_COLUMN:
				    return detail.getAccountNumber();
				case CONTACT_NAME_COLUMN:
				    return (detail.getLitePrimaryContact() == null ? null : detail.getLitePrimaryContact().getContLastName() + ", " + detail.getLitePrimaryContact().getContFirstName());
				case MAP_NUMBER_COLUMN:
				    return detail.getMapNumber();
				case DEVICE_NAME_COLUMN:
					return detail.getLitePaobject().getPaoName();
		        case DEVICE_TYPE_COLUMN:
		            return PAOGroups.getPAOTypeString(detail.getLitePaobject().getType());
				case METER_NUMBER_COLUMN:
				    return (detail.getLiteDeviceMeterNumber() == null ? null : detail.getLiteDeviceMeterNumber().getMeterNumber());
				case PHYSICAL_ADDRESS_COLUMN:
				    return String.valueOf(detail.getLitePaobject().getAddress());
				case ROUTE_NAME_OR_COLL_GROUP_COLUMN:	//TODO collectionGroup
					return PAOFuncs.getYukonPAOName(detail.getLitePaobject().getRouteID());
				case LAST_KWH_READING_COLUMN:
				    return detail.getMeterPointData().getValue();
				case DATE_TIME_COLUMN:
				    return detail.getMeterPointData().getTimeStamp();
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
/*		    if (Route)	//TODO
			{
				columnNames = new String[]{
				    ROUTE_NAME_STRING,
				    ACCOUNT_NUMBER_STRING, 
				    CONTACT_NAME_STRING,
				    MAP_NUMBER_STRING,
				    DEVICE_NAME_STRING,
				    DEVICE_TYPE_STRING,
				    PHYSICAL_ADDRESS_STRING,
				    METER_NUMBER_STRING,
				    COLLECTION_GROUP_STRING,
				    LAST_KWH_READING_STRING,
				    DATE_TIME_STRING
				};
		    }
*/		    
//		    else //if(getBillingGroupType() == DeviceMeterGroup.COLLECTION_GROUP)
		    {
				columnNames = new String[]{
				    COLLECTION_GROUP_STRING,
				    ACCOUNT_NUMBER_STRING, 
				    CONTACT_NAME_STRING,
				    MAP_NUMBER_STRING,
				    DEVICE_NAME_STRING,
				    DEVICE_TYPE_STRING,
				    PHYSICAL_ADDRESS_STRING,
				    METER_NUMBER_STRING,
				    ROUTE_NAME_STRING,
				    LAST_KWH_READING_STRING,
				    DATE_TIME_STRING
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
				String.class,
				String.class,
				Double.class,
				Date.class
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
				new ColumnProperties(offset, 1, 200, null),
				new ColumnProperties(offset, 1, offset+=60, null),
				new ColumnProperties(offset, 1, offset+=95, null),
				new ColumnProperties(offset, 1, offset+=70, null),
				new ColumnProperties(offset, 1, offset+=110, null),
				new ColumnProperties(offset, 1, offset+=60, null),
				new ColumnProperties(offset, 1, offset+=53, null),
				new ColumnProperties(offset, 1, offset+=53, null),
				new ColumnProperties(offset, 1, offset+=80, null),
				new ColumnProperties(offset, 1, offset+=66, columnValueFormat),
				new ColumnProperties(offset, 1, offset+=85, columnDateTimeFormat)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
	    String title = "Stars AMR Detail - ";
		if( getFilterModelType() == ModelFactory.COLLECTIONGROUP)
		    title += " By Collection Group";
		else if( getFilterModelType() == ModelFactory.TESTCOLLECTIONGROUP)
		    title += " By Alternate Group";
		else if( getFilterModelType() == ModelFactory.BILLING_GROUP)
		    title += " By Billing Group";
//		else if( ROUTE)
//			title += " By Route";
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
			case ORDER_BY_ACCOUNT_NUMBER:
				return "Account Number";
			case ORDER_BY_DEVICE_NAME:
				return "Device Name";
			case ORDER_BY_DEVICE_TYPE:
			    return "Device Type";
			case ORDER_BY_LAST_NAME:
			    return "Last Name";
			case ORDER_BY_MAP_NUMBER:
			    return "Map Number";
			case ORDER_BY_METER_NUMBER:
			    return "Meter Number";
			case ORDER_BY_PHYSICAL_ADDRESS:
			    return "Physical Address";
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
	
	/**
	 * Override ReportModelBase in order to reset the column headings.
	 * @param i
	 */
	/* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.ReportModelBase#setFilterModelType(int)
     */
    public void setFilterModelType(int modelType)
    {
        if( getFilterModelType() != modelType)
            columnNames = null;
        super.setFilterModelType(modelType);
    } 
}

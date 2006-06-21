package com.cannontech.analysis.tablemodel;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
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
	public final static int GROUP_NAME_1_OR_TIMESTAMP_COLUMN = 6;
	public final static int GROUP_NAME_2_OR_VALUE_COLUMN = 7;
	//Alternate columns for a successful meter report

	/** String values for column representation */
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String METER_NUMBER_STRING = "Meter Number";
	public final static String PHYSICAL_ADDRESS_STRING = "Address";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String ALT_GROUP_NAME_STRING = "Alternate\nGroup";
	public final static String BILLING_GROUP_NAME_STRING = "Billing\nGroup";
	//Alternate columns for a successful meter report
	public final static String TIMESTAMP_STRING= "Timestamp";
	public final static String VALUE_STRING = "Value";
	
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
	private static String ATT_POINT_TYPE = "pointType";
	private static final String ATT_ORDER_BY = "orderBy";
	
	public class MeterReadComparator implements Comparator, Serializable
	{
		public int compare(Object o1, Object o2){
	        LiteDeviceMeterNumber ldmn1 = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber( ((MeterAndPointData)o1).getPaobjectID().intValue());
		    LiteDeviceMeterNumber ldmn2 = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber( ((MeterAndPointData)o2).getPaobjectID().intValue());

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
			    LiteYukonPAObject pao1 = DaoFactory.getPaoDao().getLiteYukonPAO( ((MeterAndPointData)o1).getPaobjectID().intValue());
			    LiteYukonPAObject pao2 = DaoFactory.getPaoDao().getLiteYukonPAO( ((MeterAndPointData)o2).getPaobjectID().intValue());
			    
			    if( getOrderBy() == ORDER_BY_ROUTE_NAME)
			    {
			        thisVal = DaoFactory.getPaoDao().getYukonPAOName(pao1.getRouteID());
					anotherVal = DaoFactory.getPaoDao().getYukonPAOName(pao2.getRouteID());
			    }
			    else if( getOrderBy() == ORDER_BY_METER_NUMBER)
			    {
			        thisVal = (ldmn1 == null ? NULL_STRING : ldmn1.getMeterNumber());
					anotherVal = (ldmn2 == null ? NULL_STRING : ldmn2.getMeterNumber());
			    }
			    if (getOrderBy() == ORDER_BY_DEVICE_NAME || thisVal.equalsIgnoreCase(anotherVal))
			    {
			        thisVal = DaoFactory.getPaoDao().getYukonPAOName(pao1.getYukonID());
			        anotherVal = DaoFactory.getPaoDao().getYukonPAOName(pao2.getYukonID());
			        if( thisVal.equalsIgnoreCase(anotherVal))
			        {
					    		            
				        thisVal = DaoFactory.getPointDao().getPointName( ((MeterAndPointData)o1).getPointID().intValue());
				        anotherVal = DaoFactory.getPointDao().getPointName( ((MeterAndPointData)o2).getPointID().intValue());
			        }
			    }
			}
			return (thisVal.compareToIgnoreCase(anotherVal));
		}
		public boolean equals(Object obj){
			return false;
		}
	};
	
	public MeterReadComparator meterReadComparator = new MeterReadComparator();

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
    			ModelFactory.BILLING_GROUP
//				ModelFactory.MCT,
//				ModelFactory.METER
				} 
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
			Date ts = null;
			Double value = null;
			if (getMeterReadType()== SUCCESS_METER_READ_TYPE)
			{
			    Timestamp timestamp = rset.getTimestamp(3);
			    ts = new Date(timestamp.getTime());
			    value = new Double(rset.getDouble(4));
			}
			MeterAndPointData mpData = new MeterAndPointData(paobjectID, pointID, ts, value);

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
		StringBuffer sql = new StringBuffer	("SELECT DISTINCT PAO.PAOBJECTID, P.POINTID ");
		
		if( getMeterReadType() == SUCCESS_METER_READ_TYPE)
		    sql.append(", TIMESTAMP, VALUE ");
			
		sql.append(" FROM YUKONPAOBJECT PAO, POINT P ");

		if( getMeterReadType() == SUCCESS_METER_READ_TYPE)
		    sql.append(", RAWPOINTHISTORY RPH1");
		
		if( getBillingGroups() != null && getBillingGroups().length > 0)
		    sql.append(", DEVICEMETERGROUP DMG ");
			
		sql.append(" WHERE PAO.PAOCLASS = '" + DeviceClasses.STRING_CLASS_CARRIER + "' " +
		" AND P.PAOBJECTID = PAO.PAOBJECTID " +		        
		//  Select only billing points
		" AND (P.POINTTYPE = 'PulseAccumulator' AND P.POINTOFFSET IN (1, 2, 3) " +
		"      OR P.POINTTYPE = 'Analog' AND P.POINTOFFSET IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 21, 22, 23, 24, 25, 26, 27, 28) ) ");		    
			
//		Use paoIDs in query if they exist			
		if( getPaoIDs() != null && getPaoIDs().length > 0)
		{
			sql.append(" AND PAO.PAOBJECTID IN (" + getPaoIDs()[0]);
			for (int i = 1; i < getPaoIDs().length; i++)
				sql.append(", " + getPaoIDs()[i]);
			sql.append(") ");
		}
		
		if( getBillingGroups() != null && getBillingGroups().length > 0)
		{
			sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID " +
			        " AND " + getBillingGroupDatabaseString(getFilterModelType()) + " IN ( '" + getBillingGroups()[0]);			        
			for (int i = 1; i < getBillingGroups().length; i++)
				sql.append("', '" + getBillingGroups()[i]);
			sql.append("') ");
		}

		if( getMeterReadType() == SUCCESS_METER_READ_TYPE)
		    sql.append( " AND RPH1.POINTID = P.POINTID " +
		    		" AND RPH1.TIMESTAMP = (SELECT MAX(RPH2.TIMESTAMP) FROM RAWPOINTHISTORY RPH2" +
		    		" WHERE RPH1.POINTID = RPH2.POINTID " +
		    		" AND TIMESTAMP > ? AND TIMESTAMP <= ?)" +
		    		" AND RPH1.POINTID = P.POINTID ");
		    		 
		else if (getMeterReadType() == MISSED_METER_READ_TYPE)
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
		CTILogger.info("SQL for MeterReadModel: " + sql.toString());
		
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
			LiteYukonPAObject lPao = DaoFactory.getPaoDao().getLiteYukonPAO(mpData.getPaobjectID().intValue());
			LiteDeviceMeterNumber ldmn = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(mpData.getPaobjectID().intValue());
			switch( columnIndex)
			{
				case SORT_BY_GROUP_NAME_COLUMN:
				{
				    if( ldmn == null)
				        return null;
				    if( getFilterModelType() == ModelFactory.TESTCOLLECTIONGROUP)
				        return ldmn.getTestCollGroup();
				    else if( getFilterModelType() == ModelFactory.BILLING_GROUP)
				        return ldmn.getBillGroup();
				    else //if( getFilterModelType() == ModelFactory.COLLECTION_GROUP)
				        return ldmn.getCollGroup();
				}
				case DEVICE_NAME_COLUMN:
					return lPao.getPaoName();
					
				case METER_NUMBER_COLUMN:
				    return ( ldmn == null ? null : ldmn.getMeterNumber());
				    
				case PHYSICAL_ADDRESS_COLUMN:
				    return String.valueOf(lPao.getAddress());
				    
				case POINT_NAME_COLUMN:
					return DaoFactory.getPointDao().getPointName(mpData.getPointID().intValue());
	
				case ROUTE_NAME_COLUMN:
					return DaoFactory.getPaoDao().getYukonPAOName(lPao.getRouteID());
					
				case GROUP_NAME_1_OR_TIMESTAMP_COLUMN:
				{
				    if( getMeterReadType() == SUCCESS_METER_READ_TYPE)
				        return mpData.getTimeStamp();
				        
				    if( ldmn == null)
				        return null;
				    if( getFilterModelType() == ModelFactory.COLLECTIONGROUP)
				        return ldmn.getTestCollGroup();
				    else 
				        return ldmn.getCollGroup();
				}				    
				case GROUP_NAME_2_OR_VALUE_COLUMN:
				{
				    if (getMeterReadType()== SUCCESS_METER_READ_TYPE)
				        return mpData.getValue();
				    
				    if( ldmn == null)
				        return null;
				    if( getFilterModelType() == ModelFactory.BILLING_GROUP)
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
		    String tempStr1;
		    String tempStr2;
		    String tempStr3;
		    
		    if(getFilterModelType() == ModelFactory.TESTCOLLECTIONGROUP)
		    {
		        tempStr1 = ALT_GROUP_NAME_STRING;
		    	tempStr2 = COLL_GROUP_NAME_STRING;
		    	tempStr3 = BILLING_GROUP_NAME_STRING;
		    }
			else if(getFilterModelType() == ModelFactory.BILLING_GROUP)
			{
			    tempStr1 = BILLING_GROUP_NAME_STRING;
		        tempStr2 = ALT_GROUP_NAME_STRING;
		    	tempStr3 = COLL_GROUP_NAME_STRING;
		    }
			else //if(getFilterModelType() == ModelFactory.COLLECTIONGROUP)
			{
			    tempStr1 = COLL_GROUP_NAME_STRING;
		        tempStr2 = ALT_GROUP_NAME_STRING;
		        tempStr3 = BILLING_GROUP_NAME_STRING;
		    }			
		    
		    //Reupdate the string values if success meter model
		    if( getMeterReadType() == SUCCESS_METER_READ_TYPE)
		    {
		        tempStr2 = TIMESTAMP_STRING;
		        tempStr3 = VALUE_STRING;
		    }

		    columnNames = new String[]{
				tempStr1,
				DEVICE_NAME_STRING,
				METER_NUMBER_STRING,
				PHYSICAL_ADDRESS_STRING,
				POINT_NAME_STRING,
				ROUTE_NAME_STRING,
				tempStr2,
				tempStr3
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
		    if( getMeterReadType() == SUCCESS_METER_READ_TYPE)
		    {
				columnTypes = new Class[]{
					String.class,
					String.class,
					String.class,
					String.class,
					String.class,
					String.class,
					Date.class,
					Double.class
				};
		    }
		    else
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
		    if (getMeterReadType() == SUCCESS_METER_READ_TYPE)
		    {
				columnProperties = new ColumnProperties[]{
					//posX, posY, width, height, numberFormatString
					new ColumnProperties(0, 1, 200, null),
					new ColumnProperties(0, 1, 180, null),
					new ColumnProperties(180, 1, 65, null),
					new ColumnProperties(245, 1, 60, null),
					new ColumnProperties(305, 1, 90, null),
					new ColumnProperties(395, 1, 165, null),
					new ColumnProperties(560, 1, 92, "MM/dd/yyyy HH:mm:ss"),
					new ColumnProperties(652, 1, 75, "0.000")
				};
		    }
		    else
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
			title += "Successful ";
		else if( getMeterReadType() ==  MISSED_METER_READ_TYPE)
    	    title += "Missed ";
	    	    
		title += "Meter Data";
		if( getFilterModelType() == ModelFactory.COLLECTIONGROUP)
		    title += " By Collection Group";
		else if( getFilterModelType() == ModelFactory.TESTCOLLECTIONGROUP)
		    title += " By Alternate Group";
		else if( getFilterModelType() == ModelFactory.BILLING_GROUP)
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
				return "Device Name";
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
		if( meterReadType != i)
		{	//reset the fields that depend on this type.
		   columnProperties = null;
		   columnNames = null;
		   columnTypes = null;
		}		
		meterReadType = i;
	}

	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
		/*html += "    <td valign='top' align='center'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td valign='top' class='TitleHeader'>Point Type</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='radio' name='" + ATT_POINT_TYPE +"' value='kWh' checked disabled>kWh" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
*/
		html += "    <td valign='top' align='center'>" + LINE_SEPARATOR;		
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


		html += "    <td valign='middle'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td>* Click this button to generate a list of missed meters that MACS can process.</td>"+ LINE_SEPARATOR;		
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='button' name='GenerateMissedList' value='Generate Missed List' onclick='document.reportForm.ACTION.value=\"GenerateMissedMeterList\";reportForm.submit();'>"+ LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
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
	public void setFilterModelType(int billGroupType)
	{
		if( getFilterModelType() != billGroupType)
			columnNames = null;
	    super.setFilterModelType(billGroupType);
	}

}

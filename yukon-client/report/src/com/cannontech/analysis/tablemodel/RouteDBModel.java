package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.DeviceFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Created on May 17, 2005
 * @author snebben
 * RouteModel TableModel object
 * Innerclass object for row data is CarrierData:
 *  String paoName			- YukonPaobject.paoName (device)
 *  String paoType			- YukonPaobject.type
 *  String address			- DeviceCarrierSettings.address
 *  String routeName		- YukonPaobject.paoName (route)
 *  String collGroup		- DeviceMeterGroup.collectionGroup
 *  String testCollGroup	- DeviceMeterGroup.testCollectionGroup
 */
public class RouteDBModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;
	
	/** Enum values for column representation */
	public final static int ROUTE_NAME_COLUMN = 0;
	public final static int METER_NAME_COLUMN = 1;
	public final static int METER_NUMBER_COLUMN = 2;
	public final static int ADDRESS_COLUMN = 3;	
	public final static int PAO_TYPE_COLUMN = 4;
	public final static int COLL_GROUP_NAME_COLUMN = 5;
	public final static int TEST_COLL_GROUP_NAME_COLUMN = 6;
	
	/** String values for column representation */
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String METER_NAME_STRING = "Meter Name";
	public final static String METER_TYPE_STRING = "Type";
	public final static String METER_NUMBER_STRING = "Meter #";
	public final static String ADDRESS_STRING  = "Address";
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String TEST_COLL_GROUP_NAME_STRING = "Alternate Group";
	
	public static final int ORDER_BY_METER_NAME = 0;
	public static final int ORDER_BY_METER_NUMBER = 1;
	public static final int ORDER_BY_COLL_GRP = 2;
	private int orderBy = ORDER_BY_METER_NAME;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_METER_NAME, ORDER_BY_METER_NUMBER, ORDER_BY_COLL_GRP
	};

	private static final String ATT_ORDER_BY = "orderBy";
	
	/** A string for the title of the data */
	private static String title = "Database Report";
		
	public Comparator routeDBComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2){

		    LiteYukonPAObject pao1 = PAOFuncs.getLiteYukonPAO( ((Integer)o1).intValue());
		    LiteYukonPAObject pao2 = PAOFuncs.getLiteYukonPAO( ((Integer)o2).intValue());
	        
		    //Always sort by routeName first
		    String thisVal = PAOFuncs.getYukonPAOName(pao1.getRouteID());
		    String anotherVal = PAOFuncs.getYukonPAOName(pao2.getRouteID());
			
			if( thisVal.equalsIgnoreCase(anotherVal))
			{
			    LiteDeviceMeterNumber ldmn1 = DeviceFuncs.getLiteDeviceMeterNumber( ((Integer)o1).intValue());
			    LiteDeviceMeterNumber ldmn2 = DeviceFuncs.getLiteDeviceMeterNumber( ((Integer)o2).intValue());
				    
			    if( getOrderBy() == ORDER_BY_COLL_GRP)
			    {
			        thisVal = (ldmn1 == null ? NULL_STRING : ldmn1.getCollGroup());
			        anotherVal = (ldmn2 == null ? NULL_STRING : ldmn2.getCollGroup());
			    }
			    else if( getOrderBy() == ORDER_BY_METER_NUMBER)
			    {
			        thisVal = (ldmn1 == null ? NULL_STRING : ldmn1.getMeterNumber());
					anotherVal = (ldmn2 == null ? NULL_STRING : ldmn2.getMeterNumber());
			    }
	
			    if (getOrderBy() == ORDER_BY_METER_NAME || thisVal.equalsIgnoreCase(anotherVal))
			    {
			        thisVal = PAOFuncs.getYukonPAOName(pao1.getYukonID());
			        anotherVal = PAOFuncs.getYukonPAOName(pao2.getYukonID());
				}
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
	public RouteDBModel()
	{
		super();
	}

	/**
	 * Add Integer (paobjectID) objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			Integer paobjectID = new Integer(rset.getInt(1));
			getData().add(paobjectID);
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
		StringBuffer sql = new StringBuffer	("SELECT PAO1.PAOBJECTID, PAO2.PAONAME ROUTE " + 
			" FROM YUKONPAOBJECT PAO1, YUKONPAOBJECT PAO2, DEVICEROUTES DR "+
			" WHERE PAO1.PAOBJECTID = DR.DEVICEID " + 
			" AND PAO2.PAOBJECTID = DR.ROUTEID ");

			//Use paoIDs in query if they exist, ROUTE IDS!!!
			if( getPaoIDs() != null && getPaoIDs().length > 0)
			{
				sql.append(" AND PAO2.PAOBJECTID IN (" + getPaoIDs()[0]);
				for (int i = 1; i < getPaoIDs().length; i++)
					sql.append(", " + getPaoIDs()[i]);
				sql.append(") ");
			}
			
			sql.append(" ORDER BY PAO2.PAONAME " );	//All ordering done by RouteName first, rest of ordering is configurable
			
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
					Collections.sort(getData(), routeDBComparator);
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
		if ( o instanceof Integer)	//Integer is the PaobjectID
		{
		    LiteYukonPAObject lPao = PAOFuncs.getLiteYukonPAO(((Integer)o).intValue());
		    LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber( ((Integer)o).intValue());		    
			switch( columnIndex)
			{
				case METER_NAME_COLUMN:
					return lPao.getPaoName();
		
				case PAO_TYPE_COLUMN:
					return PAOGroups.getPAOTypeString(lPao.getType());

				case METER_NUMBER_COLUMN:
				    return (ldmn == null ? NULL_STRING : ldmn.getMeterNumber());
				    
				case ADDRESS_COLUMN:
					return String.valueOf(lPao.getAddress());
	
				case ROUTE_NAME_COLUMN:
					return PAOFuncs.getYukonPAOName(lPao.getRouteID());
				
				case COLL_GROUP_NAME_COLUMN:
					return (ldmn == null ? NULL_STRING : ldmn.getCollGroup());
				
				case TEST_COLL_GROUP_NAME_COLUMN:
					return (ldmn == null ? NULL_STRING : ldmn.getTestCollGroup());
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
			    ROUTE_NAME_STRING,
				METER_NAME_STRING,
				METER_NUMBER_STRING,
				ADDRESS_STRING,
				METER_TYPE_STRING,				
				COLL_GROUP_NAME_STRING,
				TEST_COLL_GROUP_NAME_STRING,
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
				new ColumnProperties(0, 1, 200, null),
				new ColumnProperties(0, 1, 200, null),
				new ColumnProperties(200, 1, 100, null),
				new ColumnProperties(300, 1, 100, null),
				new ColumnProperties(400, 1, 100, null),
				new ColumnProperties(500, 1, 100, null),
				new ColumnProperties(600, 1, 100, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title + " - Routes";
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
			case ORDER_BY_METER_NAME:
				return "Meter Name";
			case ORDER_BY_METER_NUMBER:
				return "Meter Number";
			case ORDER_BY_COLL_GRP:
			    return "Collection Group";
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
				setOrderBy(ORDER_BY_METER_NAME);			
		}
	}
}

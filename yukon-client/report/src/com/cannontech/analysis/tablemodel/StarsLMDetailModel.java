package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.stars.StarsLMDetail;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;

/**
 * Created on May 20, 2005
 * @author snebben
 */
public class StarsLMDetailModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 11;
	
	/** Enum values for column representation */
	public final static int GROUP_NAME_COLUMN = 0;
	public final static int GROUP_CAPACITY_COLUMN = 1;
	public final static int ACCOUNT_NUMBER_COLUMN = 2;
	public final static int CONTACT_LAST_NAME_COLUMN = 3;
	public final static int CONTACT_FIRST_NAME_COLUMN = 4;
	public final static int MAP_NUMBER_COLUMN = 5;
	public final static int SERIAL_NUMBER_COLUMN = 6;
	public final static int DEVICE_TYPE_COLUMN = 7;
	public final static int APPLIANCE_TYPE_COLUMN = 8;
	public final static int APPLIANCE_KW_CAPACITY_COLUMN = 9;
	public final static int CURRENT_STATE_COLUMN = 10;

	/** String values for column representation */
	public final static String GROUP_NAME_STRING = "Group Name";
	public final static String GROUP_CAPACITY_STRING = "Group\r\nCapacity";
	public final static String ACCOUNT_NUMBER_STRING = "Account #";
	public final static String CONTACT_LAST_NAME_STRING = "Last Name";
	public final static String CONTACT_FIRST_NAME_STRING = "First Name";
	public final static String MAP_NUMBER_STRING = "Map #";
	public final static String SERIAL_NUMBER_STRING = "Serial #";
	public final static String DEVICE_TYPE_STRING = "Device";
	public final static String APPLIANCE_TYPE_STRING = "Appliance";
	public final static String APPLIANCE_KW_CAPACITY_STRING = "kW Capacity";
	public final static String CURRENT_STATE_STRING = "Status";

	/** Class fields */
//	public static final int ORDER_BY_GROUP_NAME = 0;
//	public static final int ORDER_BY_GROUP_CAPACITY = 1;
	public static final int ORDER_BY_ACCOUNT_NUMBER = 0;
	public static final int ORDER_BY_LAST_NAME = 1;
	public static final int ORDER_BY_MAP_NUMBER = 2;
	public static final int ORDER_BY_SERIAL_NUMBER = 3;
	public static final int ORDER_BY_STATE = 4;
	
	private int orderBy = ORDER_BY_ACCOUNT_NUMBER;	//default, after GroupName/Capacity 
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_ACCOUNT_NUMBER, ORDER_BY_LAST_NAME, ORDER_BY_MAP_NUMBER, ORDER_BY_SERIAL_NUMBER, ORDER_BY_STATE
	};

	private boolean showCapacity = false;
	
	//servlet attributes/parameter strings
	private static final String ATT_ORDER_BY = "orderBy";
	private static final String ATT_SHOW_CAPACITY = "showCapacity";

	public Comparator lmDetailComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2){

		    int tempOrderBy = getOrderBy();
		    //First by GroupName
	        String thisStrVal = PAOFuncs.getYukonPAOName(((StarsLMDetail)o1).getGroupID().intValue());
	        String anotherStrVal = PAOFuncs.getYukonPAOName(((StarsLMDetail)o2).getGroupID().intValue());
	        if ( thisStrVal.equalsIgnoreCase(anotherStrVal) )
	        {
			    if( getOrderBy() == ORDER_BY_STATE)
			    {
			        thisStrVal = YukonListFuncs.getYukonListName(((StarsLMDetail)o1).getEnrollStatus().intValue());
					anotherStrVal = YukonListFuncs.getYukonListName(((StarsLMDetail)o2).getEnrollStatus().intValue());
					if (thisStrVal.equalsIgnoreCase(anotherStrVal))
					    tempOrderBy = ORDER_BY_ACCOUNT_NUMBER;	//Need to order by accountNumber
			    }
			    if( tempOrderBy == ORDER_BY_ACCOUNT_NUMBER)
			    {
			        thisStrVal = ((StarsLMDetail)o1).getAccountNumber();
					anotherStrVal = ((StarsLMDetail)o2).getAccountNumber();
					if (thisStrVal.equalsIgnoreCase(anotherStrVal))
					    tempOrderBy = ORDER_BY_LAST_NAME;	//Need to order by lastName
			    }
			    if( tempOrderBy == ORDER_BY_LAST_NAME )
			    {
			        thisStrVal = ((StarsLMDetail)o1).getContLastName();
					anotherStrVal = ((StarsLMDetail)o2).getContLastName();
					if ( thisStrVal.equalsIgnoreCase(anotherStrVal))
					{
					    thisStrVal = ((StarsLMDetail)o1).getContFirstName();
					    anotherStrVal = ((StarsLMDetail)o2).getContFirstName();
					    if (thisStrVal.equalsIgnoreCase(anotherStrVal))					    
					        tempOrderBy = ORDER_BY_SERIAL_NUMBER;	//Need to order by serialNumber
					}
			    }
			    if( getOrderBy() == ORDER_BY_SERIAL_NUMBER)
			    {
			        thisStrVal = ((StarsLMDetail)o1).getSerialNumber();
					anotherStrVal = ((StarsLMDetail)o2).getSerialNumber();
			    }
			    if( getOrderBy() == ORDER_BY_MAP_NUMBER)
			    {
			        thisStrVal = ((StarsLMDetail)o1).getMapNumber();
					anotherStrVal = ((StarsLMDetail)o2).getMapNumber();
			    }
	        }
	        return ( thisStrVal.compareToIgnoreCase(anotherStrVal));
		}
		public boolean equals(Object obj){
			return false;
		}
	};

	/**
	 * 
	 */
	public StarsLMDetailModel()
	{
		super();
	}
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
	    try
        {
            Integer groupID = new Integer(rset.getInt(1));
            Integer groupCapacity = new Integer(rset.getInt(2));
    	    String accountNumber = rset.getString(3);
    	    String lastName = rset.getString(4);
    	    String firstName = rset.getString(5);
    	    String siteNumber = rset.getString(6);
    	    String serialNumber = rset.getString(7);
    	    Integer hardwareType = new Integer(rset.getInt(8));
    	    Integer hardwareCapacity = new Integer(rset.getInt(9));
    	    Integer applianceType = new Integer(rset.getInt(10));
    	    Integer enrollmentActionID = new Integer(rset.getInt(11));
    	    
    	    StarsLMDetail details = new StarsLMDetail(groupID, groupCapacity, accountNumber, lastName, firstName, siteNumber, serialNumber, hardwareType, applianceType, hardwareCapacity, enrollmentActionID);
    	    getData().add(details);            
        }
        catch (SQLException e)
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
		StringBuffer sql = new StringBuffer	("SELECT LMG.DEVICEID, LMG.KWCAPACITY, ACCOUNTNUMBER," +
				" CONTLASTNAME, CONTFIRSTNAME, SITE.SITENUMBER, HARD.MANUFACTURERSERIALNUMBER, " +
				" HARD.LMHARDWARETYPEID, AB.KWCAPACITY, AC.CATEGORYID, LMCEB.ACTIONID " +
				" FROM CUSTOMERACCOUNT CA, CONTACT CONT, CUSTOMER CUST, ACCOUNTSITE SITE, LMHARDWAREBASE HARD, " +
				" INVENTORYBASE IB, APPLIANCEBASE AB, LMHARDWARECONFIGURATION LMHC, LMGROUP LMG, LMCUSTOMEREVENTBASE LMCEB, APPLIANCECATEGORY AC " +
				" WHERE CA.CUSTOMERID = CUST.CUSTOMERID " +
				" AND CUST.PRIMARYCONTACTID = CONT.CONTACTID " +
				" AND CA.ACCOUNTSITEID = SITE.ACCOUNTSITEID " +
				" AND HARD.INVENTORYID = IB.INVENTORYID " +
				" AND IB.ACCOUNTID = CA.ACCOUNTID " +
				" AND LMHC.INVENTORYID = HARD.INVENTORYID " +
				" AND LMHC.APPLIANCEID = AB.APPLIANCEID " +
				" AND LMG.DEVICEID = LMHC.ADDRESSINGGROUPID " +
				" AND AB.APPLIANCECATEGORYID = AC.APPLIANCECATEGORYID " +
				" AND LMCEB.EVENTID = (SELECT MAX(EVENTID) FROM LMHARDWAREEVENT LMHE WHERE LMHC.INVENTORYID = LMHE.INVENTORYID) ") ;
		
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
//				Order the records
				Collections.sort(getData(), lmDetailComparator);
				if( getSortOrder() == DESCENDING)
				    Collections.reverse(getData());				
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
	     SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy  HH:mm:ss");
		 return (dateTimeFormat.format(new Date()));
	 }
	 
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof StarsLMDetail)
		{
			StarsLMDetail details = ((StarsLMDetail)o);
			switch( columnIndex)
			{
				case GROUP_NAME_COLUMN:
				    if( details.getGroupID() != null)
				        return PAOFuncs.getYukonPAOName(details.getGroupID().intValue());
				    break;
				case GROUP_CAPACITY_COLUMN:
					return details.getGroupCapacity();
				case ACCOUNT_NUMBER_COLUMN:
				    return details.getAccountNumber();
				case CONTACT_LAST_NAME_COLUMN:
				    return details.getContLastName();
				case CONTACT_FIRST_NAME_COLUMN:
					return details.getContFirstName();
				case MAP_NUMBER_COLUMN:
					return details.getMapNumber();
				case SERIAL_NUMBER_COLUMN:
				    return details.getSerialNumber();
				case DEVICE_TYPE_COLUMN:
				    if( details.getDeviceType() != null)
				        return YukonListFuncs.getYukonListEntry(details.getDeviceType().intValue());
				    break;
				case APPLIANCE_TYPE_COLUMN:
				    if (details.getApplianceType() != null)
					    return YukonListFuncs.getYukonListEntry(details.getApplianceType().intValue());
				    break;
				case APPLIANCE_KW_CAPACITY_COLUMN:
				    return details.getApplianceCapacity();
				case CURRENT_STATE_COLUMN:
				    if( details.getEnrollStatus() != null )
				        return YukonListFuncs.getYukonListEntry(details.getEnrollStatus().intValue());
				    break;
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
				GROUP_NAME_STRING,
				GROUP_CAPACITY_STRING,
				ACCOUNT_NUMBER_STRING,
				CONTACT_LAST_NAME_STRING,
				CONTACT_FIRST_NAME_STRING,
				MAP_NUMBER_STRING,
				SERIAL_NUMBER_STRING,
				DEVICE_TYPE_STRING,
				APPLIANCE_TYPE_STRING,
				APPLIANCE_KW_CAPACITY_STRING,
				CURRENT_STATE_STRING
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
				Integer.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				Integer.class,
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
				new ColumnProperties(80, 1, 150, null),
				new ColumnProperties(80, 1, 150, "0 kW"),
				new ColumnProperties(offset, 1, offset+=80, null),
				new ColumnProperties(offset, 1, offset+=80, null),
				new ColumnProperties(offset, 1, offset+=80, null),
				new ColumnProperties(offset, 1, offset+=80, null),
				new ColumnProperties(offset, 1, offset+=80, null),
				new ColumnProperties(offset, 1, offset+=80, null),
				new ColumnProperties(offset, 1, offset+=80, null),
				new ColumnProperties(offset, 1, offset+=60, "0"),
				new ColumnProperties(offset, 1, offset+=80, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return "Load Management Details";
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
			case ORDER_BY_LAST_NAME:
				return "Contact Last Name";
			case ORDER_BY_MAP_NUMBER:
			    return "Map Number";
			case ORDER_BY_SERIAL_NUMBER:
			    return "Serial Number";
			case ORDER_BY_STATE:
			    return "Enrollment State";
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

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;		
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Sort By Load Group</td>" +LINE_SEPARATOR;
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

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Display Options</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' name='" +ATT_SHOW_CAPACITY + "'>Display Appliance Info"+ LINE_SEPARATOR;
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
			String param = req.getParameter(ATT_ORDER_BY);
			if( param != null)
				setOrderBy(Integer.valueOf(param).intValue());
			else
				setOrderBy(ORDER_BY_ACCOUNT_NUMBER);
			
			param = req.getParameter(ATT_SHOW_CAPACITY);
			setShowCapacity(param != null);
		}
	}
	
    /**
     * @return Returns the showCapacity.
     */
    public boolean isShowCapacity()
    {
        return showCapacity;
    }
    /**
     * @param showCapacity The showCapacity to set.
     */
    public void setShowCapacity(boolean showCapacity)
    {
        this.showCapacity = showCapacity;
    }
}

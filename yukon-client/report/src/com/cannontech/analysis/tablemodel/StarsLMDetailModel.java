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
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.spring.YukonSpringHook;

/**
 * Created on May 20, 2005
 * @author snebben
 */
public class StarsLMDetailModel extends ReportModelBase<StarsLMDetail>
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
	public final static String GROUP_CAPACITY_STRING = "Group Capacity";
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

	private Comparator<StarsLMDetail> lmDetailComparator = new Comparator<StarsLMDetail>()
	{
		public int compare(StarsLMDetail o1, StarsLMDetail o2){

		    int tempOrderBy = getOrderBy();
		    //First by GroupName
	        String thisStrVal = o1.getGroupName();
	        String anotherStrVal = o2.getGroupName();
	        if ( thisStrVal.equalsIgnoreCase(anotherStrVal) )
	        {
			    if( getOrderBy() == ORDER_BY_STATE)
			    {
			        thisStrVal = YukonDefinition.getById(o1.getEnrollStatus()).getRelevantList().getListName();
			        anotherStrVal = YukonDefinition.getById(o2.getEnrollStatus()).getRelevantList().getListName();
					if (thisStrVal.equalsIgnoreCase(anotherStrVal))
					    tempOrderBy = ORDER_BY_ACCOUNT_NUMBER;	//Need to order by accountNumber
			    }
			    if( tempOrderBy == ORDER_BY_ACCOUNT_NUMBER)
			    {
			        thisStrVal = o1.getAccountNumber();
					anotherStrVal = o2.getAccountNumber();
					if (thisStrVal.equalsIgnoreCase(anotherStrVal))
					    tempOrderBy = ORDER_BY_LAST_NAME;	//Need to order by lastName
			    }
			    if( tempOrderBy == ORDER_BY_LAST_NAME )
			    {
			        thisStrVal = o1.getContLastName();
					anotherStrVal = o2.getContLastName();
					if ( thisStrVal.equalsIgnoreCase(anotherStrVal))
					{
					    thisStrVal = o1.getContFirstName();
					    anotherStrVal = o2.getContFirstName();
					    if (thisStrVal.equalsIgnoreCase(anotherStrVal))					    
					        tempOrderBy = ORDER_BY_SERIAL_NUMBER;	//Need to order by serialNumber
					}
			    }
			    if( getOrderBy() == ORDER_BY_SERIAL_NUMBER)
			    {
			        thisStrVal = o1.getSerialNumber();
					anotherStrVal = o2.getSerialNumber();
			    }
			    if( getOrderBy() == ORDER_BY_MAP_NUMBER)
			    {
			        thisStrVal = o1.getMapNumber();
					anotherStrVal = o2.getMapNumber();
			    }
	        }
	        return ( thisStrVal.compareToIgnoreCase(anotherStrVal));
		}
		public boolean equals(Object obj){
			return false;
		}
	};

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
            String groupName = rset.getString(12);
    	    
    	    StarsLMDetail details = new StarsLMDetail(groupName, groupID, groupCapacity, accountNumber, 
                                                      lastName, firstName, siteNumber, serialNumber, hardwareType, 
                                                      applianceType, hardwareCapacity, enrollmentActionID);
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
				" HARD.LMHARDWARETYPEID, AB.KWCAPACITY, AC.CATEGORYID, LMCEB.ACTIONID, Grp.PAONAME " +
				" FROM CUSTOMERACCOUNT CA, CONTACT CONT, CUSTOMER CUST, ACCOUNTSITE SITE, LMHARDWAREBASE HARD, " +
				" INVENTORYBASE IB, APPLIANCEBASE AB, LMHARDWARECONFIGURATION LMHC, LMGROUP LMG, LMCUSTOMEREVENTBASE LMCEB, " +
                " APPLIANCECATEGORY AC, YUKONPAOBJECT Grp, ECTOACCOUNTMAPPING ETAM " +
				" WHERE CA.CUSTOMERID = CUST.CUSTOMERID " +
				" AND CUST.PRIMARYCONTACTID = CONT.CONTACTID " +
				" AND CA.ACCOUNTSITEID = SITE.ACCOUNTSITEID " +
				" AND HARD.INVENTORYID = IB.INVENTORYID " +
				" AND IB.ACCOUNTID = CA.ACCOUNTID " +
				" AND CA.ACCOUNTID = ETAM.ACCOUNTID " +
				" AND LMHC.INVENTORYID = HARD.INVENTORYID " +
				" AND LMHC.APPLIANCEID = AB.APPLIANCEID " +
				" AND LMG.DEVICEID = LMHC.ADDRESSINGGROUPID " +
				" AND AB.APPLIANCECATEGORYID = AC.APPLIANCECATEGORYID " +
                " AND LMG.DEVICEID = Grp.PAOBJECTID " +
				" AND LMCEB.EVENTID = (SELECT MAX(EVENTID) FROM LMHARDWAREEVENT LMHE WHERE LMHC.INVENTORYID = LMHE.INVENTORYID) " +
				" AND ETAM.ENERGYCOMPANYID = " + this.getEnergyCompanyID() +
				" AND LMG.DEVICEID != 0");
		
		return sql;
	}
	
	@Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
		
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
			SqlUtils.close(rset, pstmt, conn );
		}
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
	@Override
	 public String getDateRangeString()
	 {
	     SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy  HH:mm:ss z");
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
			        return details.getGroupName();
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
				        return YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(details.getDeviceType().intValue());
				    break;
				case APPLIANCE_TYPE_COLUMN:
				    if (details.getApplianceType() != null)
					    return YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(details.getApplianceType().intValue());
				    break;
				case APPLIANCE_KW_CAPACITY_COLUMN:
				    return details.getApplianceCapacity();
				case CURRENT_STATE_COLUMN:
				    if( details.getEnrollStatus() != null )
				        return YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry(details.getEnrollStatus().intValue());
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
	public Class<?>[] getColumnTypes()
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
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(80, 1, 150, null),
				new ColumnProperties(80, 1, 150, "0 kW"),
				new ColumnProperties(0, 1, 80, null),
				new ColumnProperties(80, 1, 80, null),
				new ColumnProperties(160, 1, 80, null),
				new ColumnProperties(240, 1, 80, null),
				new ColumnProperties(320, 1, 80, null),
				new ColumnProperties(400, 1, 80, null),
				new ColumnProperties(480, 1, 80, null),
				new ColumnProperties(560, 1, 60, "0"),
				new ColumnProperties(620, 1, 80, null)
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
	@Override
	public String getHTMLOptionsTable()
	{
		String html = "";
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='title-header'>&nbsp;Order By</td>" +LINE_SEPARATOR;
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
		html += "          <td class='title-header'>&nbsp;Sort By Load Group</td>" +LINE_SEPARATOR;
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
		html += "          <td class='title-header'>&nbsp;Display Options</td>" +LINE_SEPARATOR;
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
	@Override
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
    
    @Override
    public boolean useStartDate() {
        return false;
    }

    @Override
    public boolean useStopDate() {
        return false;
    }

}

package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.data.stars.StarsLMSummary;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

/**
 * Created on May 20, 2005
 * @author snebben
 */
public class StarsLMSummaryModel extends ReportModelBase<StarsLMSummary>
{
    private LiteYukonUser liteUser;
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 6;
	
	/** Enum values for column representation */
	public final static int GROUP_NAME_COLUMN = 0;
	public final static int GROUP_CAPACITY_COLUMN = 1;
	public final static int RECEIVERS_COUNT_COLUMN = 2;
	public final static int RECEIVERS_TOTAL_CAPACITY_COLUMN = 3;
	public final static int RECEIVERS_OUT_OF_SERVICE_COUNT_COLUMN = 4;
	public final static int ADJUSTED_TOTAL_CAPACITY_COLUMN = 5;

	public final static int RECEIVERS_COUNT_COLUMN_WITHOUT_CAPACTITY = 1;
	public final static int RECEIVERS_OUT_OF_SERVICE_COUNT_COLUMN_WITHOUT_CAPACITY = 2;
	
	/** String values for column representation */
	public final static String GROUP_NAME_STRING = "Group Name";
	public final static String GROUP_CAPACITY_STRING = "Group Capacity";
	public final static String RECEIVERS_COUNT_STRING = "Receivers In Group";
	public final static String RECEIVERS_TOTAL_CAPACITY_STRING = "Receivers Capacity";
	public final static String RECEIVERS_OUT_OF_SERVICE_COUNT_STRING = "Receivers Out-Of-Service";
	public final static String ADJUSTED_TOTAL_CAPACITY_STRING = "Adjusted Capacity";

	/** Class fields */
	public static final int ORDER_BY_GROUP_NAME = 0;
	public static final int ORDER_BY_GROUP_CAPACITY = 1;
	public static final int ORDER_BY_RECEIVERS_COUNT = 2;
	public static final int ORDER_BY_RECEIVERS_TOTAL_CAPACITY = 3;
	public static final int ORDER_BY_RECEIVERS_OUT_OF_SERVICE = 4;
	public static final int ORDER_BY_ADJUSTED_CAPACITY = 5;
	
	private int orderBy = ORDER_BY_GROUP_NAME;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_GROUP_NAME, ORDER_BY_GROUP_CAPACITY, ORDER_BY_RECEIVERS_COUNT,
		ORDER_BY_RECEIVERS_TOTAL_CAPACITY, ORDER_BY_RECEIVERS_OUT_OF_SERVICE, ORDER_BY_ADJUSTED_CAPACITY
	};

	private boolean showCapacity = false;
	
	//servlet attributes/parameter strings
	private static final String ATT_ORDER_BY = "orderBy";
	private static final String ATT_SHOW_CAPACITY = "showCapacity";

	public Comparator<StarsLMSummary> lmSummaryComparator = new Comparator<StarsLMSummary>()
	{
		public int compare(StarsLMSummary o1, StarsLMSummary o2){
		    int thisVal = -1;
		    int anotherVal = -1;
		    if( getOrderBy() == ORDER_BY_GROUP_CAPACITY)
		    {
		        thisVal = o1.getGroupCapacity().intValue();
				anotherVal = o2.getGroupCapacity().intValue();
		    }
		    else if( getOrderBy() == ORDER_BY_RECEIVERS_COUNT)
		    {
		        thisVal = o1.getNumberOfReceivers().intValue();
				anotherVal = o2.getNumberOfReceivers().intValue();
		    }

		    else if( getOrderBy() == ORDER_BY_RECEIVERS_OUT_OF_SERVICE)
		    {
		        thisVal = o1.getNumberOfReceiversOutOfService().intValue();
				anotherVal = o2.getNumberOfReceiversOutOfService().intValue();
		    }

		    else if( getOrderBy() == ORDER_BY_ADJUSTED_CAPACITY)
		    {
		        thisVal = o1.getAdjustedGroupCapacity().intValue();
				anotherVal = o2.getAdjustedGroupCapacity().intValue();
		    }

		    else if( getOrderBy() == ORDER_BY_RECEIVERS_TOTAL_CAPACITY)
		    {
		        thisVal = o1.getTotalReceiversOutOfServiceCapacity().intValue();
				anotherVal = o2.getTotalReceiversOutOfServiceCapacity().intValue();
		    }
		    if ( getOrderBy() == ORDER_BY_GROUP_NAME ||
		            thisVal == anotherVal)	//Sort by GroupName if other values are the same
		    {
		        String thisStrVal = o1.getGroupName();
		        String anotherStrVal = o2.getGroupName();
		        return ( thisStrVal.compareToIgnoreCase(anotherStrVal));
		    }
			return ( thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj){
			return false;
		}
	};

	/**
	 * 
	 */
	public StarsLMSummaryModel()
	{
		super();
	}
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
	    //NOT IMPLEMENTED, data row addition is handled in collectData() since we have to do some temporary data manipulation 
	}

	/**
	 * Build the SQL statement to retrieve MissedMeter data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT PAONAME, LMG.KWCAPACITY, SUM(AB.KWCAPACITY), COUNT(DISTINCT MANUFACTURERSERIALNUMBER), PAO.PAOBJECTID " +
		        " FROM YUKONPAOBJECT PAO, LMGROUP LMG, LMHARDWARECONFIGURATION LMHC, APPLIANCEBASE AB, LMHARDWAREBASE LMHB, ECTOINVENTORYMAPPING ETIM " +
		        " WHERE PAO.PAOBJECTID = LMG.DEVICEID " +
		        " AND LMHC.INVENTORYID = LMHB.INVENTORYID " +
		        " AND LMHB.INVENTORYID = ETIM.INVENTORYID " + 
		        " AND LMG.DEVICEID = LMHC.ADDRESSINGGROUPID " +
		        " AND LMHC.APPLIANCEID = AB.APPLIANCEID " + 
		        " AND ETIM.ENERGYCOMPANYID = " + this.getEnergyCompanyID() + 
		        " AND PAO.PAOBJECTID != 0 " + 
		        " GROUP BY PAONAME, LMG.KWCAPACITY, PAO.PAOBJECTID ");
		
		return sql;
	}
	
	@Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
		Map<Integer, StarsLMSummary> tempDataMap = new HashMap<Integer, StarsLMSummary>();
		
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
                List<LiteYukonPAObject> restrictedGroups = ReportFuncs.getRestrictedLMGroups(liteUser);
				while( rset.next())
				{
				    String groupName = rset.getString(1);
				    Integer groupCapacity = new Integer(rset.getInt(2));
				    Integer receiversCapactiy = new Integer(rset.getInt(3));
				    Integer numberReceivers = new Integer(rset.getInt(4));
				    Integer groupID = new Integer(rset.getInt(5));
				    
                    for(YukonPao restrictedGroup : restrictedGroups){ // skip rows of groups we aren't allowed to see.
                        if(restrictedGroup.getPaoIdentifier().getPaoId() == groupID){
                            StarsLMSummary summary = new StarsLMSummary(groupName, groupCapacity, numberReceivers, receiversCapactiy, null, null);
                            tempDataMap.put(groupID, summary);
                            break;
                        }
                    }
				}
				
				//Collect the hardware that is out of service, and the capacity of them
				//Out of Service is determined from the latest event from lmHardwareEvent and YukonListEntry's entrytext values.
				int previousPaoId = -1;
				int outOfServiceCount = 0;
				int outOfServiceCapacity = 0;
				int currentPaoId = -1;
				int actionID = -1; 
				int capacity = 0;
				sql = new StringBuffer("SELECT ADDRESSINGGROUPID, LMCEB.ACTIONID, KWCAPACITY " +
				        " FROM LMHARDWAREEVENT LMHE, LMCUSTOMEREVENTBASE LMCEB, LMHARDWARECONFIGURATION LMHC, APPLIANCEBASE AB, ECTOLMCUSTOMEREVENTMAPPING ETLCM " +
				        " WHERE LMHE.EVENTID = (SELECT MAX(EVENTID) FROM LMHARDWAREEVENT LMHE2 WHERE LMHE.INVENTORYID = LMHE2.INVENTORYID) " + 
				        " AND LMHE.EVENTID = LMCEB.EVENTID " +
				        " AND LMCEB.EVENTID = ETLCM.EVENTID " +
				        " AND LMHC.INVENTORYID = LMHE.INVENTORYID " + 
				        " AND AB.APPLIANCEID = LMHC.APPLIANCEID " +
				        " AND ETLCM.ENERGYCOMPANYID =  " + this.getEnergyCompanyID() +
				        " ORDER BY ADDRESSINGGROUPID ");
				
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
				while (rset.next())
				{
				    currentPaoId = rset.getInt(1);
				    actionID = rset.getInt(2);
				    capacity = rset.getInt(3);
				    
				    if( currentPaoId != previousPaoId)
				    {
				        if( previousPaoId > -1 ) //not the first time through
				        {
				            StarsLMSummary sum = tempDataMap.get(new Integer(previousPaoId));
				            if (sum != null)
				            {
				                sum.setNumberOfReceiversOutOfService(new Integer(outOfServiceCount));
				                sum.setTotalReceiversOutOfServiceCapacity(new Integer(outOfServiceCapacity));
				                tempDataMap.put(new Integer(previousPaoId), sum);
				            }
				        }
				        previousPaoId = currentPaoId;
				        outOfServiceCapacity = 0;
				        outOfServiceCount = 0;
				    }
	
					if (actionID == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ||
						actionID == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION)	//out of service
					{
					    outOfServiceCapacity += capacity;
					    outOfServiceCount++;
					}
				}
				//Save the last one!...but only if we had at least one entry in rset (hence, currentpaoId > default value 
			    if( currentPaoId > -1)
			    {
		            StarsLMSummary sum = tempDataMap.get(new Integer(previousPaoId));
		            if (sum != null)
		            {
		                sum.setNumberOfReceiversOutOfService(new Integer(outOfServiceCount));
		                sum.setTotalReceiversOutOfServiceCapacity(new Integer(outOfServiceCapacity));
		                tempDataMap.put(new Integer(previousPaoId), sum);
		            }
			    }
			}
			//Load the data field with the map values
			if( !tempDataMap.isEmpty())
			{
			    Iterator<Map.Entry<Integer, StarsLMSummary>> iter = tempDataMap.entrySet().iterator();
				while( iter.hasNext())
				{
				    getData().add( iter.next().getValue());
				}			    
				//Order the records
				Collections.sort(getData(), lmSummaryComparator);
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
	 
    /*
     * (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
     */
    public Object getAttribute(int columnIndex, Object o) {
        if (o instanceof StarsLMSummary) {
            StarsLMSummary summary = ((StarsLMSummary) o);
            if (isShowCapacity()) {
                switch (columnIndex) {
                case GROUP_NAME_COLUMN:
                    return summary.getGroupName();
                case GROUP_CAPACITY_COLUMN:
                    return summary.getGroupCapacity();
                case RECEIVERS_COUNT_COLUMN:
                    return summary.getNumberOfReceivers();
                case RECEIVERS_TOTAL_CAPACITY_COLUMN:
                    return summary.getTotalReceiversCapactiy();
                case RECEIVERS_OUT_OF_SERVICE_COUNT_COLUMN:
                    return summary.getNumberOfReceiversOutOfService();
                case ADJUSTED_TOTAL_CAPACITY_COLUMN:
                    return summary.getAdjustedGroupCapacity();
                }
            } else {    // these indexes should match the getColumnNames index.
                switch (columnIndex) {
                    case GROUP_NAME_COLUMN:
                        return summary.getGroupName();
                    case RECEIVERS_COUNT_COLUMN_WITHOUT_CAPACTITY:
                        return summary.getNumberOfReceivers();
                    case RECEIVERS_OUT_OF_SERVICE_COUNT_COLUMN_WITHOUT_CAPACITY:
                        return summary.getNumberOfReceiversOutOfService();
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getColumnNames()
     */
    public String[] getColumnNames() {
        if( columnNames == null) {
            if (isShowCapacity()) {
                columnNames = new String[]{
                    GROUP_NAME_STRING,
                    GROUP_CAPACITY_STRING,
                    RECEIVERS_COUNT_STRING,
                    RECEIVERS_TOTAL_CAPACITY_STRING,
                    RECEIVERS_OUT_OF_SERVICE_COUNT_STRING,
                    ADJUSTED_TOTAL_CAPACITY_STRING
                };
            } else {
                columnNames = new String[]{
                    GROUP_NAME_STRING,
                    RECEIVERS_COUNT_STRING,
                    RECEIVERS_OUT_OF_SERVICE_COUNT_STRING,
                };
            }
        }
        return columnNames;
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getColumnTypes()
     */
    public Class<?>[] getColumnTypes() {
        if (columnTypes == null) {
            if (isShowCapacity()) {
                columnTypes = new Class[] {
                    String.class, 
                    Integer.class, 
                    Integer.class, 
                    Integer.class, 
                    Integer.class,
                    Integer.class
                };
            } else {
                columnTypes = new Class[] {
                        String.class, 
                        Integer.class,
                        Integer.class
                    };
            }
        }
        return columnTypes;
    }

    /*
     * (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getColumnProperties()
     */
    public ColumnProperties[] getColumnProperties() {
        if (columnProperties == null) {
            if (isShowCapacity()) {
                columnProperties = new ColumnProperties[] {
                    // posX, posY, width, height, numberFormatString
                    new ColumnProperties(0, 1, 200, null),
                    new ColumnProperties(200, 1, 100, "0"),
                    new ColumnProperties(300, 1, 100, "0"),
                    new ColumnProperties(400, 1, 100, "0"),
                    new ColumnProperties(500, 1, 100, "0"),
                    new ColumnProperties(600, 1, 100, "0")
                };
            } else {
                columnProperties = new ColumnProperties[] {
                    // posX, posY, width, height, numberFormatString
                    new ColumnProperties(0, 1, 200, null),
                    new ColumnProperties(300, 1, 100, "0"),
                    new ColumnProperties(500, 1, 100, "0"),
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
		return "Load Management Summary";
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
			case ORDER_BY_GROUP_NAME:
				return "LM Group Name";
			case ORDER_BY_GROUP_CAPACITY:
				return "Group Capacity";
			case ORDER_BY_RECEIVERS_COUNT:
			    return "Receivers Count";
			case ORDER_BY_RECEIVERS_TOTAL_CAPACITY:
			    return "Receivers Total Capacity";
			case ORDER_BY_RECEIVERS_OUT_OF_SERVICE:
			    return "Receivers Out-Of-Service Count";
			case ORDER_BY_ADJUSTED_CAPACITY:
			    return "Adjusted Group Capacity";
			    
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
		html += "          <td><input type='checkbox' name='" +ATT_SHOW_CAPACITY + "'>Display Capacity Fields"+ LINE_SEPARATOR;
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
				setOrderBy(ORDER_BY_GROUP_NAME);
			
			param = req.getParameter(ATT_SHOW_CAPACITY);
			setShowCapacity(param != null);
			setLiteUser(ServletUtil.getYukonUser(req));
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
    
    public LiteYukonUser getLiteUser() {
        return liteUser;
    }
    public void setLiteUser(LiteYukonUser liteUser) {
        this.liteUser = liteUser;
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

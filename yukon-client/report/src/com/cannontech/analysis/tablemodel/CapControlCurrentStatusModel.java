package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.capcontrol.CapControlStatusData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.state.StateGroupUtils;

/**
 * Created on Nov 11, 2005
 * @author snebben
 */
public class CapControlCurrentStatusModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	public final static int SUB_BUS_NAME_COLUMN = 0;
	public final static int FEEDER_NAME_COLUMN = 1;
	public final static int CAP_BANK_NAME_COLUMN = 2;
	public final static int CONTROL_STATUS_COLUMN = 3;	
	public final static int LAST_STATUS_CHANGE_TIME_COLUMN = 4;
    public final static int OPERTATIONAL_STATE_COLUMN = 5;
    public final static int DISABLE_FLAG_COLUMN = 6;
	
	/** String values for column representation */
	public final static String CAP_BANK_NAME_STRING = "Cap Bank";
	public final static String SUB_BUS_NAME_STRING = "Substation Bus";
	public final static String FEEDER_NAME_STRING = "Cap Feeder";
	public final static String CONTROL_STATUS_STRING = "Status";
	public final static String LAST_STATUS_CHANGE_TIME_STRING  = "Status Changed Date/Time";
    public final static String OPERATIONAL_STATE_STRING = "Operational State";
    public final static String DISABLE_FLAG_STRING = "Disabled";
    
    public static final String[] ORDER_TYPE_STRINGS =
    {
        "Order by Substation",
        "Order by Disabled",
        "Order by Operation Method",
        "Order by Status",
        "Order by Status Changed Date/Time"
    };  
	
	private int[] controlStates = null;
	
	private static final String ATT_CAP_CONTROL_STATE = "capControlState";
	private static final String ATT_All_CAP_CONTROL_STATE = "capControlStateAll";
	
	private static final int ALL_CAP_CONTROL_STATES = -1;	//use some invalid number
    
    private String orderBy = null;

    protected static final String ATT_ORDER_BY = "orderBy";
	
	/** A string for the title of the data */
	private static String title = "Current Bank Status Report";
		
	public Comparator ccStatusDataComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2){

		    CapControlStatusData data1 = (CapControlStatusData)o1;
		    CapControlStatusData data2 = (CapControlStatusData)o2;
	        
            if( ORDER_TYPE_STRINGS[0].equals(getOrderBy()) ) {
    		    //Order by Sub Bus first
    		    String thisValStr = DaoFactory.getPaoDao().getYukonPAOName(data1.getSubBusPaoID().intValue());
    		    String anotherValStr = DaoFactory.getPaoDao().getYukonPAOName(data2.getSubBusPaoID().intValue());
    			
    			if( thisValStr.equalsIgnoreCase(anotherValStr))
    			{
    //				Order by Feeder
    				thisValStr = DaoFactory.getPaoDao().getYukonPAOName(data1.getFeederPaoID().intValue());
    				anotherValStr = DaoFactory.getPaoDao().getYukonPAOName(data2.getFeederPaoID().intValue());
    
    				if( thisValStr.equalsIgnoreCase(anotherValStr))
    				{
    					//Order by control Order
    					int thisVal = data1.getControlOrder().intValue();
    					int anotherVal = data2.getControlOrder().intValue();
    					return ( thisVal <anotherVal ? -1 : (thisVal ==anotherVal ? 0 : 1));
    				}
    			}
    			return (thisValStr.compareToIgnoreCase(anotherValStr));
            }else if( ORDER_TYPE_STRINGS[1].equals(getOrderBy()) ) {
                String val1 = data1.getDisableFlag();
                String val2 = data2.getDisableFlag();
                return (val1.compareToIgnoreCase(val2));
            }else if ( ORDER_TYPE_STRINGS[2].equals(getOrderBy()) ) {
                String val1 = data1.getOperationalState();
                String val2 = data2.getOperationalState();
                return (val1.compareToIgnoreCase(val2));
            }else if ( ORDER_TYPE_STRINGS[3].equals(getOrderBy()) ) {
                String val1 = DaoFactory.getStateDao().getLiteState(StateGroupUtils.STATEGROUPID_CAPBANK, data1.getControlStatus().intValue()).toString();
                String val2 = DaoFactory.getStateDao().getLiteState(StateGroupUtils.STATEGROUPID_CAPBANK, data2.getControlStatus().intValue()).toString();
                return (val1.compareToIgnoreCase(val2));
            }else {
                Date date1 = data1.getChangeDateTime();
                Date date2 = data2.getChangeDateTime();
                return (date1.compareTo(date2));
            }
		}
		public boolean equals(Object obj){
			return false;
		}
	};
	
	/**
	 * Default Constructor
	 */
	public CapControlCurrentStatusModel()
	{
		super();
		setFilterModelTypes(new ReportFilter[]{
		        ReportFilter.CAPCONTROLSUBBUS,
                ReportFilter.CAPCONTROLFEEDER,
                ReportFilter.AREA}
			);
	}

	/**
	 * Add Integer (paobjectID) objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	@SuppressWarnings("unchecked")
    public void addDataRow(ResultSet rset)
	{
		try
		{
			Integer capBankPaoID = new Integer(rset.getInt(1));
			Integer subBusPaoID = new Integer(rset.getInt(2));
			Integer feederPaoID = new Integer(rset.getInt(3));
			Integer controlStatus = new Integer(rset.getInt(4));
			java.sql.Timestamp lastChangedateTime = rset.getTimestamp(5);
            String operationalState = rset.getString(6);
			Integer controlOrder = new Integer(rset.getInt(7));
            LiteYukonPAObject lite = DaoFactory.getPaoDao().getLiteYukonPAO(capBankPaoID.intValue());
            String disableFlag = lite.getDisableFlag();
			
			CapControlStatusData ccStatusData= new CapControlStatusData(
			        capBankPaoID, subBusPaoID, feederPaoID,
			        controlStatus, new Date(lastChangedateTime.getTime()), operationalState, disableFlag, controlOrder);
			getData().add(ccStatusData);
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
		StringBuffer sql = new StringBuffer	("SELECT DCC.CAPBANKID, CCF.SUBSTATIONBUSID, CCFBL.FEEDERID, " + 
                "DCC.CONTROLSTATUS, DCC.LASTSTATUSCHANGETIME, cb.operationalstate, CCFBL.CONTROLORDER " +
				"FROM DYNAMICCCCAPBANK DCC, CCFEEDERSUBASSIGNMENT CCF, CCFEEDERBANKLIST CCFBL, " +
				"ccsubstationsubbuslist ssb, ccsubareaassignment saa, capcontrolarea ca, capbank cb  " +
				"WHERE saa.substationbusid = ssb.substationid and ssb.substationbusid = CCF.substationbusid " +
				"and saa.areaid = ca.areaid and CCF.FEEDERID = CCFBL.FEEDERID " +
				"AND DCC.CAPBANKID = CCFBL.DEVICEID and cb.deviceid = ccfbl.deviceid");
                
                if (getPaoIDs() != null && getPaoIDs().length > 0) {
                    if(getFilterModelType().equals(ReportFilter.AREA)) { //fix
                        sql.append(" AND ca.areaid IN ( " + getPaoIDs()[0] +" ");
                        for (int i = 1; i < getPaoIDs().length; i++)
                            sql.append(" , " + getPaoIDs()[i]);
                                
                        sql.append(")");
                    }else if(getFilterModelType().equals(ReportFilter.CAPCONTROLFEEDER)) { //fix
                        sql.append(" AND CCFBL.FEEDERID IN ( " + getPaoIDs()[0] +" ");
                        for (int i = 1; i < getPaoIDs().length; i++)
                            sql.append(" , " + getPaoIDs()[i]);
                                
                        sql.append(")");
                    }else if(getFilterModelType().equals(ReportFilter.CAPCONTROLSUBBUS)) { //fix
                        sql.append(" AND CCF.SUBSTATIONBUSID IN ( " + getPaoIDs()[0] +" ");
                        for (int i = 1; i < getPaoIDs().length; i++)
                            sql.append(" , " + getPaoIDs()[i]);
                                
                        sql.append(")");
                    }
                }
                
				if (getControlStates() != null && getControlStates().length > 0) {
				    sql.append(" AND DCC.CONTROLSTATUS IN ( " + getControlStates()[0] +" ");
				    for (int i = 1; i < getControlStates().length; i++)
				        sql.append(" , " + getControlStates()[i]);
				            
				    sql.append(")");
				}
                
                
		return sql;
	}
		
	@SuppressWarnings("unchecked")
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
				if(getData() != null)
				{
//					Order the records
					Collections.sort(getData(), ccStatusDataComparator);
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
			SqlUtils.close(rset, pstmt, conn );
		}
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
	
	@Override
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
		if ( o instanceof CapControlStatusData)	//Integer is the PaobjectID
		{
		    CapControlStatusData ccStatData = (CapControlStatusData)o;

		    switch( columnIndex)
			{
				case SUB_BUS_NAME_COLUMN:
				    return DaoFactory.getPaoDao().getYukonPAOName(ccStatData.getSubBusPaoID().intValue());
				case FEEDER_NAME_COLUMN:
				    return DaoFactory.getPaoDao().getYukonPAOName(ccStatData.getFeederPaoID().intValue());
				
				case CAP_BANK_NAME_COLUMN:
					return DaoFactory.getPaoDao().getYukonPAOName(ccStatData.getCapBankPaoID().intValue());
					
				case CONTROL_STATUS_COLUMN:
					return DaoFactory.getStateDao().getLiteState(StateGroupUtils.STATEGROUPID_CAPBANK, ccStatData.getControlStatus().intValue());

				case LAST_STATUS_CHANGE_TIME_COLUMN:
					return ccStatData.getChangeDateTime();
                    
                case OPERTATIONAL_STATE_COLUMN:
                    return ccStatData.getOperationalState();
                    
                case DISABLE_FLAG_COLUMN:
                    return ccStatData.getDisableFlag();
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
			    SUB_BUS_NAME_STRING,
			    FEEDER_NAME_STRING,
			    CAP_BANK_NAME_STRING,
			    CONTROL_STATUS_STRING,
			    LAST_STATUS_CHANGE_TIME_STRING,
                OPERATIONAL_STATE_STRING,
                DISABLE_FLAG_STRING
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
				Date.class,
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
				new ColumnProperties(0, 1, 120, null),
				new ColumnProperties(120, 1, 120, null),
				new ColumnProperties(240, 1, 120, null),
				new ColumnProperties(360, 1, 100, null),
				new ColumnProperties(460, 1, 120, null),
                new ColumnProperties(580, 1, 80, null),
                new ColumnProperties(660, 1, 50, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}
	
	@Override
	public boolean useStartDate()
	{
		return false;
	}

	@Override
	public boolean useStopDate()
	{
		return false;
	}
	
	@Override
	public String getHTMLOptionsTable()
	{
	    String html = "";
		html += "<script>" + LINE_SEPARATOR;
		html += "function selectAllStates(form, value) {" + LINE_SEPARATOR;
		html += "  var stateGroup = form." + ATT_CAP_CONTROL_STATE + ";" + LINE_SEPARATOR;
		html += "  for (var i = 0; i < stateGroup.length; i++){" + LINE_SEPARATOR;
		html += "    stateGroup[i].checked = value;" + LINE_SEPARATOR;
		html += "  }" + LINE_SEPARATOR;
		html += "}" + LINE_SEPARATOR;
		html += "</script>" + LINE_SEPARATOR;
			    
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;
	    
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Control Status</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		LiteState [] liteStates = DaoFactory.getStateDao().getLiteStates( StateGroupUtils.STATEGROUPID_CAPBANK );
		for (int i = 0; i < liteStates.length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='checkbox' name='"+ATT_CAP_CONTROL_STATE +"' value='" + i + "' " + "' onclick='document.reportForm."+ATT_All_CAP_CONTROL_STATE+".checked = false;'" +  
			 (i>1 && i < 6? "checked" : "") + ">" + liteStates[i].getStateText() + LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' name='" + ATT_All_CAP_CONTROL_STATE +"' value='" + ALL_CAP_CONTROL_STATES + "' onclick='selectAllStates(document.reportForm, this.checked)'>Select All" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
        html += "    <td valign='top'>" + LINE_SEPARATOR;
        html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td valign='top' class='TitleHeader'>Order By</td>" +LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
        for( int i = 0; i < ORDER_TYPE_STRINGS.length; i++)
        {
            html += "        <tr>" + LINE_SEPARATOR;
            html += "          <td><input type='radio' name='orderBy' value='" + ORDER_TYPE_STRINGS[i] + "' " + (i==0? "checked" : "") + " >" + ORDER_TYPE_STRINGS[i] + LINE_SEPARATOR;
            html += "          </td>" + LINE_SEPARATOR;
            html += "        </tr>" + LINE_SEPARATOR;
        }
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
			String param = req.getParameter(ATT_All_CAP_CONTROL_STATE);
			if( param != null)
				setControlStates(null);	//ALL Of them!
			else{ 			
			    String[] paramArray = req.getParameterValues(ATT_CAP_CONTROL_STATE);
				if( paramArray != null)
				{
					int [] idsArray = new int[paramArray.length];
					for (int i = 0; i < paramArray.length; i++)
					{
						idsArray[i] = Integer.valueOf(paramArray[i]).intValue();
					}
					setControlStates(idsArray);
				}
				else
					setControlStates(null);
			}
            param = req.getParameter(ATT_ORDER_BY);
            setOrderBy(param);			
		}
	}
    
    public void setOrderBy( String order ) {
        orderBy = order;
    }
    
    public String getOrderBy() {
        return orderBy;
    }
    
    public int[] getControlStates()
    {
        return controlStates;
    }
    public void setControlStates(int[] controlStates)
    {
        this.controlStates = controlStates;
    }
    public void setControlStates(int controlStates)
    {
        setControlStates( new int[]{controlStates});
    }
}
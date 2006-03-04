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
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.database.model.ModelFactory;

/**
 * Created on Nov 11, 2005
 * @author snebben
 */
public class CapControlEventLogModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	public final static int SUB_BUS_NAME_COLUMN = 0;
	public final static int FEEDER_NAME_COLUMN = 1;
	public final static int CAP_BANK_NAME_COLUMN = 2;
	public final static int EVENT_DATE_TIME_COLUMN = 3;
	public final static int STATUS_VALUE_COLUMN = 4;
	public final static int EVENT_TEXT_COLUMN = 5;

	
	/** String values for column representation */
	public final static String SUB_BUS_NAME_STRING = "Substation Bus";
	public final static String FEEDER_NAME_STRING = "Cap Feeder";
	public final static String CAP_BANK_NAME_STRING = "Cap Bank";
	public final static String EVENT_DATE_TIME_STRING  = "Event Date/Time";
	public final static String STATUS_VALUE_STRING = "Status";
	public final static String EVENT_TEXT_STRING = "Event";
	
	private int[] controlStates = null;
	
	private static final String ATT_CAP_CONTROL_STATE = "capControlState";
	private static final String ATT_All_CAP_CONTROL_STATE = "capControlStateAll";
	
	private static final int ALL_CAP_CONTROL_STATES = -1;	//use some invalid number
	
	/** A string for the title of the data */
	private static String title = "Cap Control Schedule Activity Report";
		
	public Comparator ccStatusDataComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2){

		    CapControlStatusData data1 = (CapControlStatusData)o1;
		    CapControlStatusData data2 = (CapControlStatusData)o2;
	        
		    //Order by Sub Bus first
		    String thisValStr = PAOFuncs.getYukonPAOName(data1.getSubBusPaoID().intValue());
		    String anotherValStr = PAOFuncs.getYukonPAOName(data2.getSubBusPaoID().intValue());
			
			if( thisValStr.equalsIgnoreCase(anotherValStr))
			{
//				Order by Feeder
				thisValStr = PAOFuncs.getYukonPAOName(data1.getFeederPaoID().intValue());
				anotherValStr = PAOFuncs.getYukonPAOName(data2.getFeederPaoID().intValue());

				if( thisValStr.equalsIgnoreCase(anotherValStr))
				{
//					Order by CapBank
					thisValStr = PAOFuncs.getYukonPAOName(data1.getCapBankPaoID().intValue());
					anotherValStr = PAOFuncs.getYukonPAOName(data2.getCapBankPaoID().intValue());
				}
			}
			return (thisValStr.compareToIgnoreCase(anotherValStr));
		}
		public boolean equals(Object obj){
			return false;
		}
	};
	
	/**
	 * Default Constructor
	 */
	public CapControlEventLogModel()
	{
		super();
		setFilterModelTypes(new int[]{
		        ModelFactory.CAPCONTROLSTRATEGY}
			);
	}

	/**
	 * Add Integer (paobjectID) objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			Integer subBusPaoID = new Integer(rset.getInt(1));
			Integer feederPaoID = new Integer(rset.getInt(2));
			Integer capBankPaoID = new Integer(rset.getInt(3));
			String pointName = new String(rset.getString(4));
			java.sql.Timestamp changedateTime = rset.getTimestamp(5);
			String eventText = new String(rset.getString(6));
			Integer controlStatus = new Integer(rset.getInt(7));
						
			CapControlStatusData ccStatusData= new CapControlStatusData(
			        capBankPaoID, subBusPaoID, feederPaoID, pointName,
			        new Date(changedateTime.getTime()), eventText, controlStatus);
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
		StringBuffer sql = new StringBuffer	("SELECT CCLOG.SUBID, CCLOG.FEEDERID, P.PAOBJECTID, P.POINTNAME, CCLOG.DATETIME, CCLOG.TEXT, CCLOG.VALUE, CCLOG.SEQID, EVENTTYPE " +
											" FROM CCEVENTLOG CCLOG, POINT P " +
											" WHERE CCLOG.POINTID = P.POINTID " +
											" AND EVENTTYPE != 4 " +	//!= 4 is to remove the op increment logs
											" AND CCLOG.SEQID IN (SELECT DISTINCT CCLOG2.SEQID FROM CCEVENTLOG CCLOG2" +
												" WHERE CCLOG2.EVENTTYPE = 7 " +	//7 is for the start of a schedule
												" AND CCLOG2.DATETIME > ? " + 
												" AND CCLOG2.DATETIME <= ?");
				if (getPaoIDs() != null && getPaoIDs().length > 0)
				{
				    sql.append(" AND CCLOG2.SUBID IN ( " + getPaoIDs()[0] +" ");
				    for (int i = 1; i < getPaoIDs().length; i++)
				        sql.append(" , " + getPaoIDs()[i]);
				            
				    sql.append(")");
				}
				sql.append(" ) ");	//ending paren for IN statement
				if (getControlStates() != null && getControlStates().length > 0)
				{
				    sql.append(" AND VALUE IN ( " + getControlStates()[0] +" ");
				    for (int i = 1; i < getControlStates().length; i++)
				        sql.append(" , " + getControlStates()[i]);
				            
				    sql.append(")");
				}
				sql.append(" ORDER BY LOGID ");
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
		if ( o instanceof CapControlStatusData)	//Integer is the PaobjectID
		{
		    CapControlStatusData ccStatData = (CapControlStatusData)o;

		    switch( columnIndex)
			{
				case SUB_BUS_NAME_COLUMN:
				    return PAOFuncs.getYukonPAOName(ccStatData.getSubBusPaoID().intValue());

				case FEEDER_NAME_COLUMN:
				    return PAOFuncs.getYukonPAOName(ccStatData.getFeederPaoID().intValue());
				
				case CAP_BANK_NAME_COLUMN:
					return PAOFuncs.getYukonPAOName(ccStatData.getCapBankPaoID().intValue());
					
				case STATUS_VALUE_COLUMN:
					return StateFuncs.getLiteState(StateGroupUtils.STATEGROUPID_CAPBANK, ccStatData.getControlStatus().intValue());
					
				case EVENT_TEXT_COLUMN:
					return ccStatData.getEventText();
					
				case EVENT_DATE_TIME_COLUMN:
					return ccStatData.getChangeDateTime();
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
			    EVENT_DATE_TIME_STRING,
			    STATUS_VALUE_STRING,
			    EVENT_TEXT_STRING
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
				new ColumnProperties(0, 1, 160, null),
				new ColumnProperties(0, 1, 130, null),
				new ColumnProperties(130, 1, 130, null),
				new ColumnProperties(260, 1, 90, null),
				new ColumnProperties(350, 1, 90, null),
				new ColumnProperties(440, 1, 260, null)
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
	
	/**
	 * @return
	 */
	/*public int getOrderBy()
	{
		return orderBy;
	}
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
	}*/	

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
		LiteState [] liteStates = StateFuncs.getLiteStates( StateGroupUtils.STATEGROUPID_CAPBANK );
		for (int i = 0; i < liteStates.length; i++)
		{
			html += "        <tr>" + LINE_SEPARATOR;
			html += "          <td><input type='checkbox' name='"+ATT_CAP_CONTROL_STATE +"' value='" + i + "' " + "' onclick='document.reportForm."+ATT_All_CAP_CONTROL_STATE+".checked = false;'" +  
			 (i>3 && i < 6? "checked" : "") + ">" + liteStates[i].getStateText() + LINE_SEPARATOR;
			html += "          </td>" + LINE_SEPARATOR;
			html += "        </tr>" + LINE_SEPARATOR;
		}
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='checkbox' name='" + ATT_All_CAP_CONTROL_STATE +"' value='" + ALL_CAP_CONTROL_STATES + "' onclick='selectAllStates(document.reportForm, this.checked)'>Select All" + LINE_SEPARATOR;
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
			
//			String param = req.getParameter(ATT_ORDER_BY);
//			if( param != null)
//				setOrderBy(Integer.valueOf(param).intValue());
//			else
//				setOrderBy(ORDER_BY_METER_NAME);			
		}
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
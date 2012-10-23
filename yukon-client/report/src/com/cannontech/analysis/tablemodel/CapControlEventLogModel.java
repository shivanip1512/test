package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.device.capcontrol.CapControlStatusData;
import com.cannontech.analysis.report.SimpleYukonReportBase;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.device.Device;
import com.cannontech.database.db.state.StateGroupUtils;
import com.google.common.collect.Lists;

/**
 * Created on Nov 11, 2005
 * @author snebben
 */
public class CapControlEventLogModel extends FilterObjectsReportModelBase<Object>
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
	public final static String FEEDER_NAME_STRING = "Feeder";
	public final static String CAP_BANK_NAME_STRING = "Cap Bank";
	public final static String EVENT_DATE_TIME_STRING  = "Event Date/Time";
	public final static String STATUS_VALUE_STRING = "Status";
	public final static String EVENT_TEXT_STRING = "Event";
	
	private List<Integer> controlStates;
	private boolean showAllActivity = false;
	
	private static final String ATT_CAP_CONTROL_STATE = "capControlState";
	private static final String ATT_All_CAP_CONTROL_STATE = "capControlStateAll";
	private static final String ATT_SCHEDULE_ACTIVITY = "scheduleActivty";
	
	private static final int ALL_CAP_CONTROL_STATES = -1;	//use some invalid number
	
	/** A string for the title of the data */
	private static String title = "Cap Control Schedule Activity Report";
	
	/**
	 * Default Constructor
	 */
	public CapControlEventLogModel()
	{
		super();
		setFilterModelTypes(new ReportFilter[]{
		        ReportFilter.AREA,
		        ReportFilter.CAPCONTROLSUBSTATION,
		        ReportFilter.CAPCONTROLSUBBUS,
		        ReportFilter.CAPCONTROLFEEDER,}
			);
	}

	/**
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		//Result set is processed during collectData due to the unique processing of the data.
	}

	/**
	 * Build the SQL statement to retrieve DatabaseModel data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
	    SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT CCLOG.SUBID, YPO1.PaoName BusName, CCLOG.FEEDERID, YPO2.PaoName FeederName, P.PAOBJECTID, YPO3.PaoName BankName, P.POINTNAME,"); 
		sql.append("CCLOG.DATETIME, CCLOG.TEXT, CCLOG.VALUE, CCLOG.SEQID, EVENTTYPE");  
		sql.append("FROM CCEVENTLOG CCLOG");
		sql.append("Join Point P on CCLOG.PointId = P.PointId ");
		sql.append("JOIN YukonPaObject YPO1 on YPO1.paobjectId = CCLOG.SUBID");
		sql.append("JOIN YukonPaObject YPO2 on YPO2.paobjectId = CCLOG.FEEDERID");
		sql.append("JOIN YukonPaObject YPO3 on YPO3.paobjectId = P.PAOBJECTID ");
		sql.append("WHERE CCLOG.SEQID IN (");
		sql.append("  SELECT DISTINCT CCLOG2.SEQID"); 
		sql.append("  FROM CCEVENTLOG CCLOG2 ");
		sql.append("  WHERE CCLOG2.EVENTTYPE = 7");  
		sql.append("    AND CCLOG2.DATETIME > ?  ");
		sql.append("    AND CCLOG2.DATETIME <= ? ");
		
        if (getPaoIDs() != null && getPaoIDs().length > 0)
        {
            if (getFilterModelType().equals(ReportFilter.AREA)) {
                sql.append(" AND CCLOG.areaid IN ( " + getPaoIDs()[0] + " ");
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(" , " + getPaoIDs()[i]);

                sql.append(")");
            } else if (getFilterModelType().equals(ReportFilter.CAPCONTROLFEEDER)) {
                sql.append(" AND CCLOG.FEEDERID IN ( " + getPaoIDs()[0] + " ");
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(" , " + getPaoIDs()[i]);

                sql.append(")");
            } else if (getFilterModelType().equals(ReportFilter.CAPCONTROLSUBBUS)) {
                sql.append(" AND CCLOG.SUBID IN ( " + getPaoIDs()[0] + " ");
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(" , " + getPaoIDs()[i]);

                sql.append(")");
            } else if (getFilterModelType().equals(ReportFilter.CAPCONTROLSUBSTATION)) {
                sql.append(" AND CCLOG.STATIONID IN ( " + getPaoIDs()[0] + " ");
                for (int i = 1; i < getPaoIDs().length; i++)
                    sql.append(" , " + getPaoIDs()[i]);

                sql.append(")");
            }
        }
        sql.append(" ) "); // ending paren for IN statement

        if (!isShowAllActivity()) {
            sql.append(" AND EVENTTYPE != 4 "); // != 4 is to remove the op increment logs
        }

        /*Note: Verify the control states when processing the result set instead of in the query.
                Need all states if the most recent one is in getControlStates()*/
        sql.append("ORDER BY BusName, FeederName, BankName, CCLOG.SEQID, CCLOG.DATETIME");
        return new StringBuffer(sql.getSql());
	}

	@Override
	public void collectData() {
		//Reset all objects, new data being collected!
		setData(null);
		
		StringBuffer sql = buildSQLStatement();
		CTILogger.info(sql.toString());	
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		try {
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null ) {
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			} else {
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime() ));
				CTILogger.info("START DATE >= " + getStartDate() + " - STOP DATE < " + getStopDate());
				rset = pstmt.executeQuery();

                while( rset.next()) {
                    int subBusPaoID = rset.getInt(1);
                    String subBusName = rset.getString(2);
                    int feederPaoID = rset.getInt(3);
                    String feederName = rset.getString(4);
                    int capBankPaoID = rset.getInt(5);
                    String bankName = rset.getString(6);
                    String pointName = rset.getString(7);
                    Date changedateTime = rset.getTimestamp(8);
                    String eventText = rset.getString(9);
                    int controlStatus = rset.getInt(10);
                    int eventType = rset.getInt(11);
                
                    CapControlStatusData ccStatusData= new CapControlStatusData(capBankPaoID, bankName, subBusPaoID, subBusName, feederPaoID, feederName, pointName, changedateTime, eventText, controlStatus, eventType);                    
				
                    if( isShowAllActivity() 
                        || getControlStates() == null
                        || capBankPaoID == Device.SYSTEM_DEVICE_ID
                        || getControlStates().contains(controlStatus)) {
                        
                        getData().add(ccStatusData);
                    }
				}//end while
			}
		} catch( java.sql.SQLException e ) {
			e.printStackTrace();
		} finally {
			SqlUtils.close(rset, pstmt, conn);
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
					if( ccStatData.getSubBusPaoID().intValue() == Device.SYSTEM_DEVICE_ID)
						return null;
				    return ccStatData.getSubBusName();

				case FEEDER_NAME_COLUMN:
					if( ccStatData.getFeederPaoID().intValue() == Device.SYSTEM_DEVICE_ID)
						return null;
				    return ccStatData.getFeederName();
				
				case CAP_BANK_NAME_COLUMN:
					if( ccStatData.getCapBankPaoID().intValue() == Device.SYSTEM_DEVICE_ID)
						return null;
					return ccStatData.getBankName();
					
				case STATUS_VALUE_COLUMN:
					if( ccStatData.getEventType().intValue() == 4)//4 is for OpCount
						return new String("OpCount: " + ccStatData.getControlStatus().toString());
					return DaoFactory.getStateDao().findLiteState(StateGroupUtils.STATEGROUPID_CAPBANK, ccStatData.getControlStatus().intValue());
					
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
				new ColumnProperties(260, 1, 100, SimpleYukonReportBase.columnDateFormat),
				new ColumnProperties(360, 1, 90, null),
				new ColumnProperties(450, 1, 260, null)
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
		
		html += "function enableStates(form, value) {" + LINE_SEPARATOR;
		html += "  var stateGroup = form." + ATT_CAP_CONTROL_STATE + ";" + LINE_SEPARATOR;
		html += "  form." + ATT_All_CAP_CONTROL_STATE + ".checked = false;" + LINE_SEPARATOR;
		html += "  form." + ATT_All_CAP_CONTROL_STATE + ".disabled = value;" + LINE_SEPARATOR;
		html += "  document.getElementById('BankStatusID').disabled = value;" + LINE_SEPARATOR;
		html += "  if( value ) {" + LINE_SEPARATOR;		
		html += "    for (var i = 0; i < stateGroup.length; i++){" + LINE_SEPARATOR;
		html += "      stateGroup[i].checked = false;" + LINE_SEPARATOR;		
		html += "    }" + LINE_SEPARATOR;
		html += "  } else {" + LINE_SEPARATOR;
		html += "      stateGroup[4].checked = true;" + LINE_SEPARATOR;
		html += "      stateGroup[5].checked = true;" + LINE_SEPARATOR;
		html += "  }" + LINE_SEPARATOR;		
		html += "}" + LINE_SEPARATOR;
		html += "</script>" + LINE_SEPARATOR;
			    
		html += "<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "  <tr>" + LINE_SEPARATOR;

		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Schedule Activity Detail</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='radio' name='"+ATT_SCHEDULE_ACTIVITY +"' value='false' checked onclick='enableStates(document.reportForm, false);'>Ending Cap Bank Status Activity" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td><input type='radio' name='"+ATT_SCHEDULE_ACTIVITY +"' value='true' onclick='enableStates(document.reportForm, true);'>All Schedule Activity" + LINE_SEPARATOR;
		html += "          </td>" + LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		html += "      </table>" + LINE_SEPARATOR;
		html += "    </td>" + LINE_SEPARATOR;
		
		html += "    <td valign='top'>" + LINE_SEPARATOR;
		html += "        <div id='BankStatusID'>" + LINE_SEPARATOR;		
		html += "      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR;
	
		html += "        <tr>" + LINE_SEPARATOR;
		html += "          <td class='TitleHeader'>&nbsp;Ending Bank Status</td>" +LINE_SEPARATOR;
		html += "        </tr>" + LINE_SEPARATOR;
		LiteState [] liteStates = DaoFactory.getStateDao().getLiteStates( StateGroupUtils.STATEGROUPID_CAPBANK );
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
		html += "        </div>" + LINE_SEPARATOR;		
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
			String param = req.getParameter(ATT_SCHEDULE_ACTIVITY);
			if( param != null) {
				setShowAllActivity(Boolean.valueOf(param).booleanValue());	//ALL Of them!
			}
			
			param = req.getParameter(ATT_All_CAP_CONTROL_STATE);
			if( param != null) {
				setControlStates(null);	//ALL Of them!
			} else { 			
			    String[] paramArray = req.getParameterValues(ATT_CAP_CONTROL_STATE);
				if (paramArray != null) {
					List<Integer> idsArray = Lists.newArrayList();
					for (int i = 0; i < paramArray.length; i++) {
						idsArray.add(Integer.valueOf(paramArray[i]).intValue());
					}
					setControlStates(idsArray);
				} else {
					setControlStates(null);
				}
			}
			
		}
	}
    public List<Integer> getControlStates()
    {
        return controlStates;
    }

    public void setControlStates(List<Integer> controlStates)
    {
        this.controlStates = controlStates;
    }

	public boolean isShowAllActivity() {
		return showAllActivity;
	}

	public void setShowAllActivity(boolean showAllActivity) {
		this.showAllActivity = showAllActivity;
	}
}
package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.device.DisconnectMeterAndPointData;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.NaturalOrderComparator;

/**
 * Created on Feb 18, 2004
 * DisconnectReportDatabase TableModel object
 * Extending classes must implement:
 *   addDataRow(ResultSet)	- add a "row" object to the data Vector
 *   buildSQLStatement()	- Returns the sql query statment
 * 
 * Contains the start and stop times for query information.
 * Contains the devicemetergroup - YukonPaobject.paoname
 * 				deviceName - YukonPaobject.paoname
 * 				pointName - Point.pointname
 * @author bjonasson
 */
public class DisconnectModel extends ReportModelBase<DisconnectMeterAndPointData> implements Comparator<DisconnectMeterAndPointData>
{
    /** Number of columns */
    protected final int NUMBER_COLUMNS = 7;

    /** Enum values for column representation */
    public final static int DEVICE_NAME_COLUMN = 0;
    public final static int METER_NUMBER_COLUMN = 1;
    public final static int ADDRESS_COLUMN = 2;
    public final static int TYPE_COLUMN = 3;
    public final static int ROUTE_NAME_COLUMN = 4;
    public final static int DISCONNECT_ADDRESS_COLUMN = 5;
    public final static int TIMESTAMP_COLUMN = 6;
    public final static int STATE_COLUMN = 7;

    /** String values for column representation */
    public final static String DEVICE_NAME_STRING = "Device Name";
    public final static String METER_NUMBER_STRING = "Meter #";
    public final static String ADDRESS_STRING = "Address";
    public final static String TYPE_STRING = "Type";
    public final static String ROUTE_NAME_STRING = "Route Name";
    public final static String DISCONNECT_ADDRESS_STRING = "Collar Addr";
    public final static String TIMESTAMP_STRING = "Timestamp";
    public final static String STATE_STRING = "State";

    /** A string for the title of the data */ //DEFAULT
    private static String title = "Disconnect State";

    /** Rawpointhistory.value critera, null results in current disconnect state? */
    /** valid types are:  Disconnected | Connected | Intermediate | Invalid */
    public static String DISCONNECTED_STRING = "Disconnected";
    public static String CONNECTED_STRING = "Connected";
    public static String INTERMEDIATE_STRING = "Intermediate";
    public static String INVALID_STRING = "Invalid";

    public final static int DISCONNECTED_STATE = 0;
    public  final static int CONNECTED_STATE = 1;
    public final static int ALL_STATES = 2;
    private int disconnectState = ALL_STATES;

    public static final int ORDER_BY_DEVICE_NAME = 0;
    public static final int ORDER_BY_ROUTE_NAME = 1;
    public static final int ORDER_BY_METER_NUMBER = 2;
    public static final int ORDER_BY_TIMESTAMP = 3;
    public static final int ORDER_BY_STATE = 4;
    private int orderBy = ORDER_BY_DEVICE_NAME;	//default
    private static final int[] ALL_ORDER_BYS = new int[]
                                                       {
        ORDER_BY_DEVICE_NAME, ORDER_BY_ROUTE_NAME, ORDER_BY_METER_NUMBER,
        ORDER_BY_TIMESTAMP, ORDER_BY_STATE
                                                       };

    //flag for displaying the history of disconnect meters
    private boolean showHistory = false;

    private static final String ATT_DISCONNECT_STATE = "disconnectStat";
    private static final String ATT_SHOW_HISTORY = "history";
    private static final String ATT_ORDER_BY = "orderBy";

    /**
     * Constructor class
     */
    public DisconnectModel()
    {
        this(false);	
    }

    /**
     * When ShowHist is true, then disconnect/connected does NOT matter.
     * They only matter when !showHist, or rather when showing only CURRENT  
     * @param showHist
     */
    public DisconnectModel(boolean showHist)
    {
        super();
        setShowHistory(showHist);
        setFilterModelTypes(new ReportFilter[]{
                ReportFilter.METER,
                ReportFilter.DEVICE,
                ReportFilter.GROUPS}
                );
    }
    @Override
    public void collectData()
    {
        //Reset all objects, new data being collected!
        setData(null);

        SqlFragmentSource sql = buildSQLStatement();
        CTILogger.info(sql.toString());
        JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
        template.query(sql.getSql(), sql.getArguments(), new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rset) throws SQLException, DataAccessException {

                while( rset.next())
                {
                    addDataRow(rset);
                }

                return null;
            }
        });


        //Order the records
        if(getData() != null) {
            Collections.sort(getData(), this);
            if( getSortOrder() == DESCENDING) 
                Collections.reverse(getData());             
        }

        CTILogger.info("Report Records Collected from Database: " + getData().size());
        return;
    }


    /**
     * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
     * @return StringBuffer  an sqlstatement
     */
    public SqlFragmentSource buildSQLStatement() {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        //SELECT
        sql.append("SELECT DISTINCT PAO.PAOBJECTID, PAO.PAONAME, PAO.TYPE, PAO.DISABLEFLAG, ");
        sql.append("DMG.METERNUMBER, DCS.ADDRESS, ROUTE.PAOBJECTID as ROUTEPAOID, ROUTE.PAONAME as ROUTEPAONAME, ");
        sql.append("P.POINTID, P.POINTNAME, RPH1.TIMESTAMP, RPH1.VALUE, DISCONNECTADDRESS ");

        //FROM
        sql.append("FROM YukonPAObject pao left outer join DeviceMCT400Series mct on pao.paobjectid = mct.deviceid, ");
        sql.append("DEVICEMETERGROUP DMG, DEVICECARRIERSETTINGS DCS, ");
        sql.append("POINT P, DEVICEROUTES DR, YUKONPAOBJECT ROUTE, RAWPOINTHISTORY RPH1 ");

        //WHERE
        sql.append("WHERE PAO.PAOBJECTID = P.PAOBJECTID ");
        sql.append("AND PAO.PAOBJECTID = DMG.DEVICEID ");
        sql.append("AND PAO.PAOBJECTID = DCS.DEVICEID ");
        sql.append("AND PAO.PAOBJECTID = DR.DEVICEID ");
        sql.append("AND ROUTE.PAOBJECTID = DR.ROUTEID ");
        sql.append("AND P.POINTID = RPH1.POINTID ");
        sql.append("AND P.POINTOFFSET = 1 ");
        sql.append("AND P.POINTTYPE = ").appendArgument(PointTypes.getType(PointTypes.STATUS_POINT));
        sql.append(" AND ( PAO.TYPE in ('" + DeviceTypes.STRING_MCT_213[0] + "', ");
        sql.append("'" + DeviceTypes.STRING_MCT_310ID[0] + "', ");
        sql.append("'" + DeviceTypes.STRING_MCT_310IDL[0] + "') ");
        sql.append(" OR ( DISCONNECTADDRESS IS NOT NULL) ) ");        

        if( isShowHistory())
        {
           sql.append(" AND RPH1.TIMESTAMP > ").appendArgument(getStartDate());
           sql.append("AND RPH1.TIMESTAMP <= ").appendArgument(getStopDate());
        }
        else		
        {
            if( getDisconnectState() != ALL_STATES)	//limit the RPH value accepted
            {
                if (getDisconnectState() == CONNECTED_STATE)
                {
                    sql.append(" AND (RPH1.VALUE = 1.0 OR RPH1.VALUE = 3.0)");
                }
                else if (getDisconnectState() == DISCONNECTED_STATE)
                {
                    sql.append(" AND (RPH1.VALUE = 0.0 OR RPH1.VALUE = 2.0)");
                }
            }
            sql.append(" AND RPH1.TIMESTAMP = ( SELECT MAX(RPH2.TIMESTAMP) FROM RAWPOINTHISTORY RPH2 " + 
            " WHERE RPH1.POINTID = RPH2.POINTID) " );
        }
        
        // RESTRICT BY DEVICE/METER PAOID (if any)
        String paoIdWhereClause = getPaoIdWhereClause("PAO.PAOBJECTID");
        if (!StringUtils.isBlank(paoIdWhereClause)) {
            sql.append(" AND " + paoIdWhereClause);
        } 
        
        // RESTRICT BY GROUPS (if any)
        String[] groups = getBillingGroups();
        if (groups != null && groups.length > 0) {
            SqlFragmentSource deviceGroupSqlWhereClause = getGroupSqlWhereClause("PAO.PAOBJECTID");
            sql.append(" AND ", deviceGroupSqlWhereClause);
        }
        
        return sql;
    }

    /**
     * Add <innerClass> objects to data, retrieved from rset.
     * @param ResultSet rset
     */
    public void addDataRow(ResultSet rs) {
        try {
            
            final Meter meter = new Meter();
            meter.setDeviceId(rs.getInt("PAOBJECTID"));
            meter.setName(rs.getString("PAONAME"));
            PaoType paoType = PaoType.getForDbString(rs.getString("TYPE"));
            meter.setPaoType(paoType);
            meter.setDisabled(CtiUtilities.isTrue(rs.getString("DISABLEFLAG").charAt(0)));
            meter.setMeterNumber(rs.getString("METERNUMBER"));
            meter.setAddress(rs.getString("ADDRESS"));
            meter.setRouteId(rs.getInt("ROUTEPAOID"));
            meter.setRoute(rs.getString("ROUTEPAONAME"));
            final MeterAndPointData meterAndPointData = new MeterAndPointData(
                                                          meter,
                                                          rs.getInt("POINTID"),
                                                          rs.getString("POINTNAME"),
                                                          rs.getTimestamp("TIMESTAMP"),
                                                          rs.getDouble("VALUE")
                                                      );
            getData().add(new DisconnectMeterAndPointData(meterAndPointData, 
                                                          rs.getString("DISCONNECTADDRESS")));

        } catch(java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private String getRPHValueString(int pointID, double value)
    {
        LitePoint litePoint = (YukonSpringHook.getBean("pointDao", PointDao.class)).getLitePoint(pointID);
        int stateGroupId = litePoint.getStateGroupID();
        LiteState liteState = (YukonSpringHook.getBean("stateDao", StateDao.class)).findLiteState(stateGroupId, (int) value);
        return liteState.getStateText();
    }

    @Override
    public String getDateRangeString()
    {
        if (isShowHistory()) return super.getDateRangeString();

        return (getDateFormat().format(new Date()));	//use current date
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
     */
    public Object getAttribute(final int columnIndex, final Object o) {

        if (o instanceof DisconnectMeterAndPointData) {

            final DisconnectMeterAndPointData discMandPData = (DisconnectMeterAndPointData) o;

            switch( columnIndex) {
                case DEVICE_NAME_COLUMN:    
                    return discMandPData.getMeter().getName();
                case TIMESTAMP_COLUMN:
                    return discMandPData.getMeterAndPointData().getTimeStamp();
                case METER_NUMBER_COLUMN:
                    return discMandPData.getMeter().getMeterNumber();
                case ADDRESS_COLUMN:
                    return discMandPData.getMeter().getAddress();
                case TYPE_COLUMN:
                    return discMandPData.getMeter().getPaoType().getPaoTypeName();
                case STATE_COLUMN:
                    return (discMandPData.getMeterAndPointData().getValue() != null) ? 
                                getRPHValueString(discMandPData.getMeterAndPointData().getPointID(), 
                                                  discMandPData.getMeterAndPointData().getValue()) : "Unknown";
                case ROUTE_NAME_COLUMN:
                    return discMandPData.getMeter().getRoute();
                case DISCONNECT_ADDRESS_COLUMN:
                    return discMandPData.getDisconnectAddress();
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
                    DEVICE_NAME_STRING,
                    METER_NUMBER_STRING,
                    ADDRESS_STRING,
                    TYPE_STRING,
                    ROUTE_NAME_STRING,
                    DISCONNECT_ADDRESS_STRING,
                    TIMESTAMP_STRING,
                    STATE_STRING
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
                    java.util.Date.class,				
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
                    new ColumnProperties(0, 1, 150, null),	//Device Name
                    new ColumnProperties(150, 1, 65, null), //meternumber
                    new ColumnProperties(215, 1, 65, null), //address
                    new ColumnProperties(280, 1, 70, null), //type
                    new ColumnProperties(350, 1, 100, null), //routeName
                    new ColumnProperties(450, 1, 65, null), //discAddress 
                    new ColumnProperties(515, 1, 90, "MM/dd/yyyy HH:mm:ss"),   //Timestamp
                    new ColumnProperties(605, 1, 65, null) //state
            };
        }
        return columnProperties;
    }


    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getTitleString()
     */
    public String getTitleString()
    {
        if( getDisconnectState() == ALL_STATES)
            title = "All Disconnect";
        else if (getDisconnectState() == CONNECTED_STATE)
            title = "Connected";
        else if (getDisconnectState() == DISCONNECTED_STATE)
            title = "Disconnected";

        if( isShowHistory())
            title += " - Meter History Report";
        else
            title += " - Current State Report";

        return title;
    }
    /**
     * @return
     */
    public boolean isShowHistory()
    {
        return showHistory;
    }

    /**
     * @param b
     */
    public void setShowHistory(boolean b)
    {
        showHistory = b;
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
        case ORDER_BY_TIMESTAMP:
            return "Timestamp";
        case ORDER_BY_STATE:
            return "Disconnect State";
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
        final StringBuilder sb = new StringBuilder();
        sb.append("<script>" + LINE_SEPARATOR);
        sb.append("function enableCheckBox(value){" + LINE_SEPARATOR);
        sb.append("  if( value) {" + LINE_SEPARATOR);
        sb.append("    document.reportForm." + ATT_DISCONNECT_STATE + "[0].checked = !value;" + LINE_SEPARATOR);
        sb.append("    document.reportForm." + ATT_DISCONNECT_STATE + "[1].checked = !value;" + LINE_SEPARATOR);		
        sb.append("    document.reportForm." + ATT_DISCONNECT_STATE + "[2].checked = !value;" + LINE_SEPARATOR);
        sb.append("  } else {" + LINE_SEPARATOR);
        sb.append("    document.reportForm." + ATT_DISCONNECT_STATE + "[2].checked = !value;" + LINE_SEPARATOR); 		
        sb.append("  } " + LINE_SEPARATOR);
        sb.append("  document.reportForm." + ATT_DISCONNECT_STATE + "[0].disabled = value;" + LINE_SEPARATOR);
        sb.append("  document.reportForm." + ATT_DISCONNECT_STATE + "[1].disabled = value;" + LINE_SEPARATOR);
        sb.append("  document.reportForm." + ATT_DISCONNECT_STATE + "[2].disabled = value;" + LINE_SEPARATOR);
        sb.append("}" + LINE_SEPARATOR);

        //Run this method on load, it's NOT in a function!
        sb.append("enableDates(false);" + LINE_SEPARATOR);
        sb.append("</script>" + LINE_SEPARATOR);

        sb.append("<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("  <tr>" + LINE_SEPARATOR);
        sb.append("    <td valign='top'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td valign='top' class='TitleHeader'>Data Display</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='checkbox' name='" + ATT_SHOW_HISTORY +"' value='history' onclick='enableDates(this.checked);enableCheckBox(this.checked)'>Historical" + LINE_SEPARATOR);
        sb.append("          </td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);

        sb.append("    <td valign='top'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);		
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td class='TitleHeader'>&nbsp;Disconnect State</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='radio' name='" +ATT_DISCONNECT_STATE+ "' value='"+CONNECTED_STATE+"'>Show All Connected</td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);		
        sb.append("          <td><input type='radio' name='" +ATT_DISCONNECT_STATE+ "' value='"+DISCONNECTED_STATE+"'>Show All Disconnected</td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);		
        sb.append("          <td><input type='radio' name='" +ATT_DISCONNECT_STATE + "' value='"+ALL_STATES+"' checked>Show All</td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);

        sb.append("    <td valign='top'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td class='TitleHeader'>&nbsp;Order By</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        for (int i = 0; i < getAllOrderBys().length; i++)
        {
            sb.append("        <tr>" + LINE_SEPARATOR);
            sb.append("          <td><input type='radio' name='"+ATT_ORDER_BY+"' value='" + getAllOrderBys()[i] + "' ");  
            sb.append((i==0? "checked" : "") + ">" + getOrderByString(getAllOrderBys()[i])+ LINE_SEPARATOR);
            sb.append("          </td>" + LINE_SEPARATOR);
            sb.append("        </tr>" + LINE_SEPARATOR);
        }
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);

        sb.append("  </tr>" + LINE_SEPARATOR);
        sb.append("</table>" + LINE_SEPARATOR);
        return sb.toString();
    }
    @Override
    public void setParameters( HttpServletRequest req )
    {
        super.setParameters(req);
        if( req != null)
        {
            String param = req.getParameter(ATT_DISCONNECT_STATE);
            if (param != null)
                setDisconnectState(Integer.valueOf(param).intValue());			

            param = req.getParameter(ATT_SHOW_HISTORY);
            setShowHistory(param != null);	//opposite boolean value, since wording for option is "backwards"

            param = req.getParameter(ATT_ORDER_BY);
            if( param != null)
                setOrderBy(Integer.valueOf(param).intValue());
            else
                setOrderBy(ORDER_BY_DEVICE_NAME);			
        }
    }
    /**
     * @return Returns the disconnectState.
     */
    public int getDisconnectState()
    {
        return disconnectState;
    }
    /**
     * @param The disconnectState to set.
     */
    public void setDisconnectState(int disconnectState)
    {
        this.disconnectState = disconnectState;
    }

    public int compare(DisconnectMeterAndPointData o1, DisconnectMeterAndPointData o2) {
        if (getOrderBy() == ORDER_BY_ROUTE_NAME) {
            return o1.getMeter().getRoute().compareToIgnoreCase(o2.getMeter().getRoute());
        } 

        if (getOrderBy() == ORDER_BY_METER_NUMBER) {
            NaturalOrderComparator noComp = new NaturalOrderComparator();
            return noComp.compare(o1.getMeter().getMeterNumber(), o2.getMeter().getMeterNumber());
        }

        if (getOrderBy() == ORDER_BY_TIMESTAMP) {
            return o1.getMeterAndPointData().getTimeStamp().compareTo(o2.getMeterAndPointData().getTimeStamp());
        }

        if ( getOrderBy() == ORDER_BY_STATE) {
            return o1.getMeterAndPointData().getValue().compareTo(o2.getMeterAndPointData().getValue());
        }

        return o1.getMeter().getName().compareToIgnoreCase(o2.getMeter().getName());
    }
}

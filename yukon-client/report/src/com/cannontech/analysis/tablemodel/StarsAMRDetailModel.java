package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.analysis.data.stars.StarsAMRDetail;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.NaturalOrderComparator;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;

public class StarsAMRDetailModel extends ReportModelBase<StarsAMRDetail> implements Comparator<StarsAMRDetail> {
    /** Number of columns */
    protected final int NUMBER_COLUMNS = 10;

    /** Enum values for column representation */
    public final static int ROUTE_NAME_COLUMN = 0;
    public final static int ACCOUNT_NUMBER_COLUMN = 1;
    public final static int CONTACT_NAME_COLUMN = 2;
    public final static int MAP_NUMBER_COLUMN = 3;
    public final static int DEVICE_NAME_COLUMN = 4;
    public final static int DEVICE_TYPE_COLUMN = 5;
    public final static int PHYSICAL_ADDRESS_COLUMN = 6;
    public final static int METER_NUMBER_COLUMN = 7;
    public final static int LAST_KWH_READING_COLUMN = 8;
    public final static int DATE_TIME_COLUMN = 9;

    /** String values for column representation */
    public final static String ACCOUNT_NUMBER_STRING = "Account #";
    public final static String CONTACT_NAME_STRING = "Contact";
    public final static String MAP_NUMBER_STRING = "Map #";
    public final static String DEVICE_NAME_STRING = "Device Name";
    public final static String DEVICE_TYPE_STRING = "Device Type";
    public final static String PHYSICAL_ADDRESS_STRING = "Address";
    public final static String METER_NUMBER_STRING = "Meter #";
    public final static String ROUTE_NAME_STRING = "Route Name";
    public final static String LAST_KWH_READING_STRING = "Last kWh";
    public final static String DATE_TIME_STRING = "Date/Time";

    /** Class fields */
    public static final int ORDER_BY_ACCOUNT_NUMBER = 0;
    public static final int ORDER_BY_LAST_NAME = 1;
    public static final int ORDER_BY_MAP_NUMBER = 2;
    public static final int ORDER_BY_PHYSICAL_ADDRESS = 3;
    public static final int ORDER_BY_DEVICE_NAME = 4;
    public static final int ORDER_BY_DEVICE_TYPE = 5;
    public static final int ORDER_BY_METER_NUMBER = 6;
    public static final int ORDER_BY_VALUE = 7;
    public static final int ORDER_BY_DATE_TIME = 8;
    private int orderBy = ORDER_BY_ACCOUNT_NUMBER;	//default
    private static final int[] ALL_ORDER_BYS = new int[]
                                                       {
        ORDER_BY_ACCOUNT_NUMBER, ORDER_BY_LAST_NAME, ORDER_BY_MAP_NUMBER, ORDER_BY_PHYSICAL_ADDRESS, 
        ORDER_BY_DEVICE_NAME, ORDER_BY_DEVICE_TYPE, ORDER_BY_METER_NUMBER, ORDER_BY_VALUE, ORDER_BY_DATE_TIME 
                                                       };
    //servlet attributes/parameter strings
    private static final String ATT_ORDER_BY = "orderBy";
    private static final String ATT_SHOW_HISTORY = "history";	

    //flag for displaying the history of the AMR Details, enables start and stop selections
    private boolean showHistory = false;

    /**
     * 
     */
    public StarsAMRDetailModel()
    {
        this(false);
    }
    /**
     * 
     */
    public StarsAMRDetailModel(boolean showHistory_)
    {
        super();
        setShowHistory(showHistory_);
        setFilterModelTypes(new ReportFilter[]{ 
                ReportFilter.GROUPS,
                ReportFilter.ROUTE}
        );
    }

    @Override
    public void collectData()
    {
        //Reset all objects, new data being collected!
        setData(null);
        
        List<SimpleDevice> devices = getDeviceList();
        List<PaoPointIdentifier> identifiers = Lists.newArrayList();
        AttributeService attributeService = YukonSpringHook.getBean("attributeService", AttributeService.class);
        for(SimpleDevice device : devices) {
            try {
                PaoPointIdentifier identifier = attributeService.getPaoPointIdentifierForAttribute(device, BuiltInAttribute.USAGE);
                identifiers.add(identifier);
            } catch (IllegalUseOfAttribute e) {
                continue;  /* This device does not support the choosen attribute. */
            }
        }
        ImmutableMultimap<PointIdentifier, PaoIdentifier> paoPointIdentifiersMap = PaoUtils.mapPaoPointIdentifiers(identifiers);

        SimpleJdbcTemplate simpleJdbcTemplate = YukonSpringHook.getBean("simpleJdbcTemplate", SimpleJdbcTemplate.class);
        for(final PointIdentifier pointIdentifier : paoPointIdentifiersMap.keySet()){
            List<Integer> deviceIds = Lists.newArrayList();
            for(PaoIdentifier paoIdentifier :  paoPointIdentifiersMap.get(pointIdentifier)){
                deviceIds.add(paoIdentifier.getPaoId());
            }

            ChunkingSqlTemplate template = new ChunkingSqlTemplate(simpleJdbcTemplate);

            SqlFragmentGenerator<Integer> gen = new SqlFragmentGenerator<Integer>() {
                @Override
                public SqlFragmentSource generate(List<Integer> subList) {
                    SqlStatementBuilder sql = new SqlStatementBuilder();
             
                  //SELECT
                    sql.append("SELECT DISTINCT PAO.PAOBJECTID, PAO.PAONAME, PAO.TYPE, PAO.DISABLEFLAG, ");
                    sql.append("DMG.METERNUMBER, DCS.ADDRESS, ROUTE.PAOBJECTID as ROUTEPAOBJECTID, ROUTE.PAONAME as ROUTEPAONAME, ");
                    sql.append("P.POINTID, P.POINTNAME, RPH1.TIMESTAMP, RPH1.VALUE, ");
                    sql.append("CA.ACCOUNTNUMBER, SITE.SITENUMBER, CUST.CUSTOMERID ");
                    
                    //FROM
                    sql.append("FROM YUKONPAOBJECT PAO, YUKONPAOBJECT PAO2, CUSTOMERACCOUNT CA, ACCOUNTSITE SITE, ");
                    sql.append("CUSTOMER CUST, INVENTORYBASE IB, POINT P, RAWPOINTHISTORY RPH1, ");
                    sql.append("DEVICEMETERGROUP DMG, DEVICECARRIERSETTINGS DCS, DEVICEROUTES DR, YUKONPAOBJECT ROUTE ");

                    //WHERE
                    
                    sql.append("WHERE CUST.CUSTOMERID = CA.CUSTOMERID ");
                    sql.append("AND CA.ACCOUNTSITEID = SITE.ACCOUNTSITEID ");
                    sql.append("AND IB.ACCOUNTID = CA.ACCOUNTID ");
                    sql.append("AND IB.DEVICEID > 0 ");
                    sql.append("AND IB.DEVICEID = PAO.PAOBJECTID ");
                    sql.append("AND P.PAOBJECTID = PAO.PAOBJECTID ");
                    sql.append(" AND P.PointOffset").eq_k(pointIdentifier.getOffset());
                    sql.append(" AND P.PointType").eq_k(pointIdentifier.getPointType());
                    sql.append("AND P.POINTID = RPH1.POINTID ");
                    sql.append("AND PAO.PAOBJECTID = DMG.DEVICEID ");
                    sql.append("AND PAO.PAOBJECTID = DCS.DEVICEID ");
                    sql.append("AND PAO.PAOBJECTID = DR.DEVICEID ");
                    sql.append("AND ROUTE.PAOBJECTID = DR.ROUTEID ");
                    sql.append("AND RPH1.TIMESTAMP = ( SELECT MAX(RPH2.TIMESTAMP) FROM RAWPOINTHISTORY RPH2 ");
                    sql.append("WHERE RPH1.POINTID = RPH2.POINTID ");

                    if( isShowHistory()) {
                        sql.append(" AND TIMESTAMP").gt(getStartDate());
                        sql.append("AND TIMESTAMP").lte(getStopDate());
                    }

                    sql.append(") ");
                    
                    if(getFilterModelType().equals(ReportFilter.ROUTE)) { //these are Route IDS
                        sql.append(" AND DR.ROUTEID = PAO2.PAOBJECTID ");
                        sql.append(" AND PAO2.PAObjectID in (").appendArgumentList(subList).append(") ");
                    } else {
                        sql.append("  AND PAO.PAObjectID in (").appendArgumentList(subList).append(") ");
                    }
                    CTILogger.info("SQL for StarsAMRDetailModel: (" + pointIdentifier.toString() + " - " + sql.toString());
                    CTILogger.info("START DATE >= " + getStartDate() + " - STOP DATE < " + getStopDate());

                    return sql;
                }
            };

            List<StarsAMRDetail> rows = template.query(gen, deviceIds, new ParameterizedRowMapper<StarsAMRDetail>() {
                @Override
                public StarsAMRDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
                    
                    final Meter meter = new Meter();
                    meter.setDeviceId(rs.getInt("PAOBJECTID"));
                    meter.setName(rs.getString("PAONAME"));
                    PaoType paoType = PaoType.getForDbString(rs.getString("TYPE"));
	                meter.setPaoType(paoType);
                    meter.setDisabled(CtiUtilities.isTrue(rs.getString("DISABLEFLAG").charAt(0)));
                    meter.setMeterNumber(rs.getString("METERNUMBER"));
                    meter.setAddress(rs.getString("ADDRESS"));
                    meter.setRouteId(rs.getInt("ROUTEPAOBJECTID"));
                    meter.setRoute(rs.getString("ROUTEPAONAME"));

                    final MeterAndPointData mpData = 
                        new MeterAndPointData(
                                              meter,
                                              rs.getInt("POINTID"),
                                              rs.getString("POINTNAME"),
                                              rs.getTimestamp("TIMESTAMP"),
                                              rs.getDouble("VALUE")
                        );

                    return new StarsAMRDetail(mpData,
                                              rs.getString("ACCOUNTNUMBER"),
                                              rs.getInt("CUSTOMERID"),
                                              rs.getString("SITENUMBER")
                                  );
                }
            });
            getData().addAll(rows);
        }
        
        if( getData() != null)
        {
            Collections.sort(getData(), this);
            if( getOrderBy() == DESCENDING)
                Collections.reverse(getData());
        }
        
        CTILogger.info("Report Records Collected from Database: " + getData().size());
        return;
    }
    
    @Override
    public String getDateRangeString()
    {
        if( isShowHistory())
            return super.getDateRangeString();

        return ( getDateFormat().format(new Date()));	//use current date
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
     */
    public Object getAttribute(int columnIndex, Object o)
    {
        if (o instanceof StarsAMRDetail)
        {
            final StarsAMRDetail detail = (StarsAMRDetail) o;
            final MeterAndPointData mpData = detail.getMeterAndPointData();
            
            switch( columnIndex)
            {
            case ROUTE_NAME_COLUMN:
                return mpData.getMeter().getRoute();
            case ACCOUNT_NUMBER_COLUMN:
                return detail.getAccountNumber();
            case CONTACT_NAME_COLUMN:
                return (detail.getLitePrimaryContact() == null ? null : detail.getLitePrimaryContact().getContLastName() + ", " + detail.getLitePrimaryContact().getContFirstName());
            case MAP_NUMBER_COLUMN:
                return detail.getMapNumber();
            case DEVICE_NAME_COLUMN:
                return mpData.getMeter().getName();
            case DEVICE_TYPE_COLUMN:
                return mpData.getMeter().getPaoType().getPaoTypeName();
            case METER_NUMBER_COLUMN:
                return mpData.getMeter().getMeterNumber();
            case PHYSICAL_ADDRESS_COLUMN:
                return mpData.getMeter().getAddress();
            case LAST_KWH_READING_COLUMN:
                return mpData.getValue();
            case DATE_TIME_COLUMN:
                return mpData.getTimeStamp();
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
                    ACCOUNT_NUMBER_STRING, 
                    CONTACT_NAME_STRING,
                    MAP_NUMBER_STRING,
                    DEVICE_NAME_STRING,
                    DEVICE_TYPE_STRING,
                    PHYSICAL_ADDRESS_STRING,
                    METER_NUMBER_STRING,
                    LAST_KWH_READING_STRING,
                    DATE_TIME_STRING
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
            columnProperties = new ColumnProperties[]{
                    //posX, posY, width, height, numberFormatString
                    new ColumnProperties(0, 1, 500, null),
                    new ColumnProperties(60, 1, 95, null),
                    new ColumnProperties(155, 1, 70, null),
                    new ColumnProperties(225, 1, 110, null),
                    new ColumnProperties(335, 1, 60, null),
                    new ColumnProperties(395, 1, 53, null),
                    new ColumnProperties(448, 1, 53, null),
                    new ColumnProperties(501, 1, 80, null),
                    new ColumnProperties(581, 1, 66, columnValueFormat),
                    new ColumnProperties(647, 1, 85, columnDateTimeFormat)
            };
        }
        return columnProperties;
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.Reportable#getTitleString()
     */
    public String getTitleString()
    {
        String title = "Stars "; 
        if( isShowHistory())
            title += "Historical ";
        title += "AMR Detail - ";
        
        if( getFilterModelType().equals(ReportFilter.ROUTE))
            title += " By Route";
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
        case ORDER_BY_VALUE:
            return "Last kWh Reading";
        case ORDER_BY_DATE_TIME:
            return "Reading Date/Time";
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
        sb.append("enableDates(false);" + LINE_SEPARATOR);
        sb.append("</script>" + LINE_SEPARATOR);

        sb.append("<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("  <tr>" + LINE_SEPARATOR);

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

        sb.append("    <td valign='top'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td valign='top' class='TitleHeader'>Data Display</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='checkbox' name='" + ATT_SHOW_HISTORY +"' value='history' onclick='enableDates(this.checked)'>Historical" + LINE_SEPARATOR);
        sb.append("          </td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
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
            String param = req.getParameter(ATT_ORDER_BY);
            if( param != null)
                setOrderBy(Integer.valueOf(param).intValue());
            else
                setOrderBy(ORDER_BY_DEVICE_NAME);

            param = req.getParameter(ATT_SHOW_HISTORY);
            setShowHistory(param != null);	//opposite boolean value, since wording for option is "backwards"
        }
    }

    @Override
    public void setFilterModelType(ReportFilter filterModelType)
    {
        if( getFilterModelType() != filterModelType)
            columnNames = null;
        super.setFilterModelType(filterModelType);
    }
    /**
     * @return Returns the showHistory.
     */
    public boolean isShowHistory()
    {
        return showHistory;
    }
    /**
     * @param showHistory The showHistory to set.
     */
    public void setShowHistory(boolean showHistory)
    {
        this.showHistory = showHistory;
    }
    
    public int compare(StarsAMRDetail o1, StarsAMRDetail o2){
        int tempOrderBy = getOrderBy();
        final MeterAndPointData mpData1 = o1.getMeterAndPointData();
        final MeterAndPointData mpData2 = o2.getMeterAndPointData();
        
        String thisVal = NULL_STRING;
        String anotherVal = NULL_STRING;
        
        if (getFilterModelType().equals(ReportFilter.ROUTE)){
            thisVal = mpData1.getMeter().getRoute();
            anotherVal = mpData2.getMeter().getRoute();
        }

        if (thisVal.equalsIgnoreCase(anotherVal) ) {
            
            if (tempOrderBy == ORDER_BY_DEVICE_TYPE) {
                thisVal = mpData1.getMeter().getPaoType().getPaoTypeName();
                anotherVal = mpData2.getMeter().getPaoType().getPaoTypeName();
                if( thisVal.equalsIgnoreCase(anotherVal))
                    tempOrderBy = ORDER_BY_DEVICE_NAME;
            }
            if( tempOrderBy == ORDER_BY_DEVICE_NAME)
            {
                thisVal = mpData1.getMeter().getName();
                anotherVal = mpData2.getMeter().getName();
                if( thisVal.equalsIgnoreCase(anotherVal))
                    tempOrderBy = ORDER_BY_PHYSICAL_ADDRESS;
            }
            if( tempOrderBy == ORDER_BY_ACCOUNT_NUMBER)
            {
                thisVal = o1.getAccountNumber();
                anotherVal = o2.getAccountNumber();
                if (thisVal.equalsIgnoreCase(anotherVal))
                    tempOrderBy = ORDER_BY_LAST_NAME;   //Need to order by lastName
            }
            if( tempOrderBy == ORDER_BY_LAST_NAME )
            {
                thisVal = o1.getLitePrimaryContact().getContLastName();
                anotherVal = o2.getLitePrimaryContact().getContLastName();
                if ( thisVal.equalsIgnoreCase(anotherVal))
                {
                    thisVal = o1.getLitePrimaryContact().getContFirstName();
                    anotherVal = o2.getLitePrimaryContact().getContFirstName();
                    if (thisVal.equalsIgnoreCase(anotherVal))                       
                        tempOrderBy = ORDER_BY_PHYSICAL_ADDRESS;    //Need to order by physicalAddress
                }
            }
            if( tempOrderBy == ORDER_BY_PHYSICAL_ADDRESS)
            {
                thisVal = mpData1.getMeter().getAddress();
                anotherVal = mpData2.getMeter().getAddress();
            }
            if( tempOrderBy == ORDER_BY_MAP_NUMBER)
            {
                thisVal = o1.getMapNumber();
                anotherVal = o2.getMapNumber();
            }
            if( tempOrderBy == ORDER_BY_METER_NUMBER)
            {
                NaturalOrderComparator noComp = new NaturalOrderComparator(); 
                return noComp.compare(mpData1.getMeter().getMeterNumber(), mpData2.getMeter().getMeterNumber());
            }
            if( tempOrderBy == ORDER_BY_VALUE)
            {
                return mpData1.getValue().compareTo(mpData2.getValue());
            }
            if( tempOrderBy == ORDER_BY_DATE_TIME)
            {
                return mpData1.getTimeStamp().compareTo(mpData2.getTimeStamp());
            }
        }
        return ( thisVal.compareToIgnoreCase(anotherVal));
    }
}

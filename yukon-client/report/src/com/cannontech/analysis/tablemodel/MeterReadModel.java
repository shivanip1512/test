package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.util.NaturalOrderComparator;

/**
 * Created on Dec 15, 2003
 * MeterData TableModel object
 *  String deviceName	- YukonPaobject.paoName
 *  String pointName	- Point.pointName
 *  Integer pointID		- Point.pointID
 *  String routeName  
 * @author snebben
 */
public class MeterReadModel extends ReportModelBase<MeterAndPointData> implements Comparator<MeterAndPointData>
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;
	
	/** Enum values for column representation */
	public final static int DEVICE_NAME_COLUMN = 0;
    public final static int DISABLED_COLUMN = 1;
    public final static int PAO_TYPE_COLUMN = 2;
    public final static int METER_NUMBER_COLUMN = 3;
	public final static int PHYSICAL_ADDRESS_COLUMN = 4;
	public final static int ROUTE_NAME_COLUMN = 5;
	public final static int TIMESTAMP_COLUMN = 6;

	/** String values for column representation */
	public final static String DEVICE_NAME_STRING = "Device Name";
    public final static String DISABLED_STRING = "Disabled";
    public final static String PAO_TYPE_STRING = "Type";
	public final static String METER_NUMBER_STRING = "Meter #";
	public final static String PHYSICAL_ADDRESS_STRING = "Address";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String TIMESTAMP_STRING= "Timestamp";
    
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
	
	public final static int INCLUDE_DISABLED_DEVICE_STATUS = 0;
	public final static int EXCLUDE_DISABLED_DEVICE_STATUS = 1;
	private int disabledDeviceStatus = INCLUDE_DISABLED_DEVICE_STATUS; 

	//servlet attributes/parameter strings
	private static String ATT_METER_READ_TYPE = "meterReadType";
	private static final String ATT_ORDER_BY = "orderBy";
	private static final String ATT_DISABLED_DEVICE_STATUS = "disabledDeviceStatus";
	
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
		this(MISSED_METER_READ_TYPE, start_);
	}	
	/**
	 * 
	 */
	public MeterReadModel(int readType_, Date start_)
	{
		//Long.MIN_VALUE is the default (null) value for time
		super(start_, null);
		setFilterModelTypes(new ReportFilter[]{
				ReportFilter.METER,
				ReportFilter.GROUPS
				} 
		);
	}
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try {
		    String disabledStr = rset.getString("disableFlag");
		    boolean disabled = CtiUtilities.isTrue(disabledStr.charAt(0));
		    
		    
		    if(getDisabledDeviceStatus() == INCLUDE_DISABLED_DEVICE_STATUS || !disabled)
		    {
    		    Meter meter = new Meter();
                
                int paobjectID = rset.getInt("paobjectId");
                meter.setDeviceId(paobjectID);
                String paoName = rset.getString("paoName");
                meter.setName(paoName);
                String type = rset.getString("type");
                int deviceType = PAOGroups.getDeviceType(type);
                meter.setType(deviceType);
                meter.setTypeStr(type);
                meter.setDisabled(disabled);
                String meterNumber = rset.getString("meterNumber");
                meter.setMeterNumber(meterNumber);
                String address = rset.getString("address");
                meter.setAddress(address);
                int routeID = rset.getInt("routeId");
                meter.setRouteId(routeID);
                String routeName = rset.getString("routeName");
                meter.setRoute(routeName);
                
    			Date ts = null;
    			if (getMeterReadType()== SUCCESS_METER_READ_TYPE)
    			{
    			    Timestamp timestamp = rset.getTimestamp("timestamp");
    			    ts = new Date(timestamp.getTime());
    			}
    			MeterAndPointData mpData = new MeterAndPointData(meter, ts);
    
    			getData().add(mpData);
		    }
		}
		catch(SQLException e)
		{
			throw new DataRetrievalFailureException("Unable to addDataRow", e);
		}
	}

	/**
	 * Build the SQL statement to retrieve MissedMeter data.
	 * @return StringBuffer  an sqlstatement
	 */
	public SqlFragmentSource buildSQLStatement() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT");
        sql.append("  PAO.PAOBJECTID, PAO.PAONAME, PAO.TYPE, PAO.DISABLEFLAG, DMG.METERNUMBER, DCS.ADDRESS,");
        sql.append("  ROUTE.PAOBJECTID as routeId, ROUTE.PAONAME as routeName");

		if (getMeterReadType() == SUCCESS_METER_READ_TYPE) {
            sql.append(", RPH.maxTime as TIMESTAMP");
        }

        sql.append("FROM YukonPaObject PAO");
        sql.append("  JOIN DeviceMeterGroup DMG on PAO.PAOBJECTID = DMG.DEVICEID");
        sql.append("  JOIN DeviceCarrierSettings DCS on PAO.PAOBJECTID = DCS.DEVICEID");
        sql.append("  JOIN DeviceRoutes DR on PAO.PAOBJECTID = DR.DEVICEID");
        sql.append("  JOIN YukonPaObject ROUTE on ROUTE.PAOBJECTID = DR.ROUTEID");
        
        if( getMeterReadType() == SUCCESS_METER_READ_TYPE) {
            sql.append("  JOIN (");
            sql.append("    SELECT sP.paobjectid, max(sRPH.timestamp) maxTime");
            sql.append("    FROM RawPointHistory sRPH");
            sql.append("      JOIN Point sP on sRPH.pointId = sP.pointId");
            sql.append("    WHERE timestamp > ").appendArgument(getStartDate());
            sql.append("      AND timestamp <= ").appendArgument(getStopDate());
            sql.appendFragment(buildWhereClause("sP.paobjectId"));
            sql.append("    GROUP BY sP.paobjectid");
            sql.append("  ) RPH on RPH.paobjectid = PAO.paobjectid");
        } else {
            sql.append("WHERE  ");
            sql.append("  PAO.PAOBJECTID NOT IN (");
            sql.append("    SELECT distinct sP.paobjectid " );
            sql.append("    FROM RawPointHistory sRPH" );
            sql.append("      JOIN Point sP on sRPH.pointId = sP.pointId" );
            sql.append("    WHERE timestamp > ").appendArgument(getStartDate());
            sql.append("      AND timestamp <= ").appendArgument(getStopDate());
            sql.append("  )");
            sql.appendFragment(buildWhereClause("pao.paobjectid"));
        }
        
		return sql;
	}

    private SqlStatementBuilder buildWhereClause(String columnName) {
        SqlStatementBuilder sqlWhere = new SqlStatementBuilder();
        if (getPaoIDs() != null && getPaoIDs().length > 0) {
        	sqlWhere.append(" AND ", columnName, "IN (", getPaoIDs(), ") ");
        }
        
        if (getBillingGroups() != null && getBillingGroups().length > 0) {
            SqlFragmentSource deviceGroupSqlWhereClause = getGroupSqlWhereClause(columnName);
            sqlWhere.append(" AND ", deviceGroupSqlWhereClause);
        }
        return sqlWhere;
    }
	
	@Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
				
		SqlFragmentSource sql = buildSQLStatement();
		CTILogger.info("SQL for MeterReadModel: " + sql.toString());
		CTILogger.info("START DATE >= " + getStartDate() + " - STOP DATE < " + getStopDate());
		JdbcOperations template = JdbcTemplateHelper.getYukonTemplate();
		template.query(sql.getSql(), sql.getArguments(), new RowCallbackHandler() {
		    @Override
		    public void processRow(ResultSet rset) throws SQLException {
		        addDataRow(rset);
		    }
		});

		if (getData() != null)
		{
		    // Order the records
		    Collections.sort(getData(), this);
		    if (getSortOrder() == DESCENDING)
		        Collections.reverse(getData());				
		}
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
	 
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof MeterAndPointData)
		{
			MeterAndPointData mpData = ((MeterAndPointData)o);

			switch( columnIndex)
			{
				case DEVICE_NAME_COLUMN:
					return mpData.getMeter().getName();

                case DISABLED_COLUMN:
                    return (mpData.getMeter().isDisabled() ? "Yes" : "No");
        
                case PAO_TYPE_COLUMN:
                    return mpData.getMeter().getTypeStr();

                case METER_NUMBER_COLUMN:
                    return mpData.getMeter().getMeterNumber();
                    
                case PHYSICAL_ADDRESS_COLUMN:
                    return mpData.getMeter().getAddress();
    
                case ROUTE_NAME_COLUMN:
                    return mpData.getMeter().getRoute();
                    
                case TIMESTAMP_COLUMN:
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
		    //Reupdate the string values if success meter model
		    if( getMeterReadType() == SUCCESS_METER_READ_TYPE)
		    {
                columnNames = new String[]{
                        DEVICE_NAME_STRING,
                        DISABLED_STRING,
                        PAO_TYPE_STRING,
                        METER_NUMBER_STRING,
                        PHYSICAL_ADDRESS_STRING,
                        ROUTE_NAME_STRING,
                        TIMESTAMP_STRING, 
                    };      
		    } else {
                columnNames = new String[]{
                        DEVICE_NAME_STRING,
                        DISABLED_STRING,
                        PAO_TYPE_STRING,
                        METER_NUMBER_STRING,
                        PHYSICAL_ADDRESS_STRING,
                        ROUTE_NAME_STRING
                    };      
            }

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
                    new ColumnProperties(0, 1, 220, null),
                    new ColumnProperties(220, 1, 60, null),
                    new ColumnProperties(280, 1, 70, null),
                    new ColumnProperties(350, 1, 70, null),
                    new ColumnProperties(420, 1, 70, null),
                    new ColumnProperties(490, 1, 120, null),
					new ColumnProperties(610, 1, 110, "MM/dd/yyyy HH:mm:ss"),
				};
		    }
		    else
		    {
		    	columnProperties = new ColumnProperties[]{
					//posX, posY, width, height, numberFormatString
	                new ColumnProperties(0, 1, 260, null),
	                new ColumnProperties(260, 1, 70, null),
	                new ColumnProperties(330, 1, 80, null),
	                new ColumnProperties(410, 1, 80, null),
	                new ColumnProperties(490, 1, 80, null),
	                new ColumnProperties(570, 1, 150, null)
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
	
	public int getDisabledDeviceStatus() {
        return disabledDeviceStatus;
    }

    public void setDisabledDeviceStatus(int disabledDeviceStatus) {
        this.disabledDeviceStatus = disabledDeviceStatus;
    }

	@Override
	public String getHTMLOptionsTable()
	{
		final StringBuilder sb = new StringBuilder();
        sb.append("<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("  <tr>" + LINE_SEPARATOR);
        sb.append("    <td valign='top'>" + LINE_SEPARATOR);		
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td valign='top' class='TitleHeader'>Meter Read Status</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);

        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='radio' onclick=\"$('GenerateMissedList').disabled=false;\" name='" + ATT_METER_READ_TYPE +"' value='" + MISSED_METER_READ_TYPE + "' checked>Missed Read" + LINE_SEPARATOR);
        sb.append("          </td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='radio' onclick=\"$('GenerateMissedList').disabled=true;\" name='" + ATT_METER_READ_TYPE +"' value='" + SUCCESS_METER_READ_TYPE + "' >Successful Read" + LINE_SEPARATOR);
        sb.append("          </td>" + LINE_SEPARATOR);
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
        
        sb.append("    <td valign='top'>" + LINE_SEPARATOR);        
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td valign='top' class='TitleHeader'>Disabled Devices</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='radio' name='" + ATT_DISABLED_DEVICE_STATUS +"' value='" + INCLUDE_DISABLED_DEVICE_STATUS + "' checked>Include" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='radio' name='" + ATT_DISABLED_DEVICE_STATUS +"' value='" + EXCLUDE_DISABLED_DEVICE_STATUS + "'>Exclude" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);
        
        sb.append("    <td valign='middle'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td>* Click this button to generate a list of missed meters that MACS can process.</td>"+ LINE_SEPARATOR);		
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='button' id='GenerateMissedList' name='GenerateMissedList' value='Generate Missed List' onclick='document.reportForm.ACTION.value=\"GenerateMissedMeterList\";reportForm.submit();'>"+ LINE_SEPARATOR);
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
			
			param = req.getParameter(ATT_DISABLED_DEVICE_STATUS);
			if( param != null)
			    setDisabledDeviceStatus(Integer.valueOf(param).intValue());
			else
			    setDisabledDeviceStatus(INCLUDE_DISABLED_DEVICE_STATUS);
		}		
	}

	@Override
	public void setFilterModelType(ReportFilter filterModelType)
	{
		if( getFilterModelType() != filterModelType )
			columnNames = null;
	    super.setFilterModelType(filterModelType);
	}
    
    public int compare(MeterAndPointData o1, MeterAndPointData o2) {

        String thisVal = NULL_STRING;
        String anotherVal = NULL_STRING;

        if( getOrderBy() == ORDER_BY_ROUTE_NAME)
        {
            thisVal = o1.getMeter().getRoute();
            anotherVal = o2.getMeter().getRoute();
        }
        else if( getOrderBy() == ORDER_BY_METER_NUMBER)
        {
            NaturalOrderComparator noComp = new NaturalOrderComparator(); 
            return noComp.compare(o1.getMeter().getMeterNumber(), o2.getMeter().getMeterNumber()); 
        }
        if (getOrderBy() == ORDER_BY_DEVICE_NAME || thisVal.equalsIgnoreCase(anotherVal))
        {
            thisVal = o1.getMeter().getName();
            anotherVal = o2.getMeter().getName();
            
            if( thisVal.equalsIgnoreCase(anotherVal)) {
                thisVal = o1.getPointName();
                anotherVal = o2.getPointName();
            }
        }
        
        return (thisVal.compareToIgnoreCase(anotherVal));
    }

}

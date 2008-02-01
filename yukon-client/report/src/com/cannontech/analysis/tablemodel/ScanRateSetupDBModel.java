package com.cannontech.analysis.tablemodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.MeterAndPointData;
import com.cannontech.analysis.data.device.ScanRateMeterData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.util.NaturalOrderComparator;

public class ScanRateSetupDBModel extends ReportModelBase<ScanRateMeterData> implements Comparator<ScanRateMeterData> {
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 8;
	
	/** Enum values for column representation */
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int DEVICE_TYPE_COLUMN = 1;
	public final static int METER_NUMBER_COLUMN = 2;
	public final static int ADDRESS_COLUMN = 3;
	public final static int ROUTE_NAME_COLUMN = 4;
	public final static int SCAN_TYPE_COLUMN = 5;
	public final static int INTERVAL_RATE_COLUMN = 6;
	public final static int ALTERNATE_RAT_COLUMN = 7;
	
	/** String values for column representation */
	public final static String DEVICE_NAME_STRING = "Meter Name";
	public final static String DEVICE_TYPE_STRING = "Type";
	public final static String METER_NUMBER_STRING = "Meter #";
	public final static String ADDRESS_STRING  = "Address";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String SCAN_TYPE_STRING = "Scan Type";
	public final static String INTERVAL_RATE_STRING = "Interval\r\nRate";
	public final static String ALTERNATE_RATE_STRING = "Alternate\r\nRate";
		
	public static final int ORDER_BY_DEVICE_NAME = 0;
	public static final int ORDER_BY_METER_NUMBER = 1;
	public static final int ORDER_BY_ROUTE_NAME = 2;
	private int orderBy = ORDER_BY_DEVICE_NAME;	//default
	private static final int[] ALL_ORDER_BYS = new int[]
	{
		ORDER_BY_DEVICE_NAME, ORDER_BY_METER_NUMBER, ORDER_BY_ROUTE_NAME
	};

	private static final String ATT_ORDER_BY = "orderBy";
	
	/** A string for the title of the data */
	private static String title = "Database Report";
	
	/**
	 * Default Constructor
	 */
	public ScanRateSetupDBModel()
	{
		super();
	}

	/**
	 * Add LPMeterData objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rs) {
		try {
            final Meter meter = new Meter();
            meter.setDeviceId(rs.getInt("PAOBJECTID"));
            meter.setName(rs.getString("PAONAME"));
            meter.setTypeStr(rs.getString("TYPE"));
            meter.setType(PAOGroups.getDeviceType(meter.getTypeStr()));
            meter.setDisabled(CtiUtilities.isTrue(rs.getString("DISABLEFLAG")));
            meter.setMeterNumber(rs.getString("METERNUMBER"));
            meter.setAddress(rs.getString("ADDRESS"));
            meter.setRouteId(rs.getInt("ROUTEPAOBJECTID"));
            meter.setRoute(rs.getString("ROUTEPAONAME"));

            final MeterAndPointData mpData = 
                new MeterAndPointData(
                    meter
                );

            DeviceScanRate deviceScanRate = new DeviceScanRate();
            deviceScanRate.setDeviceID(meter.getDeviceId());
            deviceScanRate.setScanType(rs.getString("SCANTYPE"));
            deviceScanRate.setIntervalRate(rs.getInt("INTERVALRATE"));
            deviceScanRate.setAlternateRate(rs.getInt("ALTERNATERATE"));
            
            getData().add(
                new ScanRateMeterData(
                    mpData,
                    deviceScanRate
                ));
            
		} catch(java.sql.SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve DatabaseModel data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuilder buildSQLStatement() {
		final StringBuilder sql = new StringBuilder();
        //SELECT
        sql.append(" SELECT DISTINCT PAO.PAOBJECTID, PAO.PAONAME, PAO.TYPE, PAO.DISABLEFLAG, ");
        sql.append(" DMG.METERNUMBER, DCS.ADDRESS, ROUTE.PAOBJECTID as ROUTEPAOBJECTID, ROUTE.PAONAME as ROUTEPAONAME, ");
        sql.append(" SCANTYPE, INTERVALRATE, ALTERNATERATE");
        
        //FROM
        sql.append(" FROM YUKONPAOBJECT PAO, DEVICESCANRATE DSR, DEVICEMETERGROUP DMG, DEVICECARRIERSETTINGS DCS, ");
        sql.append(" DEVICEROUTES DR, YUKONPAOBJECT ROUTE ");
        
        //WHERE
		sql.append(" WHERE PAO.PAOBJECTID = DSR.DEVICEID ");
        sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID ");
        sql.append(" AND PAO.PAOBJECTID = DCS.DEVICEID ");
        sql.append(" AND PAO.PAOBJECTID = DR.DEVICEID ");
        sql.append(" AND ROUTE.PAOBJECTID = DR.ROUTEID ");
        
        //ORDER
		sql.append("ORDER BY PAO.PAOBJECTID");
		return sql;
	}
		
	@Override
	public void collectData()
	{
		//Reset all objects, new data being collected!
		setData(null);
		
		StringBuilder sql = buildSQLStatement();
		CTILogger.info(sql.toString());	
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;

		try
		{
		    conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

		    if( conn == null )
		    {
		        CTILogger.error(getClass() + ":  Error getting database connection.");
		        return;
		    }
		    pstmt = conn.prepareStatement(sql.toString());
		    rset = pstmt.executeQuery();
		    while( rset.next())
		    {
		        addDataRow(rset);
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
        
        if(getData() != null) {
            //Order the records
            Collections.sort(getData(), this);
            if( getSortOrder() == DESCENDING)
                Collections.reverse(getData());             
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
	public Object getAttribute(int columnIndex, Object o) {
		
        if (o instanceof ScanRateMeterData) {
            
            final ScanRateMeterData scanRateData = (ScanRateMeterData) o;
            final MeterAndPointData mpData = scanRateData.getMeterAndPointData();
            
			switch( columnIndex) {
				case DEVICE_NAME_COLUMN:
                    return mpData.getMeter().getName();
				case DEVICE_TYPE_COLUMN:
                    return mpData.getMeter().getTypeStr();
				case METER_NUMBER_COLUMN:
                    return mpData.getMeter().getMeterNumber();
				case ADDRESS_COLUMN:
                    return mpData.getMeter().getAddress();
				case ROUTE_NAME_COLUMN:
                    return mpData.getMeter().getRoute();
				case SCAN_TYPE_COLUMN:
				    return scanRateData.getDeviceScanRate().getScanType();
				case INTERVAL_RATE_COLUMN:
				    return scanRateData.getDeviceScanRate().getIntervalRate();
				case ALTERNATE_RAT_COLUMN:
				    return scanRateData.getDeviceScanRate().getAlternateRate();
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
				DEVICE_TYPE_STRING,
				METER_NUMBER_STRING,
				ADDRESS_STRING,
				ROUTE_NAME_STRING,
				SCAN_TYPE_STRING,
				INTERVAL_RATE_STRING,
				ALTERNATE_RATE_STRING
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
				Integer.class,
				Integer.class
			};
		}
		return columnTypes;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null) {
			columnProperties = new ColumnProperties[]{
					new ColumnProperties(0, 1, 150, null),
					new ColumnProperties(150, 1, 75, null),
					new ColumnProperties(225, 1, 70, null),
					new ColumnProperties(295, 1, 70, null),
					new ColumnProperties(365, 1, 180, null),
					new ColumnProperties(545, 1, 75, null),
					new ColumnProperties(620, 1, 50, null),
					new ColumnProperties(670, 1, 50, null)
			};
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title + " - Scan Rate Meter Data";
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
				return "Meter Name";
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
    
	@Override
	public String getHTMLOptionsTable() {
	    final StringBuilder sb = new StringBuilder();
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
		}		
	}
    
    public int compare(ScanRateMeterData o1, ScanRateMeterData o2) {
        final MeterAndPointData mpData1 = o1.getMeterAndPointData();
        final MeterAndPointData mpData2 = o2.getMeterAndPointData();
        
        if (getOrderBy() == ORDER_BY_ROUTE_NAME) {
            return mpData1.getMeter().getRoute().compareToIgnoreCase(mpData2.getMeter().getRoute());
        }
        
        if (getOrderBy() == ORDER_BY_METER_NUMBER) {
            NaturalOrderComparator noComp = new NaturalOrderComparator();
            return noComp.compare(mpData1.getMeter().getMeterNumber(), mpData2.getMeter().getMeterNumber());
        }
        
        return mpData1.getMeter().getName().compareToIgnoreCase(mpData2.getMeter().getName());
    }
}

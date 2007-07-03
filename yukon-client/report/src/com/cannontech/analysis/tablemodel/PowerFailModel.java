package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.device.PowerFail;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

/**
 * Created on Dec 15, 2003
 * MissedMeterData TableModel object
 * Innerclass object for row data is MissedMeter:
 *  String collGroup	- DeviceMeterGroup.collectionGroup 
 *  String deviceName	- YukonPaobject.paoName
 *  String pointName	- Point.pointName
 *  Integer pointID		- Point.pointID
 *  String routeName  
 * @author snebben
 */
public class PowerFailModel extends ReportModelBase
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;

	/** Enum values for column representation */
	public final static int COLL_GROUP_NAME_COLUMN = 0;
	public final static int DEVICE_NAME_COLUMN = 1;
	public final static int POINT_NAME_COLUMN = 2;
	public final static int DATE_COLUMN = 3;
	public final static int TIME_COLUMN = 4;
	public final static int POWER_FAIL_COUNT_COLUMN = 5;
    public final static int INTERVAL_DIFFERENCE_COLUMN = 6;
    public final static int TOTAL_DIFFERENCE_COLUMN = 7;

	/** String values for column representation */
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
	public final static String POWER_FAIL_COUNT_STRING = "PF Count";
    public final static String INTERVAL_DIFFERENCE_STRING = "Interval Diff";
    public final static String TOTAL_DIFFERENCE_STRING = "Total Diff";

	/** A string for the title of the data */
	private static String title = "Power Fail Count By Collection Group";
    
    public final static String ATT_MINIMUM_DIFFERENCE = "minimumDifference";
    
    /** Temporary counters */
    private Integer previousCount = null;
    private String previousDevice = null;
    private String previousCollection = null;
    private Integer totalDifference = 0;
    private Integer minimumDifference = 0;
    private Vector tempData = new Vector();
    
	/**
	 * 
	 */
	public PowerFailModel()
	{
		super();
		setFilterModelTypes(new ReportFilter[]{
				ReportFilter.METER,
                ReportFilter.DEVICE,
				ReportFilter.COLLECTIONGROUP, 
    			ReportFilter.ALTERNATEGROUP, 
    			ReportFilter.BILLINGGROUP}
				);
	}
	
	/**
	 * 
	 */
	public PowerFailModel(Date start_, Date stop_)
	{
		super(start_, stop_);
	}
	/**
	 * Add MissedMeter objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	@SuppressWarnings("unchecked")
    public void addDataRow(ResultSet rset)
	{
		try
		{
            PowerFail powerFail;
			String collGrp = rset.getString(1);
			String paoName = rset.getString(2);
            String pointName = rset.getString(3);
            Timestamp timestamp = rset.getTimestamp(4);
            Integer powerFailCount = new Integer(rset.getInt(5));
            
            if( previousCollection != null && previousDevice != null) {
                
                if( !previousCollection.equals(collGrp) || !previousDevice.equals(paoName)) {
                    // new device or new collection grp or both
                    powerFail = new PowerFail(collGrp, paoName, pointName, new Date(timestamp.getTime()), powerFailCount, null, 0);
                    previousCount = powerFailCount;
                    previousDevice = paoName;
                    previousCollection = collGrp;
                    totalDifference = 0;
                }else {
                    //same device and collection grp
                    int intervalDif = powerFailCount - previousCount;
                    totalDifference = totalDifference + intervalDif;
                    powerFail = new PowerFail(collGrp, paoName, 
                                              pointName, 
                                              new Date(timestamp.getTime()),
                                              powerFailCount, 
                                              new Integer(powerFailCount - previousCount), 
                                              totalDifference);
                    previousCount = powerFailCount;
                    previousCollection = collGrp;
                    previousDevice = paoName;
                }
            }else {
                // first result
                powerFail = new PowerFail(collGrp, paoName, pointName, new Date(timestamp.getTime()), powerFailCount, null, totalDifference);
                previousCount = powerFailCount;
                previousCollection = collGrp;
                previousDevice = paoName;
            }
			

			tempData.add(powerFail);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}
    
    @SuppressWarnings("unchecked")
    public void loadTempData()
    {
        Vector tempDeviceVector = new Vector(); 
        for (int i = 0; i < tempData.size(); i ++) {
            PowerFail currentPF = (PowerFail)tempData.get(i);
            tempDeviceVector.add(currentPF);
            if(i+1 < tempData.size()) {
                PowerFail nextPF = (PowerFail)tempData.get(i + 1);
                if(!nextPF.getCollGroup().equals(currentPF.getCollGroup()) ||
                        !nextPF.getDeviceName().equals(currentPF.getDeviceName())) {
                    //done with this device's readings for this collection grp
                    if(currentPF.getTotalDifference() >= minimumDifference ) {
                        // add these device readings to the report and clear the holding vector
                        getData().addAll(tempDeviceVector);
                        tempDeviceVector = new Vector();
                    }else {
                        tempDeviceVector = new Vector();
                    }
                }
            }else {
                //very last item in tempData
                if(currentPF.getTotalDifference() >= minimumDifference ) {
                    // add these device readings to the report
                    getData().addAll(tempDeviceVector);
                }
            }
        }
    }
    
    @Override
    public String getHTMLOptionsTable()
    {
        String html = "";
        html += "      <table>" + LINE_SEPARATOR;
        html += "        <tr>" + LINE_SEPARATOR;
        html += "          <td>Minimum PF Difference: <input type='text' name='" + ATT_MINIMUM_DIFFERENCE +"' value='0' size='3'>";
        html += "          </td>" + LINE_SEPARATOR;
        html += "        </tr>" + LINE_SEPARATOR;
        html += "      </table>" + LINE_SEPARATOR;
        return html;
    }
    @Override
    public void setParameters( HttpServletRequest req )
    {
        super.setParameters(req);
        if( req != null)
        {
            String param = req.getParameter(ATT_MINIMUM_DIFFERENCE);
            if( param != null) {
                minimumDifference = (Integer.valueOf(param).intValue());
            }
        }
    }
    
    /**
	 * Build the SQL statement to retrieve PowerFail data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer	("SELECT DMG.COLLECTIONGROUP, PAO.PAONAME, P.POINTNAME, RPH.TIMESTAMP, RPH.VALUE " + 
			" FROM YUKONPAOBJECT PAO, DEVICEMETERGROUP DMG, POINT P, RAWPOINTHISTORY RPH "+
			" WHERE PAO.PAOBJECTID = DMG.DEVICEID  AND P.POINTID = RPH.POINTID"+
			" AND P.PAOBJECTID = PAO.PAOBJECTID AND P.POINTOFFSET = 20 ");
			
//		Use paoIDs in query if they exist			
		if( getPaoIDs() != null && getPaoIDs().length > 0)
		{
			sql.append(" AND PAO.PAOBJECTID IN (" + getPaoIDs()[0]);
			for (int i = 1; i < getPaoIDs().length; i++)
				sql.append(", " + getPaoIDs()[i]);
			sql.append(") ");
		}
					
		if( getBillingGroups() != null && getBillingGroups().length > 0)
		{
			sql.append(" AND " + getBillingGroupDatabaseString(getFilterModelType()) + " IN ( '" + getBillingGroups()[0]);
			for (int i = 1; i < getBillingGroups().length; i++)
				sql.append("', '" + getBillingGroups()[i]);
			sql.append("') ");
		}

			 
			sql.append(" AND RPH.TIMESTAMP > ? AND TIMESTAMP <= ? ORDER BY DMG.COLLECTIONGROUP, PAO.PAONAME, P.POINTNAME, TIMESTAMP");
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
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartDate().getTime()));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopDate().getTime()));
				CTILogger.info("START DATE > " + getStartDate() + " - STOP DATE <= " + getStopDate());
				rset = pstmt.executeQuery();
				
				while( rset.next())
				{
					addDataRow(rset);
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
        loadTempData();
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof PowerFail)
		{
			PowerFail meter = ((PowerFail)o); 
			switch( columnIndex)
			{
				case COLL_GROUP_NAME_COLUMN:
					return meter.getCollGroup();
		
				case DEVICE_NAME_COLUMN:
					return meter.getDeviceName();
	
				case POINT_NAME_COLUMN:
					return meter.getPointName();
	
				case DATE_COLUMN:
					return meter.getTimestamp();

				case TIME_COLUMN:
					return meter.getTimestamp();
					
				case POWER_FAIL_COUNT_COLUMN:
					return meter.getPowerFailCount();
                    
                case INTERVAL_DIFFERENCE_COLUMN:
                    return meter.getIntervalDifference();
                    
                case TOTAL_DIFFERENCE_COLUMN:
                    return meter.getTotalDifference();
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
				COLL_GROUP_NAME_STRING,
				DEVICE_NAME_STRING,
				POINT_NAME_STRING,
				DATE_STRING,
				TIME_STRING,
				POWER_FAIL_COUNT_STRING,
                INTERVAL_DIFFERENCE_STRING,
                TOTAL_DIFFERENCE_STRING
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
				Date.class,
				Integer.class,
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
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
				//posX, posY, width, height, numberFormatString
//				new ColumnProperties(0, 1, 200, null),
//				new ColumnProperties(0, 1, 160, null),
//				new ColumnProperties(160, 1, 160, null),
//				new ColumnProperties(320, 1, 75, "MM/dd/yyyy"),
//				new ColumnProperties(395, 1, 75, "HH:mm:ss"),
//				new ColumnProperties(470, 1, 80, "#")
                    
                new ColumnProperties(0, 1, 200, null),
                new ColumnProperties(0, 1, 120, null),
                new ColumnProperties(120, 1, 100, null),
                new ColumnProperties(220, 1, 75, "MM/dd/yyyy"),
                new ColumnProperties(295, 1, 50, "HH:mm:ss"),
                new ColumnProperties(345, 1, 50, "#"),
                new ColumnProperties(395, 1, 70, "#"),
                new ColumnProperties(465, 1, 50, "#"),
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
}

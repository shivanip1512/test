package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.data.device.capcontrol.MaxDailyOpsData;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

public class CapBankRecentMaxDailyOpsModel extends FilterObjectsReportModelBase<Object> {
    
    /** Number of columns */
    protected final int NUMBER_COLUMNS = 7;
    
    /** Enum values for column representation */
    public final static int REGION_COLUMN = 0;
    public final static int SUB_NAME_COLUMN = 1;
    public final static int FEEDER_NAME_COLUMN = 2;
    public final static int CBC_NAME_COLUMN = 3;
    public final static int CAP_NAME_COLUMN = 4;
    public final static int CURRENT_WEEK_COLUMN = 5;
    public final static int PREV_WEEK_1_COLUMN = 6;
    public final static int PREV_WEEK_2_COLUMN = 7;
    public final static int PREV_WEEK_3_COLUMN = 8;
    public final static int PREV_WEEK_4_COLUMN = 9;
    public final static int PREV_WEEK_5_COLUMN = 10;
    public final static int PREV_WEEK_6_COLUMN = 11;
    
    /** String values for column representation */
    public final static String REGION_STRING = "Area";
    public final static String SUB_NAME_STRING = "Substation Bus";
    public final static String FEEDER_NAME_STRING = "Feeder";
    public final static String CBC_NAME_STRING = "CBC";
    public final static String CAP_NAME_STRING = "Cap Bank";
    public final static String CURRENT_WEEK_STRING = "Current Week";
    public final static String PREV_WEEK_1_STRING = "Prev Week 1";
    public final static String PREV_WEEK_2_STRING = "Prev Week 2";
    public final static String PREV_WEEK_3_STRING = "Prev Week 3";
    public final static String PREV_WEEK_4_STRING = "Prev Week 4";
    public final static String PREV_WEEK_5_STRING = "Prev Week 5";
    public final static String PREV_WEEK_6_STRING = "Prev Week 6";
    
    /** A string for the title of the data */
    private static String title = "Cap Bank Recent Max Daily Operations Report";

    /**
     * Default Constructor
     */
    public CapBankRecentMaxDailyOpsModel()
    {
        super();
        setFilterModelTypes(new ReportFilter[]{
                ReportFilter.AREA,
                ReportFilter.CAPCONTROLSUBSTATION,
                ReportFilter.CAPCONTROLSUBBUS,
                ReportFilter.CAPCONTROLFEEDER,
         	 	ReportFilter.CAPBANK}
            );
    }
    
    /**
     * Build the SQL statement to retrieve DatabaseModel data.
     * @return StringBuffer  an sqlstatement
     */
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select ca.paoname Region, ");
        sql.append( "yp3.paoname SubName, yp2.paoname FeederName, yp.paoname cbcName, yp1.paoname capName, ");
        sql.append( "slTemp.currentWeekCount, slTemp1.prevWeek1Count, slTemp2.prevWeek2Count, slTemp3.prevWeek3Count, slTemp4.prevWeek4Count, ");
        sql.append( "slTemp5.prevWeek5Count, slTemp6.prevWeek6Count ");
        sql.append( "from capbank cb ");
        sql.append( "join point p on p.paobjectid = cb.deviceid and p.pointname = 'OPERATION' " );
        sql.append( "left outer join (select pointid, count(*) currentWeekCount " );
        sql.append( "from systemlog where description like '%CapBank Exceeded Max%' " );
        sql.append( "and (datetime <= ? and datetime > ?) group by pointid) slTemp on slTemp.pointid = p.pointid ");
        sql.append( "left outer join (select pointid, count(*) prevWeek1Count ");
        sql.append( "from systemlog where description like '%CapBank Exceeded Max%' ");
        sql.append( "and (datetime <= ? and ");
        sql.append( "datetime > ?) group by pointid) slTemp1 on slTemp1.pointid = p.pointid ");
        sql.append( "left outer join (select pointid, count(*) prevWeek2Count ");
        sql.append( "from systemlog where description like '%CapBank Exceeded Max%' ");
        sql.append( "and (datetime <= ? and ");
        sql.append( "datetime > ?) group by pointid) slTemp2 on slTemp2.pointid = p.pointid ");
        sql.append( "left outer join (select pointid, count(*) prevWeek3Count ");
        sql.append( "from systemlog where description like '%CapBank Exceeded Max%' ");
        sql.append( "and (datetime <= ? and ");
        sql.append( "datetime > ?) group by pointid) slTemp3 on slTemp3.pointid = p.pointid ");
        sql.append( "left outer join (select pointid, count(*) prevWeek4Count ");
        sql.append( "from systemlog where description like '%CapBank Exceeded Max%' ");
        sql.append( "and (datetime <= ? and ");
        sql.append( "datetime > ?) group by pointid) slTemp4 on slTemp4.pointid = p.pointid ");
        sql.append( "left outer join (select pointid, count(*) prevWeek5Count ");
        sql.append( "from systemlog where description like '%CapBank Exceeded Max%' ");
        sql.append( "and (datetime <= ? and ");
        sql.append( "datetime > ?) group by pointid) slTemp5 on slTemp5.pointid = p.pointid ");
        sql.append( "left outer join (select pointid, count(*) prevWeek6Count ");
        sql.append( "from systemlog where description like '%CapBank Exceeded Max%' ");
        sql.append( "and (datetime <= ? and ");
        sql.append( "datetime > ?) group by pointid) slTemp6 on slTemp6.pointid = p.pointid ");
        sql.append( "join yukonpaobject yp1 on yp1.paobjectid = cb.deviceid ");
        sql.append( "join yukonpaobject yp on yp.paobjectid = cb.controldeviceid ");
        sql.append( "left outer join ccfeederbanklist fb on fb.deviceid = cb.deviceid ");
        sql.append( "join yukonpaobject yp2 on yp2.paobjectid = fb.feederid ");
        sql.append( "left outer join ccfeedersubassignment fs on fs.feederid = fb.feederid ");
        sql.append( "join yukonpaobject yp3 on yp3.paobjectid = fs.substationbusid ");
        sql.append( "left outer join ccsubstationsubbuslist ssb on ssb.substationbusid = fs.substationbusid ");
        sql.append( "join yukonpaobject yp4 on yp4.paobjectid = ssb.substationid ");
        sql.append( "left outer join ccsubareaassignment sa on sa.substationbusid = ssb.substationid ");
        sql.append( "join yukonpaobject ca on ca.paobjectid = sa.areaid ");
        sql.append( "and (slTemp6.prevWeek6Count > 0 or slTemp5.prevWeek5Count > 0 or "); 
        sql.append( "slTemp4.prevWeek4Count > 0 or slTemp3.prevWeek3Count > 0 or ");
        sql.append( "slTemp2.prevWeek2Count > 0 or slTemp1.prevWeek1Count > 0 or ");
        sql.append( "slTemp.currentWeekCount > 0 ) ");
        
        if (getPaoIDs() != null && getPaoIDs().length > 0) {
            if(getFilterModelType().equals(ReportFilter.CAPCONTROLFEEDER)) {
                sql.append( "where yp2.paobjectid in ('" + getPaoIDs()[0]);
                for (int i = 1; i < getPaoIDs().length; i++) {
                    sql.append("', '" + getPaoIDs()[i]);
                }
                sql.append("') ");
            }
        
            if(getFilterModelType().equals(ReportFilter.CAPBANK)) {
                sql.append( "where yp1.paobjectid in ('" + getPaoIDs()[0]);
                for (int i = 1; i < getPaoIDs().length; i++) {
                    sql.append("', '" + getPaoIDs()[i]);
                }
                sql.append("') ");
            }
        
            if(getFilterModelType().equals(ReportFilter.CAPCONTROLSUBBUS)) {
                sql.append( "where yp3.paobjectid in ('" + getPaoIDs()[0]);
                for (int i = 1; i < getPaoIDs().length; i++) {
                    sql.append("', '" + getPaoIDs()[i]);
                }
                sql.append("') ");
            }
            
            if(getFilterModelType().equals(ReportFilter.CAPCONTROLSUBSTATION)) {
                sql.append( "where yp4.paobjectid in ('" + getPaoIDs()[0]);
                for (int i = 1; i < getPaoIDs().length; i++) {
                    sql.append("', '" + getPaoIDs()[i]);
                }
                sql.append("') ");
            }
            
            if(getFilterModelType().equals(ReportFilter.CAPCONTROLSUBSTATION)) {
                sql.append( "where ssb.substationid in ('" + getPaoIDs()[0]);
                for (int i = 1; i < getPaoIDs().length; i++) {
                    sql.append("', '" + getPaoIDs()[i]);
                }
                sql.append("') ");
            }
            
            if(getFilterModelType().equals(ReportFilter.AREA)) {
         	 	sql.append( "and ca.paobjectid in ('" + getPaoIDs()[0]);
         	 	for (int i = 1; i < getPaoIDs().length; i++) {
         	 	    sql.append("', '" + getPaoIDs()[i]);
         	 	}
         	 	sql.append("') ");
            }
                      
            
        }
        return sql;
    }
    
    public Date getDateNow() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(new Date());
        
        return cal.getTime();
    }
    
    // returns the beginning of last sunday
    public Date getLastSunday() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(getStopDate());
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);
        
        if ( currentDay == 1) {  // today is sunday
            // no adjustment neeeded
        }else if ( currentDay == 2) {  // monday
            cal.add( Calendar.DAY_OF_YEAR, -1 );
        }else if ( currentDay == 3) {  // tuesday
            cal.add( Calendar.DAY_OF_YEAR, -2 );
        }else if ( currentDay == 4) {  // wednesday
            cal.add( Calendar.DAY_OF_YEAR, -3 );
        }else if ( currentDay == 5) {  // thursday
            cal.add( Calendar.DAY_OF_YEAR, -4 );
        }else if ( currentDay == 6) {  // friday
            cal.add( Calendar.DAY_OF_YEAR, -5 );
        }else if ( currentDay == 7) {  // saturday
            cal.add( Calendar.DAY_OF_YEAR, -6 );
        }
        return cal.getTime();
    }
    
    // returns the previous weeks start time
    public Date getPrevWeekStartDate(int week) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(getLastSunday());
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        if( week == 1 ) {
            cal.add(Calendar.DAY_OF_YEAR, -7);
        }else if ( week == 2 ) {
            cal.add(Calendar.DAY_OF_YEAR, -14);
        }else if ( week == 3 ) {
            cal.add(Calendar.DAY_OF_YEAR, -21);
        }else if ( week == 4 ) {
            cal.add(Calendar.DAY_OF_YEAR, -28);
        }else if ( week == 5 ) {
            cal.add(Calendar.DAY_OF_YEAR, -35);
        }else if ( week == 6 ) {
            cal.add(Calendar.DAY_OF_YEAR, -42);
        }
        return cal.getTime();
    }
    
    // return the previous weeks stop time
    public Date getPrevWeekStopDate(int week) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(getLastSunday());
        cal.set( Calendar.HOUR_OF_DAY, 23 );
        cal.set( Calendar.MINUTE, 59 );
        cal.set( Calendar.SECOND, 59 );
        if( week == 1 ) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }else if ( week == 2 ) {
            cal.add(Calendar.DAY_OF_YEAR, -8);
        }else if ( week == 3 ) {
            cal.add(Calendar.DAY_OF_YEAR, -15);
        }else if ( week == 4 ) {
            cal.add(Calendar.DAY_OF_YEAR, -22);
        }else if ( week == 5 ) {
            cal.add(Calendar.DAY_OF_YEAR, -29);
        }else if ( week == 6 ) {
            cal.add(Calendar.DAY_OF_YEAR, -36);
        }
        return cal.getTime();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void collectData() {
//      Reset all objects, new data being collected!
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
                
                pstmt.setTimestamp(1, new java.sql.Timestamp( getStopDate().getTime() ));
                pstmt.setTimestamp(2, new java.sql.Timestamp( getLastSunday().getTime() ));
                pstmt.setTimestamp(3, new java.sql.Timestamp( getPrevWeekStopDate(1).getTime() ));
                pstmt.setTimestamp(4, new java.sql.Timestamp( getPrevWeekStartDate(1).getTime() ));
                pstmt.setTimestamp(5, new java.sql.Timestamp( getPrevWeekStopDate(2).getTime() ));
                pstmt.setTimestamp(6, new java.sql.Timestamp( getPrevWeekStartDate(2).getTime() ));
                pstmt.setTimestamp(7, new java.sql.Timestamp( getPrevWeekStopDate(3).getTime() ));
                pstmt.setTimestamp(8, new java.sql.Timestamp( getPrevWeekStartDate(3).getTime() ));
                pstmt.setTimestamp(9, new java.sql.Timestamp( getPrevWeekStopDate(4).getTime() ));
                pstmt.setTimestamp(10, new java.sql.Timestamp( getPrevWeekStartDate(4).getTime() ));
                pstmt.setTimestamp(11, new java.sql.Timestamp( getPrevWeekStopDate(5).getTime() ));
                pstmt.setTimestamp(12, new java.sql.Timestamp( getPrevWeekStartDate(5).getTime() ));
                pstmt.setTimestamp(13, new java.sql.Timestamp( getPrevWeekStopDate(6).getTime() ));
                pstmt.setTimestamp(14, new java.sql.Timestamp( getPrevWeekStartDate(6).getTime() ));
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
        	SqlUtils.close(rset, pstmt, conn );
        }
        CTILogger.info("Report Records Collected from Database: " + getData().size());
        return;
    }
    
    /**
     * Add <innerClass> objects to data, retrieved from rset.
     * @param ResultSet rset
     */
    public void addDataRow(ResultSet rset)
    {
        try
        {
            String region = rset.getString(1);
            String  subName = rset.getString(2);
            String feeder = rset.getString(3);
            String cbc = rset.getString(4);
            String cap = rset.getString(5);
            Integer currentWeek = new Integer(rset.getInt(6));
            Integer prevWeek1 = new Integer(rset.getInt(7));
            Integer prevWeek2 = new Integer(rset.getInt(8));
            Integer prevWeek3 = new Integer(rset.getInt(9));
            Integer prevWeek4 = new Integer(rset.getInt(10));
            Integer prevWeek5 = new Integer(rset.getInt(11));
            Integer prevWeek6 = new Integer(rset.getInt(12));
            
            MaxDailyOpsData data = new MaxDailyOpsData(region, subName, feeder, cbc, cap, currentWeek, prevWeek1, prevWeek2, prevWeek3, prevWeek4, prevWeek5, prevWeek6);
            getData().add(data);
        }
        catch(java.sql.SQLException e)
        {
            e.printStackTrace();
        }
    }

    public Object getAttribute(int columnIndex, Object o) {
        if ( o instanceof MaxDailyOpsData)
        {
            MaxDailyOpsData data = ((MaxDailyOpsData)o);
            
            switch( columnIndex)
            {
                case REGION_COLUMN:
                    return data.getRegion();
    
                case SUB_NAME_COLUMN:
                    return data.getSubName();
                    
                case FEEDER_NAME_COLUMN:
                    return data.getFeeder();
                    
                case CBC_NAME_COLUMN:
                    return data.getCBC();
                    
                case CAP_NAME_COLUMN:
                    return data.getCap();
                    
                case CURRENT_WEEK_COLUMN:
                    return data.getCurrentWeek();
                    
                case PREV_WEEK_1_COLUMN:
                    return data.getPrevWeek1();
                    
                case PREV_WEEK_2_COLUMN:
                    return data.getPrevWeek2();
                    
                case PREV_WEEK_3_COLUMN:
                    return data.getPrevWeek3();
                    
                case PREV_WEEK_4_COLUMN:
                    return data.getPrevWeek4();
                    
                case PREV_WEEK_5_COLUMN:
                    return data.getPrevWeek5();
                    
                case PREV_WEEK_6_COLUMN:
                    return data.getPrevWeek6();
            }
        }
        return null;
    }

    public String[] getColumnNames() {
        if( columnNames == null)
        {
            columnNames = new String[]{
                    REGION_STRING,
                    SUB_NAME_STRING,
                    FEEDER_NAME_STRING,
                    CBC_NAME_STRING,
                    CAP_NAME_STRING,
                    CURRENT_WEEK_STRING,
                    PREV_WEEK_1_STRING,
                    PREV_WEEK_2_STRING,
                    PREV_WEEK_3_STRING,
                    PREV_WEEK_4_STRING,
                    PREV_WEEK_5_STRING,
                    PREV_WEEK_6_STRING,
            };
        }
        return columnNames;
    }

    public ColumnProperties[] getColumnProperties() {
        if(columnProperties == null)
        {
            columnProperties = new ColumnProperties[]{
                new ColumnProperties(0, 1, 60, null),
                new ColumnProperties(60, 1, 60, null),
                new ColumnProperties(120, 1, 60, null),
                new ColumnProperties(180, 1, 60, null),
                new ColumnProperties(240, 1, 60, null),
                new ColumnProperties(300, 1, 60, null),
                new ColumnProperties(360, 1, 60, null),
                new ColumnProperties(420, 1, 60, null),
                new ColumnProperties(480, 1, 60, null),
                new ColumnProperties(540, 1, 60, null),
                new ColumnProperties(600, 1, 60, null),
                new ColumnProperties(660, 1, 60, null),
            };
        }
        return columnProperties;
    }

    public Class[] getColumnTypes() {
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
                String.class,
                String.class,
                String.class,
                String.class,
            };
        }
        return columnTypes;
    }

    public String getTitleString() {
        return title ;
    }
}

package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;

public class MeterKwhDifferenceModel extends BareDatedReportModelBase<MeterKwhDifferenceModel.ModelRow> {
    
    private static String title = "Meter Kwh Difference Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> deviceIds;
//    private Set<Integer> groupIds;
    private Double previousKwh = null;
    private String previousDevice= null;
    
    static public class ModelRow {
        public String deviceName;
        public String meterNumber;
        public Timestamp date;
        public Double kWh;
        public Double previousKwh;
        public Double difference;
    }
    
    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        Timestamp[] dateRange = {new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime())};
        jdbcOps.query(sql.toString(), dateRange, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                MeterKwhDifferenceModel.ModelRow row = new MeterKwhDifferenceModel.ModelRow();

                String deviceName = rs.getString("deviceName");
                row.deviceName = deviceName;
                row.meterNumber = rs.getString("meterNumber");
                row.date = rs.getTimestamp("date");
                Double kWh = rs.getDouble("kWh"); 
                row.kWh = kWh;
                
                if(previousDevice != null) {
                    if(!deviceName.equals(previousDevice)) {
                        data.add(new MeterKwhDifferenceModel.ModelRow());
                        previousKwh = null;
                    }
                }
                
                row.previousKwh = previousKwh;
                
                if(previousKwh != null) {
                    row.difference = kWh - previousKwh;
                }else {
                    row.difference = null;
                }
                data.add(row);
                previousKwh = kWh;
                previousDevice = deviceName;
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("SELECT DISTINCT PAO.PAOBJECTID,PAO.PAONAME as deviceName, PAO.TYPE, DMG.METERNUMBER as meterNumber, "); 
        sql.append("DMG.COLLECTIONGROUP as collectionGroup, P.POINTNAME, TIMESTAMP as date, VALUE as kwh ");
        sql.append("FROM RAWPOINTHISTORY RPH, POINT P, YUKONPAOBJECT PAO ");
        sql.append("left outer join DEVICEMETERGROUP DMG on PAO.PAOBJECTID = DMG.DEVICEID ");
        sql.append("left outer join DEVICECARRIERSETTINGS DCS on PAO.PAOBJECTID = DCS.DEVICEID ");
        sql.append("WHERE P.POINTID = RPH.POINTID ");
        sql.append("AND P.PAOBJECTID = PAO.PAOBJECTID ");
        sql.append("AND TIMESTAMP > ? ");
        sql.append("AND TIMESTAMP <= ? ");
        sql.append("AND P.POINTTYPE = 'PulseAccumulator' ");
        sql.append("AND P.POINTOFFSET = 1 ");
        sql.append(" ");
        
 
        String result = null;
        
        if(deviceIds != null && !deviceIds.isEmpty()) {
            result = "DMG.meternumber in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(deviceIds);
            result += wheres;
            result += " ) ";
        }
//        else if(groupIds != null && !groupIds.isEmpty()) {
//            result = "DMG.collectiongroupid in ( ";
//            String wheres = SqlStatementBuilder.convertToSqlLikeList(groupIds);
//            result += wheres;
//            result += " ) ";
//        }
        
        if (result != null) {
            sql.append(" and ");
            sql.append(result);
        }
        
        sql.append(";");
        return sql;
    }

    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getTitle() {
        return title;
    }
    
    public void setDeviceIdsFilter(Set<Integer> deviceIds) {
        this.deviceIds = deviceIds;
    }
    
    public void setGroupIdsFilter(Set<Integer> groupIds) {
//        this.groupIds = groupIds;
    }

}

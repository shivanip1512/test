package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class DeviceRequestDetailModel extends DeviceReportModelBase<DeviceRequestDetailModel.ModelRow> {

    private Logger log = YukonLogManager.getLogger(DeviceRequestDetailModel.class);

    // dependencies
    private SimpleJdbcOperations simpleJdbcTemplate;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private boolean lifetime = false;

    static public class ModelRow {
        public String deviceName;
        public String type;
        public String route;
        public Integer requests;
        public String success;
        public Integer numberOfSuccesses;
        public String successPercent;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    @Override
    public String getTitle() {
        return "Device Request Detail Report";
    }

    @Override
    public int getRowCount() {
        return data.size();
    }
    
    private SqlFragmentSource getSqlSource(Collection<Integer> subList){
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if(!lifetime){
            sql.append("select ypo.paoname deviceName, ypo.type type, route.paoname route, sum(requests) requests, sum(attempts) attempts, sum(completions) completions");
            sql.append("from DYNAMICPAOSTATISTICSHISTORY dpsh");
            sql.append("  join YukonPAObject ypo on ypo.PAObjectID = dpsh.PAObjectID");
            sql.append("  join DeviceRoutes dr on dr.DEVICEID = dpsh.PAObjectID");
            sql.append("join YukonPAObject route on route.PAObjectID = dr.ROUTEID");
            sql.append("WHERE dpsh.PAObjectID IN (").appendArgumentList(subList).append(")");
            sql.append("  and DateOffset").gte(getStartDateOffset()).append("and DateOffset").lte(getStopDateOffset());
            sql.append("group by ypo.PAOName,ypo.type, route.PAOName");
        } else {
            sql.append("select ypo.paoname deviceName, ypo.type type, route.paoname route, requests, attempts, completions");
            sql.append("from DYNAMICPAOSTATISTICS dps");
            sql.append("  join YukonPAObject ypo on ypo.PAObjectID = dps.PAObjectID");
            sql.append("  join DeviceRoutes dr on dr.DEVICEID = dps.PAObjectID");
            sql.append("  join YukonPAObject route on route.PAObjectID = dr.ROUTEID");
            sql.append("WHERE dps.PAObjectID IN (").appendArgumentList(subList).append(")");
            sql.append("  and dps.StatisticType = 'Lifetime'");
            sql.append("group by ypo.PAOName,ypo.type, route.PAOName, Requests, Attempts, Completions");
        }
        return sql;
    }
    
    private long getStartDateOffset() {
        long startDateMillis = getStartDate().getTime();
        long startOffset = ((((startDateMillis / 1000) / 60) / 60) / 24);
        return startOffset;
    }
    
    private long getStopDateOffset(){
        long stopDateMillis = getStopDate().getTime();
        long stopOffset = ((((stopDateMillis / 1000) / 60) / 60) / 24);
        return stopOffset;
    }

    @Override
    public void doLoadData() {
        List<SimpleDevice> devices = getDeviceList();
        
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(simpleJdbcTemplate);
        SqlFragmentGenerator<SimpleDevice> gen = new SqlFragmentGenerator<SimpleDevice>() {
            @Override
            public SqlFragmentSource generate(List<SimpleDevice> subList) {
                Collection<Integer> deviceIds = Collections2.transform(subList, new Function<SimpleDevice, Integer>() {
                    @Override
                    public Integer apply(SimpleDevice simpleDevice) {
                        return simpleDevice.getDeviceId();
                    }
                });
                return getSqlSource(deviceIds);
            }
        };
        
        List<ModelRow> rows = template.query(gen, devices, new ParameterizedRowMapper<ModelRow>() {
            @Override
            public ModelRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                DeviceRequestDetailModel.ModelRow row = new DeviceRequestDetailModel.ModelRow();
                row.deviceName = rs.getString("deviceName");
                row.type = rs.getString("type");
                row.route = rs.getString("route");
                int requests = rs.getInt("requests");
                row.requests = requests;
                int completions = rs.getInt("completions");
                if(completions > 0){
                    int attempts = rs.getInt("attempts");
                    double success =  new Double(attempts) / new Double(completions);
                    DecimalFormat formatter = new DecimalFormat("#####.##");
                    String successString = formatter.format(success);
                    row.success = successString;
                } else {
                    row.success = "---";
                }
                row.numberOfSuccesses = completions;
                double successPercent = new Double(completions) / new Double(requests);
                successPercent = successPercent * 100.0;
                DecimalFormat df = new DecimalFormat("###.##");
                row.successPercent = df.format(successPercent) + "%";
                
                return row;
            }
        });
        
        data.addAll(rows);
            
        log.info("Report Records Collected from Database: " + data.size());
        
    }
    
    public void setLifetime(boolean lifetime){
        this.lifetime = lifetime;
    }
    
    public boolean isLifetime(){
        return lifetime;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
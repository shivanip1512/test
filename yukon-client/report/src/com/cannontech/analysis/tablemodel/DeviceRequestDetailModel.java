package com.cannontech.analysis.tablemodel;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class DeviceRequestDetailModel extends DeviceReportModelBase<DeviceRequestDetailModel.ModelRow> {

    private Logger log = YukonLogManager.getLogger(DeviceRequestDetailModel.class);

    // dependencies
    private YukonJdbcTemplate yukonJdbcTemplate;
    
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
    
    private SqlFragmentSource getSqlSource(){
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        if(lifetime){
            sql.append("select ypo.paoname deviceName, ypo.type type, route.paoname route, requests, attempts, completions");
        } else {
            sql.append("select ypo.paoname deviceName, ypo.type type, route.paoname route, sum(requests) requests, sum(attempts) attempts, sum(completions) completions");
        }
        
        sql.append("from DYNAMICPAOSTATISTICS dps");
        sql.append("  join YukonPAObject ypo on ypo.PAObjectID = dps.PAObjectID");
        sql.append("  join DeviceRoutes dr on dr.DEVICEID = dps.PAObjectID");
        sql.append("join YukonPAObject route on route.PAObjectID = dr.ROUTEID");
        sql.append("WHERE").append(getFilterSqlWhereClause());
        
        if(lifetime){
            sql.append("  and dps.StatisticType = 'Lifetime'");
        } else {
            sql.append("  and dps.StatisticType = 'Daily'");
            sql.append("  and StartDateTime").gte(getStartDate()).append("and StartDateTime").lt(getStopDate());
            sql.append("group by ypo.PAOName,ypo.type, route.PAOName");
        }

        return sql;
    }
    
    @Override
    public void doLoadData() {
    	SqlFragmentSource sql = getSqlSource();
        List<ModelRow> rows = yukonJdbcTemplate.query(sql, new YukonRowMapper<ModelRow>() {
            @Override
            public ModelRow mapRow(YukonResultSet rs) throws SQLException {
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
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}

	@Override
	public String getPaoIdIdentifier() {
		return "ypo.paobjectid";
	}

	@Override
	public String getPaoNameIdentifer() {
		return "ypo.paoname";
	}
}
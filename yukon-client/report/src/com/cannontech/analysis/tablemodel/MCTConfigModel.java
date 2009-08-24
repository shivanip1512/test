package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;


public class MCTConfigModel extends BareReportModelBase<MCTConfigModel.ModelRow> {
    
    // dependencies
    private JdbcOperations jdbcOps;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();

    public MCTConfigModel() {
    }
    
    static public class ModelRow {
        public String mctName;
        public String type;
        public String demandRate;
        public String profileRate;
        public String configName;
        public String configDemandRate;
        public String configProfileRate;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }
    
    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }
    
    public String getTitle() {
        return "MCT 430/470 Config To Device Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
            
        String sql = buildSQLStatement();
        
        jdbcOps.query(sql, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                MCTConfigModel.ModelRow row = new MCTConfigModel.ModelRow();

                row.mctName = rs.getString("mctName");
                row.type = rs.getString("type");
                
                int demandRate = rs.getInt("demandRate");
                Integer demandRateMinutes = demandRate / 60; 
                row.demandRate = demandRateMinutes.toString();
                
                int profileRate = rs.getInt("profileRate");
                Integer profileRateMinutes = profileRate / 60; 
                row.profileRate = profileRateMinutes.toString();
                
                row.configName = rs.getString("configName");
                row.configDemandRate = rs.getString("configDemandRate");
                row.configProfileRate = rs.getString("configProfileRate");
                
                data.add(row);
            }
        });
            
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public String buildSQLStatement() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select");
        sql.append("yp.paoname as mctName");
        sql.append(", yp.type");
        sql.append(", dlp.LASTINTERVALDEMANDRATE as demandRate");
        sql.append(", dlp.LOADPROFILEDEMANDRATE as profileRate");
        sql.append(", dc.Name as configName");
        sql.append(", dci.Value as configDemandRate");
        sql.append(", dci2.value as configProfileRate");
        sql.append("from YukonPAObject yp");
        sql.append("join dEVICELOADPROFILE dlp on yp.PAObjectID = dlp.DEVICEID and (yp.Type like 'MCT-430%' or yp.Type like 'MCT-470%')");
        sql.append("left outer join DEVICECONFIGURATIONDEVICEMAP dcm on dcm.DeviceId = yp.paobjectid");
        sql.append("left outer join DEVICECONFIGURATION dc on dc.DeviceConfigurationID = dcm.DeviceConfigurationId");
        sql.append("left join DEVICECONFIGURATIONITEM dci on dc.DeviceConfigurationID = dci.DeviceConfigurationID");
        sql.append("and dci.FieldName = 'Demand Interval'");
        sql.append("left outer join DEVICECONFIGURATIONITEM dci2 on dc.DeviceConfigurationID = dci2.DeviceConfigurationID");
        sql.append("and dci2.FieldName = 'Load Profile Interval 1'");
        
        return sql.toString();
    }

    @Required
    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }

}

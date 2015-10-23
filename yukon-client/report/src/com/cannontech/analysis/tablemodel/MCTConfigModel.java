package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;

public class MCTConfigModel extends BareReportModelBase<MCTConfigModel.ModelRow> {
    private Logger log = YukonLogManager.getLogger(MCTConfigModel.class);
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private List<ModelRow> data = new ArrayList<ModelRow>();

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
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YP.PAOName AS MctName, YP.Type, DLP.LastIntervalDemandRate AS DemandRate, ");
        sql.append("   DLP.LoadProfileDemandRate AS ProfileRate, DC.Name AS ConfigName, ");
        sql.append("   DCI.ItemValue AS ConfigDemandRate, DCI2.ItemValue AS ConfigProfileRate");
        sql.append("FROM YukonPaObject YP");
        sql.append("   JOIN DeviceLoadProfile DLP ON YP.PAObjectID = DLP.DEVICEID");
        sql.append("      AND (YP.Type LIKE 'MCT-430%' OR YP.Type LIKE 'MCT-470%')");
        sql.append("   LEFT JOIN DeviceConfigurationDeviceMap DCM ON DCM.DeviceId = YP.paobjectid");
        sql.append("   LEFT JOIN DeviceConfiguration DC ON DC.DeviceConfigurationID = DCM.DeviceConfigurationId");
        sql.append("   LEFT JOIN DeviceConfigCategoryItem DCI ON DC.DeviceConfigurationID = DCI.DeviceConfigCategoryId");
        sql.append("      AND DCI.ItemName = 'demandInterval'");
        sql.append("   LEFT JOIN DeviceConfigCategoryItem DCI2 ON DCI2.DeviceConfigCategoryId = DCM.DeviceConfigurationId");
        sql.append("      AND DCI2.ItemName = 'loadProfileInterval1'");
        
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                MCTConfigModel.ModelRow row = new MCTConfigModel.ModelRow();

                row.mctName = rs.getString("MctName");
                row.type = rs.getString("Type");
                
                int demandRate = rs.getInt("DemandRate");
                Integer demandRateMinutes = demandRate / 60; 
                row.demandRate = demandRateMinutes.toString();
                
                int profileRate = rs.getInt("ProfileRate");
                Integer profileRateMinutes = profileRate / 60; 
                row.profileRate = profileRateMinutes.toString();
                
                row.configName = rs.getString("ConfigName");
                row.configDemandRate = rs.getString("ConfigDemandRate");
                row.configProfileRate = rs.getString("ConfigProfileRate");
                
                data.add(row);
            }
        });
            
        log.info("Report Records Collected from Database: " + data.size());
    }
}
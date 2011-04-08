package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;

public class DisconnectCollarModel extends MeterReportModelBase<DisconnectCollarModel.ModelRow> {
    
    // dependencies
	private YukonJdbcTemplate yukonJdbcTemplate;
    
    // member variables
    private static String title = "Disconnect Collar Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    static public class ModelRow {
        public String deviceName;
        public String enabled;
        public String deviceType;
        public String meterNumber;
        public String physicalAddress;
        public String disconnectAddress;
    }
    
    public void doLoadData() {
        
        SqlFragmentSource sql = buildSQLStatement();
        List<ModelRow> rows = yukonJdbcTemplate.query(sql, new ParameterizedRowMapper<ModelRow>() {
            @Override
            public ModelRow mapRow(ResultSet rs, int rowNum) throws SQLException {
                DisconnectCollarModel.ModelRow row = new DisconnectCollarModel.ModelRow();

                String deviceName = rs.getString("deviceName");
                row.deviceName = deviceName;
                String disableFlag = rs.getString("disableFlag");
                row.enabled = (CtiUtilities.isTrue(disableFlag.charAt(0)) ? "No" : "Yes"); 
                row.deviceType = rs.getString("deviceType");
                row.meterNumber = rs.getString("meterNumber");
                row.disconnectAddress = rs.getString("disconnectAddress");
                row.physicalAddress = rs.getString("physicalAddress");
                return row;
            }
        });
        data.addAll(rows);
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public SqlFragmentSource buildSQLStatement()
    {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT PAO.PAONAME as deviceName, PAO.TYPE as deviceType, DMG.METERNUMBER as meterNumber, "); 
        sql.append("DMCT400.DISCONNECTADDRESS as disconnectAddress, DCS.ADDRESS as physicalAddress, DISABLEFLAG as disableFlag ");
        sql.append("FROM YUKONPAOBJECT PAO, DEVICEMCT400SERIES DMCT400, DEVICEMETERGROUP DMG, DEVICECARRIERSETTINGS DCS ");
        sql.append("WHERE PAO.PAOBJECTID = DMCT400.DEVICEID ");
        sql.append(" AND PAO.PAOBJECTID = DMG.DEVICEID ");
        sql.append(" AND PAO.PAOBJECTID = DCS.DEVICEID ");
        sql.append(" AND").appendFragment(getFilterSqlWhereClause("PAO.PaobjectId"));
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
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
}

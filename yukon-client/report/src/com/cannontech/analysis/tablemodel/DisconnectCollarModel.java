package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.JdbcTemplateHelper;

public class DisconnectCollarModel extends BareDatedReportModelBase<DisconnectCollarModel.ModelRow> {
    
    private static String title = "Disconnect Collar Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    
    static public class ModelRow {
        public String deviceName;
        public String deviceType;
        public String meterNumber;
        public String disconnectAddress;
    }
    
    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        jdbcOps.query(sql.toString(), new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                DisconnectCollarModel.ModelRow row = new DisconnectCollarModel.ModelRow();

                String deviceName = rs.getString("deviceName");
                row.deviceName = deviceName;
                row.deviceType = rs.getString("deviceType");
                row.meterNumber = rs.getString("meterNumber");
                row.disconnectAddress = rs.getString("disconnectAddress");
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("SELECT PAO.PAONAME as deviceName, PAO.TYPE as deviceType, DMG.METERNUMBER as meterNumber, "); 
        sql.append("DMCT400.DISCONNECTADDRESS as disconnectAddress ");
        sql.append("FROM YUKONPAOBJECT PAO, DEVICEMCT400SERIES DMCT400, DEVICEMETERGROUP DMG ");
        sql.append("WHERE PAO.PAOBJECTID = DMCT400.DEVICEID AND PAO.PAOBJECTID = DMG.DEVICEID ");
 
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

}

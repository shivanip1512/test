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


public class CapControlMaintenancePendingModel extends BareReportModelBase<CapControlMaintenancePendingModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> areaIds;
    
    public CapControlMaintenancePendingModel() {
    }
    
    static public class ModelRow {
        public String cbcName;
        public String capBankName;
        public String address;
        public String drivingDirections;
        public Integer maintenanceAreaId;
        public Integer poleNumber;
        public String latitude;
        public String longitude;
        public String otherComments;
        public String opteamComments;
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
        return "Maintenance Pending Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        jdbcOps.query(sql.toString(), new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                CapControlMaintenancePendingModel.ModelRow row = new CapControlMaintenancePendingModel.ModelRow();

                row.cbcName = rs.getString("cbcName");
                row.capBankName = rs.getString("capBankName");
                row.address = rs.getString("address");
                row.drivingDirections = rs.getString("drivingDirections");
                row.maintenanceAreaId = rs.getInt("maintenanceAreaId");
                row.poleNumber = rs.getInt("poleNumber");
                row.latitude = rs.getString("latitude");
                row.longitude = rs.getString("longitude");
                row.otherComments = rs.getString("otherComments");
                row.opteamComments = rs.getString("opteamComments");
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select yp.paoname as cbcname, yp1.paoname as capbankname, yp1.description as address, "); 
        sql.append("cba.drivedirections, cba.maintenanceareaid, cba.polenumber, cba.latitude, cba.longitude, "); 
        sql.append("cba.othercomments, cba.opteamcomments from ccfeederbanklist fb, ccfeedersubassignment fs, "); 
        sql.append("ccsubstationsubbuslist ss, ccsubareaassignment sa, yukonpaobject yp, yukonpaobject yp1, capbankadditional cba, capbank cb "); 
        sql.append("where yp.paobjectid = cb.controldeviceid and yp1.paobjectid = cb.deviceid and cba.deviceid = cb.deviceid ");
        sql.append("and cba.maintenancereqpend = 'Y' and cb.deviceid = fb.deviceid and fb.feederid = fs.feederid and ");
        sql.append("fs.substationbusid = ss.substationbusid and ss.substationid = sa.substationbusid ");
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "cb.deviceid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }else if(feederIds != null && !feederIds.isEmpty()) {
            result = "fb.feederid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "fs.substationbusid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "sa.areaid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" and ");
            sql.append(result);
        }
        
        sql.append(";");
        return sql;
    }

    public void setCapBankIdsFilter(Set<Integer> capBankIds) {
        this.capBankIds = capBankIds;
    }

    public void setFeederIdsFilter(Set<Integer> feederIds) {
        this.feederIds = feederIds;
    }
    
    public void setSubbusIdsFilter(Set<Integer> subbusIds) {
        this.subbusIds = subbusIds;
    }
    
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }
    
}
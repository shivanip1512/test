package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;

public class CBCSpecialAreaAddressingModel extends BareReportModelBase<CBCSpecialAreaAddressingModel.ModelRow> implements CapControlFilterable  {

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private String orderBy = "capbank";
    
    static public class ModelRow {
        public String specialArea;
        public String area;
        public String substation;
        public String substationBus;
        public String feeder;
        public String capBank;
        public String cbc;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        jdbcOps.query(sql.toString(), new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                CBCSpecialAreaAddressingModel.ModelRow row = new CBCSpecialAreaAddressingModel.ModelRow();

                row.specialArea = rs.getString("specialArea");
                row.area = rs.getString("area");
                row.substation = rs.getString("substation");
                row.substationBus = rs.getString("substationBus");
                row.feeder = rs.getString("feeder");
                row.capBank = rs.getString("capBank");
                row.cbc = rs.getString("cbc");
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement() {
        StringBuffer sql = new StringBuffer ("select yp.paoname as specialArea ");
        sql.append(", ypA.paoname as area ");
        sql.append(", ypS.paoname as substation ");
        sql.append(", ypSB.paoname as substationBus ");
        sql.append(", ypF.paoname as feeder ");
        sql.append(", ypC.paoname as capBank ");
        sql.append(", ypCC.paoname as cbc ");
        sql.append("from ");
        sql.append("yukonpaobject yp, yukonpaobject ypA, yukonpaobject ypS, yukonpaobject ypSB, ");
        sql.append("yukonpaobject ypF, yukonpaobject ypC, ");
        sql.append("yukonpaobject ypCC, ");
        sql.append("ccsubspecialareaassignment sa, ccsubareaassignment a, ccsubstationsubbuslist ss, ");
        sql.append("ccfeedersubassignment fs, ccfeederbanklist fb, capbank c ");
        sql.append("where yp.paobjectid = sa.areaid ");
        sql.append("and ypA.paobjectid = a.areaid and a.substationbusid = ss.substationid ");
        sql.append("and ypS.paobjectid = sa.substationbusid and sa.substationbusid = ss.substationid ");
        sql.append("and ypSB.paobjectid = ss.substationbusid and fs.substationbusid = ss.substationbusid ");
        sql.append("and ypF.paobjectid = fs.feederid and fs.feederid = fb.feederid ");
        sql.append("and ypC.paobjectid = C.deviceid and fb.deviceid = c.deviceid ");
        sql.append("and ypCC.paobjectid = c.controldeviceid ");
        sql.append("and c.controldeviceid > 0 ");
        
        String result = null;
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "ypC.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }else if(feederIds != null && !feederIds.isEmpty()) {
            result = "ypF.feederid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "ypSB.subbusid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "ypS.substationid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "ypA.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" and ");
            sql.append(result);
        }
        
        if(orderBy.equalsIgnoreCase("Special Area")) {
            sql.append("order by specialArea ");
        }else if (orderBy .equalsIgnoreCase("Area")) {
            sql.append("order by area ");
        }else if (orderBy .equalsIgnoreCase("Substation")) {
            sql.append("order by substation ");
        }else if (orderBy .equalsIgnoreCase("Substation Bus")) {
            sql.append("order by substationBus ");
        }else if (orderBy .equalsIgnoreCase("Feeder")) {
            sql.append("order by feeder ");
        }else if (orderBy .equalsIgnoreCase("Cap Bank")) {
            sql.append("order by capBank ");
        }else if (orderBy .equalsIgnoreCase("CBC")) {
            sql.append("order by cbc ");
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
    
    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
    
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getTitle() {
        return "CBC Special Area Addressing Report";
    }
    
    public void setOrderBy(String order_) {
        this.orderBy = order_;
    }
    
}

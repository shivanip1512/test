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


public class CapBankMaxOpsExceededModel extends BareDatedReportModelBase<CapBankMaxOpsExceededModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private String orderBy = "area";
    
    public CapBankMaxOpsExceededModel() {
    }
    
    static public class ModelRow {
    	public String areaName;
    	public String substationName;
    	public String subBusName;
    	public String feederName;
        public String capBankName;
        public String dateTime;
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
        return "Cap Banks that Exceeded Max Daily Operations";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        Timestamp[] dateRange = {new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime())};
        
        jdbcOps.query(sql.toString(), dateRange, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                CapBankMaxOpsExceededModel.ModelRow row = new CapBankMaxOpsExceededModel.ModelRow();
                row.areaName = rs.getString("Area");
                row.substationName = rs.getString("Substation");
                row.subBusName = rs.getString("subBus");
                row.feederName = rs.getString("feederName");
                row.capBankName = rs.getString("capBankName");
                row.dateTime = rs.getString("datetime");
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement() {
        StringBuffer sql = new StringBuffer ("select ");
        sql.append("yp4.paoname Area ");
        sql.append(", yp3.paoname Substation ");
        sql.append(", yp2.paoname subBus ");
        sql.append(", yp1.paoname feederName ");
        sql.append(", yp.paoname capBankName ");
        sql.append(", sl.datetime dateTime");
        sql.append(", sl.description ");
        sql.append("from systemlog sl, ");
        sql.append("yukonpaobject yp, ");
        sql.append("yukonpaobject yp1, ");
        sql.append("yukonpaobject yp2, ");
        sql.append("yukonpaobject yp3, ");
        sql.append("yukonpaobject yp4, ");
        sql.append("capbank c, ");
        sql.append("point p, ");
        sql.append("ccfeederbanklist fb, ");
        sql.append("ccfeedersubassignment fs, ");
        sql.append("ccsubstationsubbuslist ss, ");
        sql.append("ccsubareaassignment sa ");
        sql.append("where ");
        sql.append("yp.paobjectid = c.deviceid ");
        sql.append("and sa.substationbusid = ss.substationid ");
        sql.append("and ss.substationbusid = fs.substationbusid ");
        sql.append("and sa.substationbusid = ss.substationid ");
        sql.append("and yp2.paobjectid = ss.substationbusid ");
        sql.append("and yp3.paobjectid = ss.substationid ");
        sql.append("and yp4.paobjectid = sa.areaid ");
        sql.append("and fs.feederid = fb.feederid ");
        sql.append("and yp1.paobjectid = fb.feederid ");
        sql.append("and c.deviceid = fb.deviceid ");
        sql.append("and sl.pointid = p.pointid ");
        sql.append("and p.paobjectid = c.deviceid ");
        sql.append("and p.pointoffset = 1 and p.pointtype = 'Analog' ");
        sql.append("and sl.description like '%CapBank Exceeded%' ");
        sql.append("and (sl.datetime >= ? and sl.datetime < ?)");
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "yp.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }else if(feederIds != null && !feederIds.isEmpty()) {
            result = "yp1.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "yp2.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "yp3.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "yp4.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" and ");
            sql.append(result);
        }
        
        if(orderBy.equalsIgnoreCase("Area")) {
            sql.append("order by Area ");
        }else if (orderBy .equalsIgnoreCase("Substation")) {
            sql.append("order by substation ");
        }else if (orderBy .equalsIgnoreCase("Substation Bus")) {
            sql.append("order by subBus ");
        }else if (orderBy .equalsIgnoreCase("Feeder")) {
            sql.append("order by feederName ");
        }else if (orderBy .equalsIgnoreCase("Cap Bank")) {
            sql.append("order by capBankName ");
        }else if (orderBy .equalsIgnoreCase("Date/Time")) {
            sql.append("order by dateTime ");
        }
        
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
    
    public void setOrderBy(String order_) {
        this.orderBy = order_;
    }
    
}

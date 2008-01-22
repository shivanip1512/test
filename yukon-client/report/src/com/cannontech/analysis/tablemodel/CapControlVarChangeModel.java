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


public class CapControlVarChangeModel extends BareDatedReportModelBase<CapControlVarChangeModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private Integer queryPercent = 0;
    
    public CapControlVarChangeModel() {
    }
    
    static public class ModelRow {
        public String capBankName;
        public String cbcName;
        public Integer bankSize;
        public Integer kvarChange;
        public Double percentChange;
        public String dateTime;
        public String area;
        public String substation;
        public String subbus;
        public String feeder;
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
        return "CapControl Var Change Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        jdbcOps.query(sql.toString(), new Object[] {new Timestamp(getStartDate().getTime()), new Timestamp(getStopDate().getTime()), queryPercent}, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                CapControlVarChangeModel.ModelRow row = new CapControlVarChangeModel.ModelRow();

                row.capBankName = rs.getString("capBankName");
                row.cbcName = rs.getString("cbcName");
                row.bankSize = rs.getInt("bankSize");
                row.kvarChange = rs.getInt("kvarChange");
                row.percentChange = rs.getDouble("percentChange");
                row.dateTime = rs.getString("dateTime");
                row.area = rs.getString("area");
                row.substation = rs.getString("substation");
                row.subbus = rs.getString("subbus");
                row.feeder = rs.getString("feeder");
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select yp.paoname capbankname, ");
        sql.append("yp1.paoname cbcName, ");
        sql.append("c.banksize, ");
        sql.append("el.kvarChange, ");
        sql.append("((el.kvarChange / c.banksize) * 100) percentChange, ");
        sql.append("el.datetime, ");
        sql.append("yp2.paoname Area, ");
        sql.append("yp3.paoname Substation, ");
        sql.append("yp4.paoname SubBus, ");
        sql.append("yp5.paoname Feeder ");
        sql.append("from (select * from cceventlog where datetime >= ? and datetime < ? ) el, ");
        sql.append("capbank c, yukonpaobject yp, yukonpaobject yp1, ");
        sql.append("yukonpaobject yp2, yukonpaobject yp3, ");
        sql.append("yukonpaobject yp4, yukonpaobject yp5, ");
        sql.append("ccsubareaassignment sa, ccsubstationsubbuslist ss, ccfeedersubassignment fs, ");
        sql.append("ccfeederbanklist fb ");
        sql.append("where yp.paobjectid = c.deviceid ");
        sql.append("and yp1.paobjectid = c.controldeviceid ");
        sql.append("and c.deviceid = fb.deviceid ");
        sql.append("and fb.feederid = fs.feederid ");
        sql.append("and yp5.paobjectid = fb.feederid ");
        sql.append("and fs.substationbusid = ss.substationbusid ");
        sql.append("and yp4.paobjectid = fs.substationbusid ");
        sql.append("and ss.substationid = sa.substationbusid ");
        sql.append("and yp3.paobjectid = ss.substationid ");
        sql.append("and sa.areaid = yp2.paobjectid ");
        sql.append("and el.subid = fs.substationbusid ");
        sql.append("and c.banksize > 0 ");
        sql.append("and el.kvarchange != 0 ");
        sql.append("and ((el.kvarChange / c.banksize) * 100) > ? ");
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "yp.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }else if(feederIds != null && !feederIds.isEmpty()) {
            result = "yp5.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "yp4.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "yp3.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "yp2.paobjectid in ( ";
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

    public void setQueryPercent(Integer queryPercent) {
        this.queryPercent = queryPercent;
    }

    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
}


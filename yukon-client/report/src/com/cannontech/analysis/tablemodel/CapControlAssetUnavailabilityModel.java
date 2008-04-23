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


public class CapControlAssetUnavailabilityModel extends BareDatedReportModelBase<CapControlAssetUnavailabilityModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    @SuppressWarnings("unused")
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    
    public CapControlAssetUnavailabilityModel() {
    }
    
    static public class ModelRow {
        public String area;
        public String substation;
        public String subbus;
        public String feeder;
        public Integer subcount;
        public Integer feedcount;
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
        return "Cap Control Asset Unvailability Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        Timestamp[] dateRange = {new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime()),
                new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getStopDate().getTime())};
        jdbcOps.query(sql.toString(), dateRange, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                CapControlAssetUnavailabilityModel.ModelRow row = new CapControlAssetUnavailabilityModel.ModelRow();
                row.area = rs.getString("area");
                row.substation = rs.getString("substation");
                row.subbus = rs.getString("subbus");
                row.feeder = rs.getString("feeder");
                row.subcount = rs.getInt("subcount");
                row.feedcount = rs.getInt("feedcount");
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select yp.paoname area, ");
        sql.append("yp1.paoname substation, ");
        sql.append("yp2.paoname subbus, ");
        sql.append("yp3.paoname feeder, ");
        sql.append("case when els.feederid = 0 ");
        sql.append("then els.subcount ");
        sql.append("else ");
        sql.append("els.subcount ");
        sql.append("end subcount, ");
        sql.append("case when elf.feederid = 0 ");
        sql.append("then 0 ");
        sql.append("else ");
        sql.append("elf.feedcount ");
        sql.append("end feedcount ");
        sql.append("from yukonpaobject yp,yukonpaobject yp1,yukonpaobject yp2,yukonpaobject yp3, ");
        sql.append("ccsubareaassignment sa, ccsubstationsubbuslist ss, ccfeedersubassignment fs, ");
        sql.append("(select count(*) subcount, subid, feederid from cceventlog ");
        sql.append("where (text like '%Cannot Decrease Var%' or text like '%Cannot Increase Volt%') and datetime > ? and datetime < ? group by subid, feederid) els, ");
        sql.append("(select count(*) feedcount, feederid from cceventlog ");
        sql.append("where (text like '%Cannot Decrease Var%' or text like '%Cannot Increase Volt%') and datetime > ? and datetime < ? group by feederid) elf ");
        sql.append("where ");
        sql.append("yp.paobjectid = sa.areaid ");
        sql.append("and yp1.paobjectid = sa.substationbusid ");
        sql.append("and sa.substationbusid = ss.substationid ");
        sql.append("and yp2.paobjectid = ss.substationbusid ");
        sql.append("and ss.substationbusid = fs.substationbusid ");
        sql.append("and yp3.paobjectid = fs.feederid ");
        sql.append("and els.subid = fs.substationbusid ");
        sql.append("and (els.feederid = fs.feederid or els.feederid = 0) ");
        sql.append("and (elf.feederid = fs.feederid or elf.feederid = 0) ");
        
        String result = null;
        
         if(feederIds != null && !feederIds.isEmpty()) {
            result = "yp3.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "yp2.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "yp1.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "yp.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" and ");
            sql.append(result);
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
    
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }

    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
}


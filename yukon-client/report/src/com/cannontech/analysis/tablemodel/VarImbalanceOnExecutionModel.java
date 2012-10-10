package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;


public class VarImbalanceOnExecutionModel extends BareDatedReportModelBase<VarImbalanceOnExecutionModel.ModelRow> implements CapControlFilterable {
    
    // dependencies
    private JdbcOperations jdbcOps;
    
    // inputs
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private Integer imbalance;
    
    public final static String AREA_NAME_STRING = "Area";
    public final static String SUBSTATION_NAME_STRING = "Substation";
    public final static String SUBSTATIONBUS_NAME_STRING = "Substation Bus";
    public final static int AREA_NAME_COLUMN = 0;
    public final static int SUBSTATION_NAME_COLUMN = 1;
    public final static int SUBSTATIONBUS_NAME_COLUMN = 2;
    public final static int FEEDER_NAME_COLUMN = 3;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    public VarImbalanceOnExecutionModel() {
    }
    
    static public class ModelRow {
        public String area;
        public String substation;
        public String substationbus;
        public String feeder;
        public String capbank;
        public String capbankstate;
        public Date opTime;
        public String operation;
        public String avar;
        public String bvar;
        public String cvar;
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
        return "Var Imbalance on Execution Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        Object[] args = {getStartDate(), getStopDate(), imbalance, imbalance,imbalance};
        jdbcOps.query(sql.toString(), args ,new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                VarImbalanceOnExecutionModel.ModelRow row = new VarImbalanceOnExecutionModel.ModelRow();

                row.area = rs.getString("area");
                row.substation = rs.getString("substation");
                row.substationbus = rs.getString("substationbus");
                row.feeder = rs.getString("feeder");
                row.capbank = rs.getString("capbank");
                row.capbankstate = rs.getString("capbankstateinfo");
                row.opTime = rs.getTimestamp("opTime");
                row.operation = rs.getString("operation");
                row.avar = rs.getString("aVarAtExecution");
                row.bvar = rs.getString("bVarAtExecution");
                row.cvar = rs.getString("cVarAtExecution");
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select ypA.paoname as area ");
        sql.append(", ypS.paoname as substation ");
        sql.append(", ypSB.paoname as substationbus ");
        sql.append(", ypF.paoname as feeder ");
        sql.append(", ypC.paoname as capbank ");
        sql.append(", cAction.datetime as opTime ");
        sql.append(", cAction.Text as operation ");
        sql.append(", cOutcome.datetime as confTime ");
        sql.append(", cOutcome.text as confirmation ");
        sql.append(", cOutcome.kvarBefore ");
        sql.append(", cOutcome.kvarAfter ");
        sql.append(", cOutcome.kvarChange ");
        sql.append(", cOutcome.capbankstateinfo ");
        sql.append(", cAction. aVar as aVarAtExecution ");
        sql.append(", cAction.bVar as bVarAtExecution ");
        sql.append(", cAction.cVar as cVarAtExecution ");
        sql.append("from ");
        sql.append("yukonpaobject ypA, ");
        sql.append("yukonpaobject ypS, ");
        sql.append("ccsubstationsubbuslist ss, ");
        sql.append("ccsubareaassignment sa, ");
        sql.append("yukonpaobject ypSB, ");
        sql.append("yukonpaobject ypF, ");
        sql.append("yukonpaobject ypC, ");
        sql.append("capbank c, ");
        sql.append("point p, ");
        sql.append("cceventlog cAction, ");
        sql.append("cceventlog cOutcome ");
        sql.append("where ");
        sql.append("cAction.DateTime > ?"); 
        sql.append("and cAction.DateTime <= ?");
        sql.append("and cAction.pointid = cOutcome.pointid ");
        sql.append("and p.pointid = cAction.pointid ");
        sql.append("and cAction.actionId = cOutcome.actionId ");
        sql.append("and cAction.actionid > 0 ");
        sql.append("and (cAction.text like 'Open Sent,%' or cAction.text like 'Close Sent,%') ");
        sql.append("and (cOutcome.text like 'Var:%') ");
        sql.append("and ypSB.paobjectid = cAction.subID ");
        sql.append("and ypF.paobjectid = cAction.feederID ");
        sql.append("and c.deviceid = ypC.paobjectid ");
        sql.append("and cAction.pointid = p.pointid ");
        sql.append("and p.paobjectid = c.deviceid ");
        sql.append("and cAction.subID = ss.substationbusid ");
        sql.append("and ypS.paobjectid = ss.substationid ");
        sql.append("and sa.substationbusid = ss.substationid ");
        sql.append("and ypA.paobjectid = sa.areaid ");
        sql.append("and (abs(cAction.aVar - cAction.bVar) >= ? ");
        sql.append("or abs(cAction.aVar - cAction.cVar) >= ? ");
        sql.append("or abs(cAction.bVar - cAction.cVar) >= ?) ");
        
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "ypC.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }else if(feederIds != null && !feederIds.isEmpty()) {
            result = "ypF.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "ypSB.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }else if(substationIds != null && !substationIds.isEmpty()) {
            result = "ypS.paobjectid in ( ";
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
        
        sql.append("order by cAction.datetime desc ");
        
        return sql;
    }

    @Override
    public void setCapBankIdsFilter(Set<Integer> capBankIds) {
        this.capBankIds = capBankIds;
    }

    @Override
    public void setFeederIdsFilter(Set<Integer> feederIds) {
        this.feederIds = feederIds;
    }
    
    @Override
    public void setSubbusIdsFilter(Set<Integer> subbusIds) {
        this.subbusIds = subbusIds;
    }
    
    @Override
    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
    
    @Override
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }
    
    @Required
    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }
    
    public void setImbalance(Integer imbalance) {
        this.imbalance = imbalance;
    }
    
    @Override
	public void setStrategyIdsFilter(Set<Integer> strategyIds) {
		//Not used
	}
    
}

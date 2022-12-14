package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.Lists;

public class CapControlStateComparisonModel extends BareReportModelBase<CapControlStateComparisonModel.ModelRow> implements CapControlFilterable  {

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps;
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private boolean useMisMatch = false;
    
    private final static List<PaoType> paoTypes = Lists.newArrayList(PaoType.CBC_7020, PaoType.CBC_7022, PaoType.CBC_7023, PaoType.CBC_7024,
                                            PaoType.CBC_8020, PaoType.CBC_8024, PaoType.CBC_DNP, PaoType.CBC_LOGICAL);
    
    public CapControlStateComparisonModel(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }
    
    static public class ModelRow {
        public String region;
        public String subName;
        public String feederName;
        public String capBankName;
        public String cbcName;
        public String capBankStatus;
        public String capBankState;
        public String cbcStatus;
        public Date cbcChangeTime;
        public Date capBankChangeTime;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }
    
    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }
    
    @Override
    public String getTitle() {
        return "Cap Control State Comparison Report";
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public void doLoadData() {
        SqlStatementBuilder sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        jdbcOps.query(sql.getSql(), sql.getArguments(), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                CapControlStateComparisonModel.ModelRow row = new CapControlStateComparisonModel.ModelRow();
                    row.region = rs.getString("region");
                    row.subName = rs.getString("subName");
                    row.feederName = rs.getString("feederName");
                    row.capBankName = rs.getString("capBankName");
                    row.cbcName = rs.getString("cbcName");
                    row.capBankStatus = rs.getString("capBankStatus");
                    
                    String capBankState = rs.getString("capBankState");
                    if ( StringUtils.isBlank(capBankState) ) {
                        capBankState = "---";
                    }
                    row.capBankState = capBankState;
                    
                    row.cbcStatus = rs.getString("cbcStatus");
                    row.capBankChangeTime = rs.getTimestamp("capBankChangeTime");
                    row.cbcChangeTime = rs.getTimestamp("cbcChangeTime");
                    
                    data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public SqlStatementBuilder buildSQLStatement() {

        SqlStatementBuilder sql = new SqlStatementBuilder ("");

        sql.append("select");
        sql.append(    "ca.paoname region, yp3.paoName subName, yp2.paoName feederName, yp1.paoName capBankName,");
        sql.append(    "yp.paoName cbcName, s.text capBankStatus, elf.capbankstateinfo capBankState, s1.text cbcStatus,");
        sql.append(    "dcb.laststatuschangetime capBankChangeTime, dcb.twowaycbcstatetime cbcChangeTime ");
        sql.append("from");
        sql.append(    "(select * from yukonpaobject where type").in_k(paoTypes).append(") yp ");
        sql.append(    "left join capbank cb");
        sql.append(        "on cb.controldeviceid = yp.paobjectid and cb.controldeviceid > 0 ");
        sql.append(    "join POINT p");
        sql.append(        "on p.PAObjectID = cb.deviceid and p.POINTOFFSET = 1 and p.POINTTYPE").eq_k(PointType.Status);
        sql.append(    "left join");
        sql.append(        "(select");
        sql.append(            "pointid, capbankstateinfo");
        sql.append(        "from");
        sql.append(            "CCEventLog el,");
        sql.append(            "(select MAX(logid) as el2Logid, pointid as el2PointId from CCEventLog where text like 'Var:%' group by pointid) el2 ");
        sql.append(        "where");
        sql.append(            "el.logID = el2.el2Logid ) elf ");
        sql.append(        "on ELF.pointid = p.pointid ");
        sql.append(    "join");
        sql.append(        "(select");
        sql.append(            "*");
        sql.append(        "from");
        sql.append(            "yukonpaobject");
        sql.append(        "where");
        sql.append(            "type").eq_k(CapControlType.CAPBANK).append(") yp1");
        sql.append(        "on yp1.paobjectid = cb.deviceid ");
        sql.append(    "left join");
        sql.append(        "ccfeederbanklist fb");
        sql.append(            "on fb.deviceid = cb.deviceid ");
        sql.append(    "left join");
        sql.append(        "(select * from yukonpaobject where type").eq_k(CapControlType.FEEDER).append(") yp2");
        sql.append(            "on yp2.paobjectid = fb.feederid ");
        sql.append(    "left join");
        sql.append(        "ccfeedersubassignment sf");
        sql.append(            "on fb.feederid = sf.feederid ");
        sql.append(    "left join");
        sql.append(        "(select * from yukonpaobject where type").eq_k(CapControlType.SUBBUS).append(") yp3");
        sql.append(            "on yp3.paobjectid = sf.substationbusid ");
        sql.append(    "left join");
        sql.append(        "ccsubstationsubbuslist ss");
        sql.append(            "on sf.substationbusid = ss.substationbusid ");
        sql.append(    "left join");
        sql.append(        "(select * from yukonpaobject where type").eq_k(CapControlType.SUBSTATION).append(") yp4");
        sql.append(            "on yp4.paobjectid = ss.substationid "); 
        sql.append(    "join");
        sql.append(        "dynamiccccapbank dcb");
        sql.append(            "on dcb.capbankid = cb.deviceid ");
        sql.append(    "join");
        sql.append(        "state s");
        sql.append(            "on s.stategroupid = 3 and dcb.controlstatus = s.rawstate ");
        if(!useMisMatch) {
            sql.append("left join");
            sql.append(    "state s1");
            sql.append(        "on s1.stategroupid = 3 and dcb.twowaycbcstate = s1.rawstate ");
        } else {
            sql.append("join");
            sql.append(    "state s1");
            sql.append(        "on s1.stategroupid = 3 and dcb.twowaycbcstate = s1.rawstate and s1.rawstate != s.rawstate ");
        }            
        sql.append(    "left join");
        sql.append(        "ccsubstationsubbuslist ssb");
        sql.append(            "on ssb.substationbusid = sf.substationbusid ");
        sql.append(    "left join");
        sql.append(        "ccsubareaassignment saa");
        sql.append(            "on saa.substationbusid = ssb.substationid ");
        sql.append(    "left join");
        sql.append(        "(select paobjectid, paoname");
        sql.append(         "from yukonpaobject");
        sql.append(         "where type").eq_k(CapControlType.AREA).append(") ca");
        sql.append(            "on ca.paobjectid = saa.areaid ");
        
        SqlStatementBuilder result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = new SqlStatementBuilder("yp1.paobjectid").in(capBankIds);
        }
        if(feederIds != null && !feederIds.isEmpty()) {
            result = new SqlStatementBuilder("yp2.paobjectid").in(feederIds);
        }
        if(subbusIds != null && !subbusIds.isEmpty()) {
            result = new SqlStatementBuilder("yp3.paobjectid").in(subbusIds);
        }
        if(substationIds != null && !substationIds.isEmpty()) {
            result = new SqlStatementBuilder("yp4.paobjectid").in(substationIds);
        }
        if(areaIds != null && !areaIds.isEmpty()) {
            result = new SqlStatementBuilder("ca.paobjectid").in(areaIds);
        }
        
        if (result != null) {
            sql.append("where");
            sql.append(result);
        }
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
    
    public void setShowMisMatch(boolean b) {
        this.useMisMatch = b;
    }
    
	@Override
	public void setStrategyIdsFilter(Set<Integer> strategyIds) {
		//Not used
	}
}

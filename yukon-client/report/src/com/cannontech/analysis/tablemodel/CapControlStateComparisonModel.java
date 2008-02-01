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

public class CapControlStateComparisonModel extends BareReportModelBase<CapControlStateComparisonModel.ModelRow> implements CapControlFilterable  {

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private boolean useMisMatch = false;
    
    public CapControlStateComparisonModel() {
    }
    
    static public class ModelRow {
        public String region;
        public String subName;
        public String feederName;
        public String capBankName;
        public String cbcName;
        public String capBankStatus;
        public String cbcStatus;
        public Timestamp cbcChangeTime;
        public Timestamp capBankChangeTime;
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
        return "Cap Control State Comparison Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 
        
        jdbcOps.query(sql.toString(), new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                CapControlStateComparisonModel.ModelRow row = new CapControlStateComparisonModel.ModelRow();
                    row.region = rs.getString("region");
                    row.subName = rs.getString("subName");
                    row.feederName = rs.getString("feederName");
                    row.capBankName = rs.getString("capBankName");
                    row.cbcName = rs.getString("cbcName");
                    row.capBankStatus = rs.getString("capBankStatus");
                    row.cbcStatus = rs.getString("cbcStatus");
                    row.cbcChangeTime = rs.getTimestamp("capBankChangeTime");
                    row.capBankChangeTime = rs.getTimestamp("cbcChangeTime");
                    data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement() {
        StringBuffer sql = new StringBuffer ("");
        if(!useMisMatch) {
            sql.append("select ca.paoname region, yp3.paoName subName, yp2.paoName feederName, yp1.paoName capBankName, ");
            sql.append("yp.paoName cbcName, s.text capBankStatus, s1.text cbcStatus,  ");
            sql.append("dcb.laststatuschangetime capBankChangeTime, dcb.twowaycbcstatetime cbcChangeTime ");
            sql.append("from  (select * from yukonpaobject where type like 'CBC 702%') yp ");
            sql.append("left join capbank cb on cb.controldeviceid = yp.paobjectid and cb.controldeviceid > 0 ");
            sql.append("join (select * from yukonpaobject where type like 'CAP BANK') yp1 on yp1.paobjectid = cb.deviceid ");
            sql.append("left outer join ccfeederbanklist fb on fb.deviceid = cb.deviceid ");
            sql.append("left outer join (select * from yukonpaobject where type like 'CCFEEDER') yp2 on yp2.paobjectid = fb.feederid ");
            sql.append("left outer join ccfeedersubassignment sf on fb.feederid = sf.feederid ");
            sql.append("left outer join (select * from yukonpaobject where type like 'CCSUBBUS') yp3 on yp3.paobjectid = sf.substationbusid ");
            sql.append("left outer join ccsubstationsubbuslist ss on sf.substationbusid = ss.substationbusid ");
            sql.append("left outer join (select * from yukonpaobject where type like 'CCSUBSTATION') yp4 on yp4.paobjectid = ss.substationid ");
            sql.append("join dynamiccccapbank dcb on dcb.capbankid = cb.deviceid ");
            sql.append("join state s on s.stategroupid = 3 and dcb.controlstatus = s.rawstate ");
            sql.append("left outer join state s1 on s1.stategroupid = 3 and dcb.twowaycbcstate = s1.rawstate ");
            sql.append("left outer join ccsubstationsubbuslist ssb on ssb.substationbusid = sf.substationbusid ");
            sql.append("left outer join ccsubareaassignment saa on saa.substationbusid = ssb.substationid ");
     	 	sql.append("left outer join (select paobjectid, paoname from yukonpaobject where type ='ccarea' ) ca on ca.paobjectid = saa.areaid ");
        } else {
            sql.append("select ca.paoname region, yp3.paoName subName, yp2.paoName feederName, yp1.paoName capBankName, ");
            sql.append("yp.paoName cbcName, s.text capBankStatus, s1.text cbcStatus, ");
            sql.append("dcb.laststatuschangetime capBankChangeTime, dcb.twowaycbcstatetime cbcChangeTime ");
            sql.append("from (select * from yukonpaobject where type like 'CBC 702%') yp ");
            sql.append("left join capbank cb on cb.controldeviceid = yp.paobjectid and cb.controldeviceid > 0 ");
            sql.append("join (select * from yukonpaobject where type like 'CAP BANK') yp1 on yp1.paobjectid = cb.deviceid ");
            sql.append("left outer join ccfeederbanklist fb on fb.deviceid = cb.deviceid ");
            sql.append("left outer join (select * from yukonpaobject where type like 'CCFEEDER') yp2 on yp2.paobjectid = fb.feederid ");
            sql.append("left outer join ccfeedersubassignment sf on fb.feederid = sf.feederid ");
            sql.append("left outer join (select * from yukonpaobject where type like 'CCSUBBUS') yp3 on yp3.paobjectid = sf.substationbusid ");
            sql.append("left outer join ccsubstationsubbuslist ss on sf.substationbusid = ss.substationbusid ");
            sql.append("left outer join (select * from yukonpaobject where type like 'CCSUBSTATION') yp4 on yp4.paobjectid = ss.substationid ");
            sql.append("join dynamiccccapbank dcb on dcb.capbankid = cb.deviceid ");
            sql.append("join state s on s.stategroupid = 3 and dcb.controlstatus = s.rawstate ");
            sql.append("join state s1 on s1.stategroupid = 3 and dcb.twowaycbcstate = s1.rawstate and s1.rawstate != s.rawstate ");
            sql.append("left outer join ccsubstationsubbuslist ssb on ssb.substationbusid = sf.substationbusid ");
            sql.append("left outer join ccsubareaassignment saa on saa.substationbusid = ssb.substationid ");
            sql.append("left outer join (select paobjectid, paoname from yukonpaobject where type ='ccarea' ) ca on ca.paobjectid = saa.areaid ");
        }
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "yp1.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }
        if(feederIds != null && !feederIds.isEmpty()) {
            result = "yp2.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }
        if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "yp3.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(subbusIds);
            result += wheres;
            result += " ) ";
        }
        if(substationIds != null && !substationIds.isEmpty()) {
            result = "yp4.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }
        if(areaIds != null && !areaIds.isEmpty()) {
            result = "ca.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(areaIds);
            result += wheres;
            result += " ) ";
        }
        
        if (result != null) {
            sql.append(" where ");
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
    
    public void setSubstationIdsFilter(Set<Integer> substationIds) {
        this.substationIds = substationIds;
    }
    
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }
    
    public void setShowMisMatch(boolean b) {
        this.useMisMatch = b;
    }
    
}

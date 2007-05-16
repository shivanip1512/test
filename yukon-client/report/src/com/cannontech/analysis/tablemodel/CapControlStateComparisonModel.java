package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.analysis.tablemodel.CurtailmentInterruptionSummaryModel.ModelRow;
import com.cannontech.cc.dao.BaseEventDao;
import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.support.CustomerPointTypeHelper;

public class CapControlStateComparisonModel extends BareReportModelBase<CapControlStateComparisonModel.ModelRow> implements CapControlFilterable  {

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> areaIds;
    
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
        return "CapControl State Comparison Report";
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
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select yp3.description as region, yp3.paoName as subName, yp2.paoName as feederName, yp1.paoName as capBankName, ");
        sql.append("yp.paoName as cbcName, s.text as capBankStatus, s1.text as cbcStatus,  ");
        sql.append("dcb.laststatuschangetime as capBankChangeTime, dcb.twowaycbcstatetime as cbcChangeTime ");
        sql.append("from  (select * from yukonpaobject where type like 'CBC 702%') as yp ");
        sql.append("left join capbank as cb on cb.controldeviceid = yp.paobjectid and cb.controldeviceid > 0 ");
        sql.append("join (select * from yukonpaobject where type like 'CAP BANK') as yp1 on yp1.paobjectid = cb.deviceid ");
        sql.append("left outer join ccfeederbanklist fb on fb.deviceid = cb.deviceid ");
        sql.append("left outer join (select * from yukonpaobject where type like 'CCFEEDER') as  yp2 on yp2.paobjectid = fb.feederid ");
        sql.append("left outer join ccfeedersubassignment sf on fb.feederid = sf.feederid ");
        sql.append("left outer join (select * from yukonpaobject where type like 'CCSUBBUS') as yp3 on yp3.paobjectid = sf.substationbusid ");
        sql.append("join dynamiccccapbank dcb on dcb.capbankid = cb.deviceid ");
        sql.append("join state s on s.stategroupid = 3 and dcb.controlstatus = s.rawstate ");
        sql.append("left outer join state s1 on s1.stategroupid = 3 and dcb.twowaycbcstate = s1.rawstate ");
        sql.append("left outer join ccsubareaassignment saa on saa.substationbusid = sf.substationbusid ");
        sql.append("left outer join (select paobjectid from yukonpaobject where type ='ccarea' ) as ca on ca.paobjectid = saa.areaid ");
        
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

    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setCapBankIdsFilter(java.util.Set)
     */
    public void setCapBankIdsFilter(Set<Integer> capBankIds) {
        this.capBankIds = capBankIds;
    }

    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setFeederIdsFilter(java.util.Set)
     */
    public void setFeederIdsFilter(Set<Integer> feederIds) {
        this.feederIds = feederIds;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setSubbusIdsFilter(java.util.Set)
     */
    public void setSubbusIdsFilter(Set<Integer> subbusIds) {
        this.subbusIds = subbusIds;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.analysis.tablemodel.CapControlFilterable#setAreaIdsFilter(java.util.Set)
     */
    public void setAreaIdsFilter(Set<Integer> areaIds) {
        this.areaIds = areaIds;
    }
    
}

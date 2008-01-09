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


public class CapControlOperationsModel extends BareDatedReportModelBase<CapControlOperationsModel.ModelRow> implements CapControlFilterable {
    
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    private String[] statusQualities;
    
    public CapControlOperationsModel() {
    }
    
    static public class ModelRow {
        public String cbcName;
        public String bankName;
        public Timestamp opTime;
        public String operation;
        public Timestamp confTime;
        public String confStatus;
        public String bankStatusQuality;
        public String feederName;
        public Integer feederId;
        public String subName;
        public Integer subBusId;
        public String region;
        public Integer bankSize;
        public String protocol;
        public String ipAddress;
        public String serialNum;
        public String slaveAddress;
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
        return "CapControl Operations Report";
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
                
                CapControlOperationsModel.ModelRow row = new CapControlOperationsModel.ModelRow();

                row.cbcName = rs.getString("cbcName");
                row.bankName = rs.getString("bankName");
                row.opTime = rs.getTimestamp("opTime");
                row.operation = rs.getString("operation");
                row.confTime = rs.getTimestamp("confTime");
                row.confStatus = rs.getString("confStatus");
                row.bankStatusQuality = rs.getString("bankStatusQuality");
                row.feederName = rs.getString("feederName");
                row.feederId = rs.getInt("feederId");
                row.subName = rs.getString("subName");
                row.subBusId = rs.getInt("subBusId");
                row.region = rs.getString("region");
                row.bankSize = rs.getInt("bankSize");
                row.protocol = rs.getString("protocol");
                row.ipAddress = rs.getString("ipAddress");
                row.serialNum = rs.getString("serialNum");
                row.slaveAddress = rs.getString("slaveAddress");
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select yp3.paoName as cbcName, yp.paoName as bankName, el.datetime as opTime, el.text as operation, ");
        sql.append("el2.datetime as confTime, el2.text as confStatus, el2.capbankstateInfo as bankStatusQuality, yp1.paoName as feederName, yp1.paobjectId as feederId,  yp2.paoName as subName, ");
        sql.append("yp2.paobjectid as subBusId, ca.paoname as region, cb.bankSize as bankSize, cb.controllertype as protocol, p.value as ipAddress, ");
        sql.append("cbc.serialnumber as serialNum, da.slaveAddress as slaveAddress "); 
        sql.append("from ( select op.logid as oid,  min(aaa.confid) as cid  from (select logid, pointid from cceventlog where datetime > ? and datetime < ? and (text like '%Close sent,%' or text like '%Open sent,%' )) op ");
        sql.append("left join (select el.logid as opid, min(el2.logid) as confid from cceventlog el ");
        sql.append("join cceventlog el2 on el2.pointid = el.pointid left outer join  (select a.logid as aid, min(b.logid) as next_aid "); 
        sql.append("from cceventlog a, cceventlog b where a.pointid = b.pointid ");
        sql.append("and (a.text like '%Close sent,%' or a.text like '%Open sent,%') and (b.text like '%Close sent,%' or b.text like '%Open sent,%')  ");
        sql.append("and b.logid > a.logid group by a.logid) el3 on el3.aid = el.logid ");
        sql.append("where (el.text like '%Close sent,%'  or el.text like '%Open sent,%') and el2.text like 'Var: %' ");
        sql.append("and el2.logid > el.logid and (el2.logid < el3.next_aid  or el3.next_aid is null) ");
        sql.append("group by el.logid ) aaa on op.logid = aaa.opid group by op.logid ) OpConf ");
        sql.append("join cceventlog el on el.logid = opConf.oid left join cceventlog el2 on el2.logid = opConf.cid ");
        sql.append("join point on point.pointid = el.pointid ");
        sql.append("join dynamiccccapbank on dynamiccccapbank.capbankid = point.paobjectid ");
        sql.append("join yukonpaobject yp on yp.paobjectid = dynamiccccapbank.capbankid ");
        sql.append("join yukonpaobject yp1 on yp1.paobjectid = el.feederid ");
        sql.append("join yukonpaobject yp2 on yp2.paobjectid = el.subid ");
        sql.append("join ccsubstationsubbuslist ss on ss.substationbusid = el.subid ");
        sql.append("join yukonpaobject yp4 on yp4.paobjectid = ss.substationid ");
        sql.append("join capbank cb on cb.deviceid = dynamiccccapbank.capbankid ");
        sql.append("left join devicedirectcommsettings ddcs on ddcs.deviceid = cb.controldeviceid ");
        sql.append("left join deviceaddress da on da.deviceid = cb.controldeviceid ");
        sql.append("join yukonpaobject yp3 on yp3.paobjectid = cb.controldeviceid ");
        sql.append("left join devicecbc cbc on cbc.deviceid = cb.controldeviceid ");
        sql.append("left outer join (select * from dynamicpaoinfo where infokey like '%udp ip%') p ");
        sql.append("on p.paobjectid = cb.controldeviceid ");
        sql.append("left outer join ccsubstationsubbuslist sbb on sbb.substationbusid = yp2.paobjectid ");
        sql.append("left outer join ccsubareaassignment saa on saa.substationbusid = sbb.substationid  ");
 	 	sql.append("join yukonpaobject ca on ca.paobjectid = saa.areaid ");
 	 	String qualities = getQualities();
 	 	sql.append("where el2.capbankstateInfo in (" + qualities+ ") ");
 	 	
        
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
            result = "yp4.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(substationIds);
            result += wheres;
            result += " ) ";
        }else if(areaIds != null && !areaIds.isEmpty()) {
            result = "ca.paobjectid in ( ";
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
    
    private String getQualities() {
        String list = "";
        for (int i = 0; i < statusQualities.length; i ++) {
            list+="'";
            list+=statusQualities[i];
            list+="'";
            if(i != statusQualities.length -1) {
                list+=",";
            }
        }
        return list;
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
    
    public void setStatusQualities(String[] statusQualities) {
        this.statusQualities = statusQualities;
    }
    
}

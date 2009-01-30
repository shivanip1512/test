package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private String orderBy = "area";
    
    public CapControlOperationsModel() {
    }
    
    static public class ModelRow {
        public String cbcName;
        public String bankName;
        public String opTime;
        public String operation;
        public String confTime;
        public String confStatus;
        public String bankStatusQuality;
        public String feederName;
        public String subName;
        public Integer bankSize;
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
        return "Cap Control Operations Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        SqlStatementBuilder sql = buildSQLStatement();
        CTILogger.info(sql); 
        
        jdbcOps.query(sql.getSql(), sql.getArguments(), new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                
                CapControlOperationsModel.ModelRow row = new CapControlOperationsModel.ModelRow();

                Date opTime =  rs.getTimestamp("opTime");
                Date confTime = rs.getTimestamp("confTime");

                row.opTime = getColumnTimeFormat().format(opTime);
                row.confTime = getColumnTimeFormat().format(confTime);
                
                row.cbcName = rs.getString("cbcName");
                row.bankName = rs.getString("bankName");
                row.operation = rs.getString("operation");
                row.confStatus = rs.getString("confStatus");
                row.bankStatusQuality = rs.getString("bankStatusQuality");
                row.feederName = rs.getString("feederName");
                row.subName = rs.getString("subName");
                row.bankSize = rs.getInt("bankSize");
                row.ipAddress = rs.getString("ipAddress");
                row.serialNum = rs.getString("serialNum");
                row.slaveAddress = rs.getString("slaveAddress");
                
                data.add(row);
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public SqlStatementBuilder buildSQLStatement()
    {
        SqlStatementBuilder sql = new SqlStatementBuilder("select yp3.paoName cbcName, yp.paoName bankName, el.datetime opTime, el.text operation, ");
        sql.append("el2.datetime confTime, el2.text confStatus, el2.capbankstateInfo bankStatusQuality, yp1.paoName feederName, yp1.paobjectId feederId,  yp2.paoName subName, ");
        sql.append("yp2.paobjectid subBusId, ca.paoname region, cb.bankSize bankSize, cb.controllertype protocol, p.value ipAddress, ");
        sql.append("cbc.serialnumber serialNum, da.slaveAddress slaveAddress "); 
        sql.append("from ( select op.logid oid,  min(aaa.confid) cid  from (select logid, pointid from cceventlog where datetime > ").appendArgument(new java.sql.Timestamp(getStartDate().getTime()));
        sql.append(" and datetime < ").appendArgument(new java.sql.Timestamp(getStopDate().getTime()));
        sql.append(" and (text like '%Close sent,%' or text like '%Open sent,%' )) op ");
        sql.append("left join (select el.logid opid, min(el2.logid) confid from cceventlog el ");
        sql.append("join cceventlog el2 on el2.pointid = el.pointid left outer join  (select a.logid aid, min(b.logid) next_aid "); 
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
 	 	sql.append("where el2.capbankstateInfo in (").appendArgumentList(Arrays.asList(statusQualities));
 	 	sql.append(")");
 	 	
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
        
        if(orderBy.equalsIgnoreCase("CBC")) {
            sql.append("order by cbcName ");
        }else if (orderBy .equalsIgnoreCase("Operation Time")) {
            sql.append("order by opTime ");
        }else if (orderBy .equalsIgnoreCase("Operation")) {
            sql.append("order by operation ");
        }else if (orderBy .equalsIgnoreCase("Conf Time")) {
            sql.append("order by confTime ");
        }else if (orderBy .equalsIgnoreCase("Conf Status")) {
            sql.append("order by confStatus ");
        }else if (orderBy .equalsIgnoreCase("Status Quality")) {
            sql.append("order by bankStatusQuality ");
        }else if (orderBy .equalsIgnoreCase("Feeder")) {
            sql.append("order by feederName ");
        }else if (orderBy .equalsIgnoreCase("Substation Bus")) {
            sql.append("order by subName ");
        }else if (orderBy .equalsIgnoreCase("Area")) {
            sql.append("order by region ");
        }else if (orderBy .equalsIgnoreCase("Bank Size")) {
            sql.append("order by bankSize ");
        }else if (orderBy .equalsIgnoreCase("Ip Address")) {
            sql.append("order by ipAddress ");
        }else if (orderBy .equalsIgnoreCase("Serial Num")) {
            sql.append("order by serialNum ");
        }else if (orderBy .equalsIgnoreCase("Slave Address")) {
            sql.append("order by slaveAddress ");
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
    
    public void setStatusQualities(String[] statusQualities) {
        this.statusQualities = statusQualities;
    }
    
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
    
}

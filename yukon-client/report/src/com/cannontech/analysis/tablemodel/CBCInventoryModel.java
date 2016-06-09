package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;

public class CBCInventoryModel extends BareReportModelBase<CBCInventoryModel.ModelRow> implements CapControlFilterable {

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
    private Set<Integer> substationIds;
    private Set<Integer> areaIds;
    
    public CBCInventoryModel() {
    }
    
    static public class ModelRow {
        public String cbcName;
        public String ipAddress;
        public String slaveAddress;
        public String protocol;
        public String region;
        public String subName;
        public String feederName;
        public String bankName;
        public Integer bankSize;
        public String controlType;
        public String driveDirections;
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
        return "CBC Inventory Report";
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
                CBCInventoryModel.ModelRow row = new CBCInventoryModel.ModelRow();

                row.cbcName = rs.getString("cbcName");
                row.ipAddress = rs.getString("ipAddress");
                row.slaveAddress = rs.getString("slaveAddress");
                row.protocol = rs.getString("protocol");
                row.region = rs.getString("region");
                row.subName = rs.getString("subName");
                row.feederName = rs.getString("feederName");
                row.bankName = rs.getString("capbankName");
                row.bankSize = rs.getInt("bankSize");
                row.controlType = rs.getString("controlType");
                row.driveDirections = rs.getString("driveDirections");
                data.add(row);
                
            }
        });
        
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public SqlStatementBuilder buildSQLStatement()
    {
        final Set<PaoType> cbcTypes = PaoType.getCbcTypes();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select yp.paoname cbcName, p.value ipAddress, da.slaveaddress slaveAddress, cb.controllertype protocol, ");
        sql.append("ca.paoname region, yp3.paoName subName, yp2.paoName feederName, yp1.paoName capbankName, cb.bankSize bankSize, ");
        sql.append("cb.operationalstate controlType,  yp1.description driveDirections ");
        sql.append("from (select paobjectid, paoname from yukonpaobject where type").in(cbcTypes).append(") yp ");
        sql.append("left outer join capbank cb on cb.controldeviceid = yp.paobjectid ");
        sql.append("left outer join yukonpaobject yp1 on cb.deviceid = yp1.paobjectid ");
        sql.append("left outer join ccfeederbanklist fb on fb.deviceid = cb.deviceid ");
        sql.append("left outer join yukonpaobject yp2 on yp2.paobjectid = fb.feederid ");
        sql.append("left outer join ccfeedersubassignment sf on fb.feederid = sf.feederid ");
        sql.append("left outer join yukonpaobject yp3 on yp3.paobjectid = sf.substationbusid ");
        sql.append("left outer join ccsubstationsubbuslist ss on sf.substationbusid  = ss.substationbusid ");
        sql.append("left outer join yukonpaobject yp4 on yp4.paobjectid = ss.substationid ");
        sql.append("left outer join deviceaddress da on da.deviceid = yp.paobjectid");
        sql.append("left outer join (select paobjectid, value from dynamicpaoinfo where infokey like '%udp ip%' UNION");
        sql.append("select paobjectid, propertyvalue as value from paoproperty where propertyname = 'TcpIpAddress') p on p.paobjectid = yp.paobjectid ");
        sql.append("left outer join ccsubstationsubbuslist ssb on ssb.substationbusid = sf.substationbusid ");
 	 	sql.append("left outer join ccsubareaassignment saa on saa.substationbusid = ssb.substationid ");
 	 	sql.append("left outer join (select paobjectid, paoname from yukonpaobject where type").eq_k(PaoType.CAP_CONTROL_AREA).append(") ca on ca.paobjectid = saa.areaid ");
        
        String result = null;
        
        if(capBankIds != null && !capBankIds.isEmpty()) {
            result = "yp1.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(capBankIds);
            result += wheres;
            result += " ) ";
        }else if(feederIds != null && !feederIds.isEmpty()) {
            result = "yp2.paobjectid in ( ";
            String wheres = SqlStatementBuilder.convertToSqlLikeList(feederIds);
            result += wheres;
            result += " ) ";
        }else if(subbusIds != null && !subbusIds.isEmpty()) {
            result = "yp3.paobjectid in ( ";
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
            sql.append(" where ");
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
    
    @Override
	public void setStrategyIdsFilter(Set<Integer> strategyIds) {
		//Not used
	}
}
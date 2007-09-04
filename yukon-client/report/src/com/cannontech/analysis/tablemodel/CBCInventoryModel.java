package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.JdbcTemplateHelper;

public class CBCInventoryModel extends BareReportModelBase<CBCInventoryModel.ModelRow> implements CapControlFilterable {

    private List<ModelRow> data = new ArrayList<ModelRow>();
    private JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();
    private Set<Integer> capBankIds;
    private Set<Integer> feederIds;
    private Set<Integer> subbusIds;
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
    
    public String getTitle() {
        return "CBC Inventory Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {

        StringBuffer sql = buildSQLStatement();
        CTILogger.info(sql.toString()); 

        jdbcOps.query(sql.toString(), new RowCallbackHandler() {
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
    
    public StringBuffer buildSQLStatement()
    {
        StringBuffer sql = new StringBuffer ("select yp.paoname as cbcName, p.value as ipAddress, da.slaveaddress as slaveAddress, cb.controllertype as protocol, ");
        sql.append("ca.paoname as region, yp3.paoName as subName, yp2.paoName as feederName, yp1.paoName as bankName, cb.bankSize as bankSize, ");
        sql.append("cb.operationalstate as controlType,  yp1.description as driveDirections ");
        sql.append("from (select paobjectid, paoname from yukonpaobject where type like '%CBC%') as yp ");
        sql.append("left outer join capbank as cb on cb.controldeviceid = yp.paobjectid ");
        sql.append("left outer join yukonpaobject as yp1 on cb.deviceid = yp1.paobjectid ");
        sql.append("left outer join ccfeederbanklist fb on fb.deviceid = cb.deviceid ");
        sql.append("left outer join yukonpaobject yp2 on yp2.paobjectid = fb.feederid ");
        sql.append("left outer join ccfeedersubassignment sf on fb.feederid = sf.feederid ");
        sql.append("left outer join yukonpaobject yp3 on yp3.paobjectid = sf.substationbusid ");
        sql.append("left outer join deviceaddress da on da.deviceid = cb.controldeviceid ");
        sql.append("left outer join (select * from dynamicpaoinfo where infokey like '%udp ip%') as p on p.paobjectid = yp.paobjectid ");
        sql.append("left outer join ccsubareaassignment saa on saa.substationbusid = sf.substationbusid ");
 	 	sql.append("join (select paobjectid, paoname from yukonpaobject where type ='ccarea' ) as ca on ca.paobjectid = saa.areaid ");
        
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
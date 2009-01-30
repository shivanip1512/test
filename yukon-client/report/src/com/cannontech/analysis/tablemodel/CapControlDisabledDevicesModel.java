package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.clientutils.CTILogger;


public class CapControlDisabledDevicesModel extends BareReportModelBase<CapControlDisabledDevicesModel.ModelRow> implements CapControlFilterable {
    
    // dependencies
    private JdbcOperations jdbcOps;
    
    // inputs
    @SuppressWarnings("unused")
    private Set<Integer> capBankIds;
    @SuppressWarnings("unused")
    private Set<Integer> feederIds;
    @SuppressWarnings("unused")
    private Set<Integer> subbusIds;
    @SuppressWarnings("unused")
    private Set<Integer> substationIds;
    @SuppressWarnings("unused")
    private Set<Integer> areaIds;
    private String[] deviceTypes;
    
    // member variables
    private List<ModelRow> data = new ArrayList<ModelRow>();
    
    public CapControlDisabledDevicesModel() {
    }
    
    static public class ModelRow {
        public String deviceName;
        public String deviceType;
        public String deviceParent;
        public String dateTime;
        public String user;
        public String comment;
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
        return "Cap Control Disabled Devices Report";
    }

    public int getRowCount() {
        return data.size();
    }

    public void doLoadData() {
        
        List<String> deviceTypeList = Arrays.asList(deviceTypes);
        for(String type: deviceTypeList) {
            
            String sql = buildSQLStatement(type);
            
            jdbcOps.query(sql, new RowCallbackHandler() {
                public void processRow(ResultSet rs) throws SQLException {
                    
                    CapControlDisabledDevicesModel.ModelRow row = new CapControlDisabledDevicesModel.ModelRow();
    
                    row.deviceName = rs.getString("deviceName");
                    row.deviceType = rs.getString("deviceType");
                    String parent = "";
                    
                    if(row.deviceType.equalsIgnoreCase("CCAREA")) {
                        parent = "---";
                    }else if(row.deviceType.equalsIgnoreCase("CCSUBSTATION")) {
                        parent = rs.getString("area");
                    }else if(row.deviceType.equalsIgnoreCase("CCSUBBUS")) {
                        parent = rs.getString("area");
                        parent += "->";
                        parent += rs.getString("substation");
                    }else if(row.deviceType.equalsIgnoreCase("CCFEEDER")) {
                        parent = rs.getString("area");
                        parent += "->";
                        parent += rs.getString("substation");
                        parent += "->";
                        parent += rs.getString("subbus");
                    }else if(row.deviceType.equalsIgnoreCase("CAP BANK")) {
                        parent = rs.getString("area");
                        parent += "->";
                        parent += rs.getString("substation");
                        parent += "->";
                        parent += rs.getString("subbus");
                        parent += "->";
                        parent += rs.getString("feeder");
                    }else if(row.deviceType.startsWith("CBC")) {
                        parent = rs.getString("area");
                        parent += "->";
                        parent += rs.getString("substation");
                        parent += "->";
                        parent += rs.getString("subbus");
                        parent += "->";
                        parent += rs.getString("feeder");
                        parent += "->";
                        parent += rs.getString("capbank");
                    }
                    row.deviceParent = parent;
                    
                    Date date = rs.getTimestamp("commenttime"); 
                    
                    String time;
                    
                    if (date == null) {
                        time = "---";
                    } else {
                    	time  = getColumnTimeFormat().format(date);
                    }
                    row.dateTime = time;
                    
                    String user = rs.getString("username");
                    if(user == null || user.length() < 1) {
                        user = "---";
                    }
                    row.user = user;
                    
                    String comment = rs.getString("capcomment");
                    if(comment == null || comment.length() < 1) {
                        comment = "---";
                    }
                    row.comment = comment;
                    
                    data.add(row);
                }
            });
            
        }
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public String buildSQLStatement(String type) {
        
        String areaQuery = "select yp.paoname devicename ";
        areaQuery += ", yp.type deviceType ";
        areaQuery += ", '---' area ";
        areaQuery += ", '---' substation ";
        areaQuery += ", '---' subbus ";
        areaQuery += ", '---' feeder ";
        areaQuery += ", '---' capbank ";
        areaQuery += ", c.capcomment ";
        areaQuery += ", c.commenttime ";
        areaQuery += ", yu.username ";
        areaQuery += "from ";
        areaQuery += "(select * from yukonpaobject where type = 'CCAREA' and disableflag = 'Y') yp ";
        areaQuery += "left outer join capcontrolcomment c on c.paoId = yp.paobjectid and c.action = 'DISABLED' ";
        areaQuery += "left outer join yukonuser yu on yu.userid = c.userid ";
        
        String substationQuery = "select yp.paoname devicename ";
        substationQuery += ", yp.type deviceType ";
        substationQuery += ", pInfo.area area ";
        substationQuery += ", '---' substation ";
        substationQuery += ", '---' subbus ";
        substationQuery += ", '---' feeder ";
        substationQuery += ", '---' capbank ";
        substationQuery += ", c.capcomment ";
        substationQuery += ", c.commenttime ";
        substationQuery += ", yu.username ";
        substationQuery += "from (select * from yukonpaobject where type = 'CCSUBSTATION' and disableflag = 'Y') yp ";
        substationQuery += "join (select ypa.paoname area , yps.paobjectid from yukonpaobject ypa , yukonpaobject yps, ";
        substationQuery += "ccsubareaassignment sa, ccsubstationsubbuslist ss where yps.paobjectid = ss.substationid ";
        substationQuery += "and sa.substationbusid = ss.substationid and sa.areaid = ypa.paobjectid and yps.type like 'CCSUBSTATION') pInfo on yp.paobjectid = pInfo.paobjectid ";
        substationQuery += "left outer join capcontrolcomment c on c.paoId = yp.paobjectid and c.action = 'DISABLED' ";
        substationQuery += "left outer join yukonuser yu on yu.userid = c.userid ";
        substationQuery += "order by yp.type ";
        
        String subBusQuery = "select yp.paoname devicename ";
        subBusQuery += ", yp.type deviceType ";
        subBusQuery += ", pInfo.area area ";
        subBusQuery += ", pInfo.substation substation ";
        subBusQuery += ", '---' subbus ";
        subBusQuery += ", '---' feeder ";
        subBusQuery += ", '---' capbank ";
        subBusQuery += ", c.capcomment ";
        subBusQuery += ", c.commenttime ";
        subBusQuery += ", yu.username ";
        subBusQuery += "from (select * from yukonpaobject where type = 'CCSUBBUS' and disableflag = 'Y') yp ";
        subBusQuery += "join (select ypa.paoname area, yps.paoname substation, ypsb.paobjectid from yukonpaobject ypa, yukonpaobject yps, ";
        subBusQuery += "yukonpaobject ypsb, ccsubareaassignment sa, ccsubstationsubbuslist ss where ypsb.paobjectid = ss.substationbusid ";
        subBusQuery += "and yps.paobjectid = ss.substationid and sa.substationbusid = ss.substationid and sa.areaid = ypa.paobjectid ";
        subBusQuery += "and ypsb.type like 'CCSUBBUS') pInfo on yp.paobjectid = pInfo.paobjectid ";
        subBusQuery += "left outer join capcontrolcomment c on c.paoId = yp.paobjectid and c.action = 'DISABLED' ";
        subBusQuery += "left outer join yukonuser yu on yu.userid = c.userid ";
        subBusQuery += "order by yp.type ";
        
        String feederQuery = "select yp.paoname devicename ";
        feederQuery += ", yp.type deviceType ";
        feederQuery += ", pInfo.area area ";
        feederQuery += ", pInfo.substation substation ";
        feederQuery += ", pInfo.subbus subbus ";
        feederQuery += ", '---' feeder ";
        feederQuery += ", '---' capbank ";
        feederQuery += ", c.capcomment ";
        feederQuery += ", c.commenttime ";
        feederQuery += ", yu.username ";
        feederQuery += "from (select * from yukonpaobject where type = 'CCFEEDER' and disableflag = 'Y') yp ";
        feederQuery += "join (select ypa.paoname area, yps.paoname substation, ypsb.paoname subbus, ypf.paobjectid from yukonpaobject ypa, ";
        feederQuery += "yukonpaobject yps, yukonpaobject ypsb, yukonpaobject ypf, ccsubareaassignment sa, ccsubstationsubbuslist ss, ";
        feederQuery += "ccfeedersubassignment fs where ypf.paobjectid = fs.feederid and ypsb.paobjectid = fs.substationbusid ";
        feederQuery += "and yps.paobjectid = ss.substationid and fs.substationbusid = ss.substationbusid and sa.substationbusid = ss.substationid ";
        feederQuery += "and sa.areaid = ypa.paobjectid and ypf.type like 'CCFEEDER') pInfo on yp.paobjectid = pInfo.paobjectid ";
        feederQuery += "left outer join capcontrolcomment c on c.paoId = yp.paobjectid and c.action = 'DISABLED' ";
        feederQuery += "left outer join yukonuser yu on yu.userid = c.userid ";
        feederQuery += "order by yp.type ";
        
        String capBankQuery = "select yp.paoname devicename ";
        capBankQuery += ", yp.type deviceType ";
        capBankQuery += ", pInfo.area area ";
        capBankQuery += ", pInfo.substation substation ";
        capBankQuery += ", pInfo.subbus subbus ";
        capBankQuery += ", pInfo.feeder feeder ";
        capBankQuery += ", '---' capbank ";
        capBankQuery += ", c.capcomment ";
        capBankQuery += ", c.commenttime ";
        capBankQuery += ", yu.username ";
        capBankQuery += "from (select * from yukonpaobject where type = 'CAP BANK' and disableflag = 'Y') yp ";
        capBankQuery += "join (select ypa.paoname area, yps.paoname substation, ypsb.paoname subbus, ypf.paoname feeder, ypc.paobjectid ";
        capBankQuery += "from yukonpaobject ypa, yukonpaobject yps, yukonpaobject ypsb, yukonpaobject ypf, yukonpaobject ypc, ";
        capBankQuery += "ccsubareaassignment sa, ccsubstationsubbuslist ss, ccfeedersubassignment fs, ccfeederbanklist fb, capbank c ";
        capBankQuery += "where ypc.paobjectid = c.deviceid and fb.deviceid = c.deviceid and fb.feederid = fs.feederid and ypf.paobjectid = fb.feederid ";
        capBankQuery += "and ypsb.paobjectid = fs.substationbusid and yps.paobjectid = ss.substationid and fs.substationbusid = ss.substationbusid ";
        capBankQuery += "and sa.substationbusid = ss.substationid and sa.areaid = ypa.paobjectid and ypc.type like 'CAP BANK') pInfo on pInfo.paobjectid = yp.paobjectid ";
        capBankQuery += "left outer join capcontrolcomment c on c.paoId = yp.paobjectid and c.action = 'DISABLED' ";
        capBankQuery += "left outer join yukonuser yu on yu.userid = c.userid ";
        capBankQuery += "order by yp.type ";
        
        String cbcQuery = "select yp.paoname devicename ";
        cbcQuery += ", yp.type deviceType ";
        cbcQuery += ", pInfo.area area ";
        cbcQuery += ", pInfo.substation substation ";
        cbcQuery += ", pInfo.subbus subbus ";
        cbcQuery += ", pInfo.feeder feeder ";
        cbcQuery += ", pInfo.capbank capbank ";
        cbcQuery += ", c.capcomment ";
        cbcQuery += ", c.commenttime ";
        cbcQuery += ", yu.username ";
        cbcQuery += "from (select * from yukonpaobject where type like 'CBC%' and disableflag = 'Y') yp ";
        cbcQuery += "join (select ypa.paoname area, yps.paoname substation, ";
        cbcQuery += "ypsb.paoname subbus, ypf.paoname feeder, ypc.paoname capbank, yp.paobjectid from yukonpaobject yp , yukonpaobject ypa, ";
        cbcQuery += "yukonpaobject yps, yukonpaobject ypsb, yukonpaobject ypf, yukonpaobject ypc, ccsubareaassignment sa, ccsubstationsubbuslist ss, ";
        cbcQuery += "ccfeedersubassignment fs, ccfeederbanklist fb, capbank c where yp.paobjectid = c.controldeviceid and ypc.paobjectid = c.deviceid ";
        cbcQuery += "and fb.deviceid = c.deviceid and fb.feederid = fs.feederid and ypf.paobjectid = fb.feederid and ypsb.paobjectid = fs.substationbusid ";
        cbcQuery += "and yps.paobjectid = ss.substationid and fs.substationbusid = ss.substationbusid and sa.substationbusid = ss.substationid ";
        cbcQuery += "and sa.areaid = ypa.paobjectid and yp.type like 'CBC%') pInfo on yp.paobjectid = pInfo.paobjectid ";
        cbcQuery += "left outer join capcontrolcomment c on c.paoId = yp.paobjectid and c.action = 'DISABLED' ";
        cbcQuery += "left outer join yukonuser yu on yu.userid = c.userid ";
        cbcQuery += "order by yp.type ";
        
        if(type.equalsIgnoreCase("CBC")) {
            return cbcQuery;
        } else if (type.equalsIgnoreCase("Cap Bank")) {
            return capBankQuery;
        } else if (type.equalsIgnoreCase("Feeder")) {
            return feederQuery;
        }  else if (type.equalsIgnoreCase("Sub Bus")) {
            return subBusQuery;
        } else if (type.equalsIgnoreCase("Substation")) {
            return substationQuery;
        } else if (type.equalsIgnoreCase("Area")) {
            return areaQuery;
        } else {
            return"";
        }
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
    
    @Required
    public void setJdbcOps(JdbcOperations jdbcOps) {
        this.jdbcOps = jdbcOps;
    }
    
    public void setDeviceTypes(String[] deviceTypes) {
        this.deviceTypes = deviceTypes;
    }
    
}

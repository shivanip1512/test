package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
        return "CapControl Disabled Devices Report";
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
                    row.deviceParent = rs.getString("deviceParent");
                    
                    data.add(row);
                }
            });
            
        }
        CTILogger.info("Report Records Collected from Database: " + data.size());
    }
    
    public String buildSQLStatement(String type) {
        
        String areaQuery = "select yp.paoname as devicename, yp.type as deviceType, ' --- ' as deviceParent from yukonpaobject yp where type = 'CCAREA' and disableflag = 'Y'";
        
        String substationQuery ="select yp.paoname as devicename, yp.type as deviceType, pInfo.parent as deviceParent from yukonpaobject yp, ";
        substationQuery += "(select ypa.paoname as PARENT , yps.paobjectid from yukonpaobject ypa , yukonpaobject yps, ";
        substationQuery += "ccsubareaassignment sa, ccsubstationsubbuslist ss where yps.paobjectid = ss.substationid ";
        substationQuery += "and sa.substationbusid = ss.substationid and sa.areaid = ypa.paobjectid and yps.type like 'CCSUBSTATION') as pInfo ";
        substationQuery += "where type = 'CCSUBSTATION' and disableflag = 'Y' and yp.paobjectid = pInfo.paobjectid order by type ";
        
        String subBusQuery = "select yp.paoname as devicename, yp.type as deviceType, pInfo.parent as deviceParent from yukonpaobject yp, ";
        subBusQuery += "(select ypa.paoname + '->' + yps.paoname as PARENT, ypsb.paobjectid from yukonpaobject ypa, yukonpaobject yps, ";
        subBusQuery += "yukonpaobject ypsb, ccsubareaassignment sa, ccsubstationsubbuslist ss where ypsb.paobjectid = ss.substationbusid ";
        subBusQuery += "and yps.paobjectid = ss.substationid and sa.substationbusid = ss.substationid and sa.areaid = ypa.paobjectid ";
        subBusQuery += "and ypsb.type like 'CCSUBBUS') as pInfo where type = 'CCSUBBUS' and disableflag = 'Y' ";
        subBusQuery += "and yp.paobjectid = pInfo.paobjectid order by type ";
        
        String feederQuery = "select yp.paoname as devicename, yp.type as deviceType, pInfo.parent as deviceParent from yukonpaobject yp, ";
        feederQuery += "(select ypa.paoname + '->' + yps.paoname + '->' + ypsb.paoname as PARENT, ypf.paobjectid from yukonpaobject ypa, ";
        feederQuery += "yukonpaobject yps, yukonpaobject ypsb, yukonpaobject ypf, ccsubareaassignment sa, ccsubstationsubbuslist ss, ";
        feederQuery += "ccfeedersubassignment fs where ypf.paobjectid = fs.feederid and ypsb.paobjectid = fs.substationbusid ";
        feederQuery += "and yps.paobjectid = ss.substationid and fs.substationbusid = ss.substationbusid and sa.substationbusid = ss.substationid ";
        feederQuery += "and sa.areaid = ypa.paobjectid and ypf.type like 'CCFEEDER') as pInfo where type = 'CCFEEDER' and disableflag = 'Y' ";
        feederQuery += "and yp.paobjectid = pInfo.paobjectid order by type ";
        
        String capBankQuery = "select yp.paoname as devicename, yp.type as deviceType, pInfo.parent as deviceParent from yukonpaobject yp, ";
        capBankQuery += "(select ypa.paoname + '->' + yps.paoname + '->' + ypsb.paoname + '->' + ypf.paoname as PARENT, ypc.paobjectid ";
        capBankQuery += "from yukonpaobject ypa, yukonpaobject yps, yukonpaobject ypsb, yukonpaobject ypf, yukonpaobject ypc, ";
        capBankQuery += "ccsubareaassignment sa, ccsubstationsubbuslist ss, ccfeedersubassignment fs, ccfeederbanklist fb, capbank c ";
        capBankQuery += "where ypc.paobjectid = c.deviceid and fb.deviceid = c.deviceid and fb.feederid = fs.feederid and ypf.paobjectid = fb.feederid ";
        capBankQuery += "and ypsb.paobjectid = fs.substationbusid and yps.paobjectid = ss.substationid and fs.substationbusid = ss.substationbusid ";
        capBankQuery += "and sa.substationbusid = ss.substationid and sa.areaid = ypa.paobjectid and ypc.type like 'CAP BANK') as pInfo ";
        capBankQuery += "where type = 'CAP BANK' and disableflag = 'Y' and pInfo.paobjectid = yp.paobjectid order by type ";
        
        String cbcQuery = "select yp.paoname as devicename, yp.type as deviceType, pInfo.parent as deviceParent from yukonpaobject yp, (select ypa.paoname + '->' + yps.paoname + '->' ";
        cbcQuery += "+ ypsb.paoname + '->' + ypf.paoname + '->' + ypc.paoname as PARENT, yp.paobjectid from yukonpaobject yp , yukonpaobject ypa, ";
        cbcQuery += "yukonpaobject yps, yukonpaobject ypsb, yukonpaobject ypf, yukonpaobject ypc, ccsubareaassignment sa, ccsubstationsubbuslist ss, ";
        cbcQuery += "ccfeedersubassignment fs, ccfeederbanklist fb, capbank c where yp.paobjectid = c.controldeviceid and ypc.paobjectid = c.deviceid ";
        cbcQuery += "and fb.deviceid = c.deviceid and fb.feederid = fs.feederid and ypf.paobjectid = fb.feederid and ypsb.paobjectid = fs.substationbusid ";
        cbcQuery += "and yps.paobjectid = ss.substationid and fs.substationbusid = ss.substationbusid and sa.substationbusid = ss.substationid ";
        cbcQuery += "and sa.areaid = ypa.paobjectid and yp.type like 'CBC%') as pInfo where type = 'CBC %' and disableflag = 'Y' ";
        cbcQuery += "and yp.paobjectid = pInfo.paobjectid order by type ";
        
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
